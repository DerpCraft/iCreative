package me.itidez.plugins.icreative;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.itidez.plugins.icreative.store.PlayerOptions;

public class Players {
    public static final long CLEANUP_TIMEOUT = 300000; // 300s = 5m
    private static Map<String, LCPlayer> players = new HashMap<String, LCPlayer>();
    private static PlayerOptions options = new PlayerOptions();
    
    public static LCPlayer get(Player player) {
        Core.debug("player: " + player.getName() + " - " + ((Object)player).hashCode() + " - " + player.getEntityId() + " - " + player.getUniqueId());
        if (!players.containsKey(player.getName())) {
            LCPlayer p = new LCPlayer(player);
            players.put(player.getName(), p);
            return p;
        } else {
            LCPlayer p = players.get(player.getName());
            if (player != p.getPlayer())
                p.updatePlayer(player);
            //p.touch();
            return p;
        }
    }
    
    public static LCPlayer get(String player) {
        if (players.containsKey(player)) {
            return players.get(player);
        }
        return null;
    }
    
    public static void remove(String player) {
        players.remove(player);
    }
    
    /*public static void clear(String player) {
        if (players.containsKey(player)) {
            LCPlayer p = players.get(player);
            p.updatePlayer(null);
            p.touch(); // keep meta data alive till cleanup, but remove player bukkit assoc.
        }
    }*/
    
    /*public static void cleanUp() {
        int count = players.size();
        Iterator<Map.Entry<String, LCPlayer>> i = players.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, LCPlayer> entry = i.next();
            if (entry.getValue().isOutdated()) {
                Core.debug("removing "+entry.getValue().getName());
                i.remove();
            }
        }
        Core.debug("cleanup done: player count: "+count+" / "+players.size());
    }*/

    public static PlayerOptions getOptions() {
        return options;
    }
}
