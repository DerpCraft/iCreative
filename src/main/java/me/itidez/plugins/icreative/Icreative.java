package me.itidez.plugins.icreative;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Icreative extends JavaPlugin {
    private static Logger log = Logger.getLogger("Minecraft");
    private PluginDescriptionFile description;
    private static String prefix;
    private String version;
    private static boolean debug;
    public static Icreative instance;
    public static List<Player> creativeUsers;
    
    @Override
    public void onDisable() {
        //Null out the statics as to help ease load on running server
        log = null;
        prefix = null;
        debug = false;
        instance = null;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.description = getDescription();
        this.version = this.description.getVersion();
        prefix = ("[" + this.description.getName() + "] ");
        //TODO: Add configuration option to change this around
        debug = true;
        addCreativeUsers();
        //Start adding Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        log("Loaded iCreative");
        debug("Debug Enabled!");
    }
    
    public static void log( String message) {
        log.log(Level.INFO, "{0}{1}", new Object[]{prefix, message});
    }
    
    public static void debug( String message) {
        if(debug)
            log.log(Level.INFO, "{0}{1}", new Object[]{prefix, message});
    }
    
    public void addCreativeUsers() {
        for(Player player : getServer().getOnlinePlayers()) {
            if(player.getGameMode().equals(GameMode.CREATIVE) && !(player.isOp())) {
                creativeUsers.add(player);
            }
        }
    }
}

