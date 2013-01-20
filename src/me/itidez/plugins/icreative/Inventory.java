package me.itidez.plugins.icreative;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.itidez.plugins.icreative.store.PlayerInventoryStorage;

public class Inventory {
    private static PlayerInventoryStorage storage = Core.plugin.config.getInvetoryStorage();
    //private static InvMemStorage tempinvs = new InvMemStorage();
    
    protected LCPlayer player;

    public enum Target {
        SURVIVAL,
        CREATIVE,
        ADVENTURE;
        
        public static Target getTarget(GameMode gm) {
            return Target.valueOf(gm.name());
        }
    }
    
    public Inventory(LCPlayer player) {
        this.player = player;
    }
    public LCPlayer getLCPlayer() {
        return player;
    }
    public Player getPlayer() {
        return player.getPlayer();
    }
    
    private PlayerInventory inv() {
        return player.getPlayer().getInventory();
    }
    
    public void save() {
        Core.debug(getPlayer().getName()+": store inventory: "+getPlayer().getGameMode());
        storage.store(this, Target.getTarget(getPlayer().getGameMode()));
    }
    public void save(GameMode gm) {
        Core.debug(getPlayer().getName()+": store inventory: "+gm);
        storage.store(this, Target.getTarget(gm));
    }
    
    public void load() {
        load(getPlayer().getGameMode());
    }
    
    public boolean isStored(GameMode gm) {
        return storage.contains(this, Target.getTarget(gm));
    }
    
    public void load(GameMode gm) {
        Core.debug(getPlayer().getName()+": load inventory: "+gm);
        try {
            storage.load(this, Target.getTarget(gm));
        } catch (IllegalArgumentException e) {
            //if (Core.plugin.config.getUnsafeStorage()) {
                throw e;
            //} else {
                //getPlayer().sendMessage(ChatColor.DARK_RED + L("exception.storage.load"));
            //}
        }
    }
    
    /*public void storeTemp() {
        Core.debug(getPlayer().getName()+": temp store inventory");
        tempinvs.store(this);
    }
    public void restoreTemp() {
        Core.debug(getPlayer().getName()+": temp restore inventory");
        tempinvs.load(this);
    }
    public void clearTemp() {
        Core.debug(getPlayer().getName()+": temp clear inventory");
        tempinvs.remove(this);
    }*/
    
    public void clear() {
        inv().setArmorContents(new ItemStack[0]);
        inv().clear();
    }
}
