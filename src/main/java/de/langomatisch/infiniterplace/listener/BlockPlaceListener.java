package de.langomatisch.infiniterplace.listener;

import de.langomatisch.infiniterplace.InfiniteRPlacePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class BlockPlaceListener implements Listener {

    private final InfiniteRPlacePlugin plugin;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
        ItemStack itemInHand = event.getItemInHand();
        if (!itemInHand.getType().isBlock()) return;
        plugin.getGrid().set(event.getPlayer(), event.getBlock().getLocation(), itemInHand);
    }

    @EventHandler
    public void onStuff(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(EntityBlockFormEvent event) {
        System.out.println("entity block form event " + event.getBlock().getType().name());
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }

    @EventHandler
    public void onStuff(EntityDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(BlockDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStuff(BlockBurnEvent event) {
        event.setCancelled(true);
    }

}
