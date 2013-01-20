package me.itidez.plugins.icreative.cmdblock;

import static me.itidez.plugins.icreative.utils.Locale.L;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.itidez.plugins.icreative.main.Core;
import me.itidez.plugins.icreative.main.LCPlayer;
import me.itidez.plugins.icreative.main.Perms;
import me.itidez.plugins.icreative.main.Players;

public class CommandBlocker {
    private Core plugin;
    public CommandBlocker(Core plugin) {
        this.plugin = plugin;
        
        plugin.getServer().getPluginManager().registerEvents(new Listener(), plugin);
    }
    
    
    class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onPreCommand(PlayerCommandPreprocessEvent event) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                String cmd = event.getMessage();
                if (cmd.startsWith("/")) { // just to be sure ;)
                    cmd = cmd.substring(1);
                    for (ICmdBlockEntry blockentry : plugin.config.getCommandBlockList()) {
                        if (blockentry.test(cmd)) {
                            LCPlayer player = Players.get(event.getPlayer());
                            if (!player.hasPermission(Perms.CmdBlock.ALL)) {
                                Core.debug("CmdBlock: "+event.getPlayer().getName()+": '/"+cmd+"' blocked by rule '"+blockentry.toString()+"'");
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(L("cmdblock.blocked"));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
