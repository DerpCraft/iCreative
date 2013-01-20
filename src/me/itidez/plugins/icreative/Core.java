package me.itidez.plugins.icreative;
import static me.itidez.plugins.utils.Locale.L;

import java.util.logging.Logger;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.itidez.plugins.icreative.integration.Communicator;
import me.itidez.plugins.icreative.cmdblock.CommandBlocker;
import me.itidez.plugins.icreative.listeners.LimitListener;
import me.itidez.plugins.icreative.listeners.MainListener;
import me.itidez.plugins.icreative.regions.WorldGuardIntegration;
import me.itidez.plugins.utils.Locale;
import me.itidez.plugins.utils.Permissions;


public class Core extends JavaPlugin {
    public final Logger logger = Logger.getLogger("Minecraft");
    public Configuration config;
    public Permissions perm;
    public WorldGuardIntegration worldguard;
    public Communicator com;
    public static Core plugin;
    public NoBlockItemSpawn spawnblock;
    public CommandBlocker cmdblock;

    @Override
    public void onDisable() {
        plugin.getServer().getScheduler().cancelTasks(this);
        if (worldguard != null)
            worldguard.unload();
        try {
            Locale.unload();
        } catch (NoClassDefFoundError e) {} // prevent unload issue
        
        plugin = null;
        worldguard = null;
        config = null;
        spawnblock = null;
        com = null;
        cmdblock = null;
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = new Configuration(this);
        perm = new Permissions(this);
        com = new Communicator(this);
        
        new Locale(this, config.getLocale());

        spawnblock = new NoBlockItemSpawn();
        
        // 1st Feature: Separated Inventories Storage
        if (config.getStoreEnabled() && getServer().getPluginManager().isPluginEnabled("MultiInv")) {
            warn(L("basic.conflict", "MultiInv", L("basic.feature.store")));
            config.setTempStoreEnabled(false);
        } else if (config.getStoreEnabled() && getServer().getPluginManager().isPluginEnabled("Multiverse-Inventories")) {
            warn(L("basic.conflict", "Multiverse-Inventories", L("basic.feature.store")));
            config.setTempStoreEnabled(false);
        }
        if (config.getStoreEnabled()) {
            com.hookAuthInvs();
        }
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
        
        // 2nd Feature: Creative Limitations (Restrictions)
        if (config.getLimitEnabled())
            getServer().getPluginManager().registerEvents(new LimitListener(this), this);
        
        // 3rd Feature: WorldGuard Region-Support
        if (config.getRegionEnabled() && getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            worldguard = new WorldGuardIntegration(this);
        } else if(config.getRegionEnabled()) {
            warn(L("basic.warning.worldguard_not_found", L("basic.feature.region")));
        }
        
        // 4th Feature: Command Blocker
        if (config.getCommandBlockerEnabled())
            cmdblock = new CommandBlocker(this);
        
        debug("Store: " + config.getStoreEnabled());
        debug("Limit: " + config.getLimitEnabled());
        debug("Region: " + (worldguard != null));
        debug("CmdBlock: " + config.getCommandBlockerEnabled());
        
        Commands.register(this);
        
        /*plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                Players.cleanUp();
            }
        }, Players.CLEANUP_TIMEOUT / 50L, Players.CLEANUP_TIMEOUT / 50L); // 50 = 1000ms / 20ticks*/
        
        PluginDescriptionFile df = this.getDescription();
        if (worldguard != null)
            logger.info("["+df.getName() + " v" + df.getVersion() + "] "+L("basic.loaded.worldguard"));
        else
            logger.info("["+df.getName() + " v" + df.getVersion() + "] "+L("basic.loaded.no_worldguard"));
    }
    
    public void reload() {
        getServer().getScheduler().cancelTasks(this);
        getServer().getServicesManager().unregisterAll(this);
        HandlerList.unregisterAll(this);
        setEnabled(false);
        setEnabled(true);
    }
    public void info(String s) {
        logger.info("["+this.getDescription().getName()+"] " + s);
    }
    public void warn(String s) {
        logger.warning("["+this.getDescription().getName()+"] " + s);
    }
    public void error(String s) {
        logger.severe("["+this.getDescription().getName()+"] " + s);
    }
    public static void debug(String s) {
        if (isDebug())
            plugin.info("DEBUG: " + s);
    }
    public static boolean isDebug() {
        return plugin.config.getDebug();
    }
}
