package me.itidez.plugins.icreative;

import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 *
 * @author iTidez
 */
public class PlayerListener implements Listener{

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        Item item = event.getItem();
        List<MetadataValue> meta = item.getMetadata("iCreative");
        int i = 0;
        while(meta.iterator().hasNext()) {
            MetadataValue metaValue = meta.iterator().next();
            if(metaValue.asString().equalsIgnoreCase("CRTProt")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onItemSpawned(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        if(Icreative.creativeUsers.contains(player)) {
            item.setMetadata("iCreative", new FixedMetadataValue(Icreative.instance, "CRTProt"));
        }
    }
}
