package me.itidez.plugins.icreative;

import static me.itidez.plugins.utils.Locale.L;
import static me.itidez.plugins.utils.Util.copyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import me.itidez.plugins.icreative.cmdblock.ICmdBlockEntry;
import me.itidez.plugins.icreative.cmdblock.RegexpBlockEntry;
import me.itidez.plugins.icreative.cmdblock.StringBlockEntry;
import me.itidez.plugins.icreative.store.InvYamlStorage;
import me.itidez.plugins.icreative.store.PlayerInventoryStorage;

public class Configuration {
    private FileConfiguration c;
    private File file;
    public static Core plugin;
    
    public enum Option {
        STORECREATIVE("store.creative", true),
        CREATIVEARMOR("store.armor.enabled", true),
        ADVENTUREINV("store.adventure", false),
        REGION_OPTIONAL("region.optional", true),
        REGION_REMEMBER("region.remember", false),
        BLOCKPICKUP("limit.pickup", true),
        BLOCKSIGN("limit.sign", true),
        BLOCKBUTTON("limit.button", false),
        BLOCKDAMAGEMOB("limit.damagemob", false),
        BLOCKBENCHES("limit.workbench", false),
        REMOVEDROP("limit.remove_drops", true),
        REMOVEPICKUP("limit.remove_pickup", false),
        PERM_WEPIF("permissions.wepif", true),
        CMDBLOCKER("cmdblocker.enabled", true),
        DEBUG("debug", false);
        
        private String key;
        private boolean _default;
        private Option(String key, boolean def) {
            this.key = key;
            this._default = def;
        }
        public String getKey() {
            return key;
        }
        public boolean getDefault() {
            return _default;
        }
        public static List<Option> getAvailableOptions() {
            List<Option> ret = new ArrayList<Option>(Arrays.asList(Option.values()));
            ret.remove(Option.DEBUG); // keep it undocumented ;)
            return ret;
        }
    }
    
    public Configuration(Core pplugin) {
        plugin = pplugin;
        
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists())
            //plugin.saveDefaultConfig();
            copyFile(plugin.getResource("config.yml"), file);
        
        c = plugin.getConfig();
    }
    
    public void set(Option opt, boolean value) {
        /*if (!opt.isSetAble())
            throw new IllegalArgumentException("Setting this option is not allowed");*/
        this.reload();
        c.set(opt.getKey(), value);
        this.save();
    }
    
    public boolean getBoolean(Option opt) {
        return c.getBoolean(opt.getKey(), opt.getDefault());
    }

    /**
     * Intended to be undocumented ;)
     */
    public boolean getDebug() {
        return this.getBoolean(Option.DEBUG);
    }
    
    public boolean getStoreEnabled() {
        return getTempStoreEnabled() && c.getBoolean("store.enabled", true);
    }
    public boolean getLimitEnabled() {
        return c.getBoolean("limit.enabled", true);
    }
    public boolean getRegionEnabled() {
        return c.getBoolean("region.enabled", true);
    }
    
    public boolean getStoreCreative() {
        return this.getBoolean(Option.STORECREATIVE);
    }
    /*public boolean getUnsafeStorage() {
        return c.getBoolean("store.unsafe", false);
    }*/
    public String getInventoryFolder() {
        return c.getString("store.folder", "inventories");
    }
    public boolean getAdventureInv() {
    	return this.getBoolean(Option.ADVENTUREINV);
    }
    public boolean getBlockPickupInCreative() {
        return this.getBoolean(Option.BLOCKPICKUP);
    }
    public boolean getSignBlock() {
        return this.getBoolean(Option.BLOCKSIGN);
    }
    public boolean getBenchBlock() {
        return this.getBoolean(Option.BLOCKBENCHES);
    }
    public boolean getButtonBlock() {
        return this.getBoolean(Option.BLOCKBUTTON);
    }
    public boolean getRemoveDrop() {
        return this.getBoolean(Option.REMOVEDROP);
    }
    public boolean getRemovePickup() {
        return this.getBoolean(Option.REMOVEPICKUP);
    }
    public boolean getMobDamageBlock() {
        return this.getBoolean(Option.BLOCKDAMAGEMOB);
    }
    
    public boolean getWEPIFEnabled() {
    	return this.getBoolean(Option.PERM_WEPIF);
    }
    public boolean getRegionOptional() {
        return this.getBoolean(Option.REGION_OPTIONAL);
    }
    public boolean getRegionRememberOptional() {
        return this.getRegionOptional() && this.getBoolean(Option.REGION_REMEMBER);
    }
    
    public String getLocale() {
        if (c.contains("locale") && c.getString("locale") != "none")
            return c.getString("locale");
        return null;
    }

    protected void reload() {
        _block_break = null;
        _block_use = null;
        plugin.reloadConfig();
        c = plugin.getConfig();
    }
    protected void save() {
        plugin.saveConfig();
    }
    
    private List<BlackList> _block_break = null;
    private List<BlackList> _block_use = null;
    
    public List<BlackList> getBlockedBreaks() {
        if (_block_break == null)
            _block_break = parseMaterialList(c.getStringList("limit.break"));
        return _block_break;
    }
    
    public List<BlackList> getBlockedUse() {
        if (_block_use == null)
            _block_use = parseMaterialList(c.getStringList("limit.use"));
        return _block_use;
    }
    
    private List<BlackList> parseMaterialList(List<String> s) {
        List<BlackList> list = new ArrayList<BlackList>();
        if (s != null) {
            for (String m : s) {
                if (m.equals("*")) {
                    list.clear();
                    list.add(new BlackList.All());
                    break;
                } else {
                    MaterialData md = parseMaterial(m);
                    if (md != null)
                        list.add(new BlackList.Some(md));
                }
            }
        }
        return list;
    }
    
    private MaterialData parseMaterial(String m) {
        int d = -1;
        if (m.contains(":")) {
            String[] t = m.split(":");
            m = t[0];
            try {
                d = Integer.parseInt(t[1]);
            } catch (NumberFormatException ex) {
                // TODO: try to find the data value by 
                if (d == -1)
                    plugin.warn(L("exception.config.materiak_data_not_found", t[1]));
            }
        }
        Material e = null;
        try {
            e = Material.getMaterial(Integer.parseInt(m));
        } catch (NumberFormatException ex) {
            e = Material.matchMaterial(m);
        }
        if (e == null) {
            plugin.warn(L("exception.config.material_not_found", m));
            return null;
        }
        if (d != -1)
            return new MaterialData(e, (byte) d);
        else
            return new MaterialData(e);
    }
    
    private boolean _store_enabled = true;
    public void setTempStoreEnabled(boolean b) {
        _store_enabled = b;
    }
    public boolean getTempStoreEnabled() {
        return _store_enabled;
    }

    /**
     * @return Milliseconds
     */
    public long getRepeatingMessageTimeout() {
        return 10000; // 10 sec. limit
    }

    /**
     * The maximum height a player may fall down by leaving creative gamemode 
     * @return Block-Height
     */
    public int getMaximumFloatingHeight() {
        return 3;
    }
    
    public PlayerInventoryStorage getInvetoryStorage() {
        return new InvYamlStorage(new File(plugin.getDataFolder(), getInventoryFolder()));
    }

    public Map<String, MaterialData> getCreativeArmor() {
        if (c.contains("store.armor") && c.isConfigurationSection("store.armor")) {
            ConfigurationSection sect = c.getConfigurationSection("store.armor");
            if (sect.getBoolean("enabled")) {
                Map<String, MaterialData> armor = new HashMap<String, MaterialData>();
                for (Map.Entry<String, Object> entry : sect.getValues(false).entrySet()) {
                    if (!entry.getKey().equals("enabled")) {
                        MaterialData md = parseMaterial((String) entry.getValue());
                        if (md != null)
                            armor.put(entry.getKey(), md);
                    }
                }
                return armor;
            }
        }
        return null;
    }
    
    public boolean getCommandBlockerEnabled() {
        return this.getBoolean(Option.CMDBLOCKER);
    }
    
    private List<ICmdBlockEntry> _blocklist = null;
    public List<ICmdBlockEntry> getCommandBlockList() {
         if (_blocklist == null) {
             _blocklist = new ArrayList<ICmdBlockEntry>();
             for (String cmd : c.getStringList("cmdblock.commands")) {
                 if (cmd.startsWith("^")) {
                     _blocklist.add(new RegexpBlockEntry(cmd));
                 } else {
                     _blocklist.add(new StringBlockEntry(cmd));
                 }
             }
         }
         return _blocklist;
    }
}
