package de.langomatisch.infiniterplace.listener;

import de.langomatisch.infiniterplace.InfiniteRPlacePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final InfiniteRPlacePlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(new Location(plugin.getWorld(), 0, 101, 0));
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage("Welcome to InfiniteRPlace!");
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        if (itemStack.getItemMeta() instanceof MapMeta mapMeta) {
            mapMeta.setMapView(Bukkit.getMap(0));
            itemStack.setItemMeta(mapMeta);
        }
        inventory.setItemInOffHand(itemStack);
    }

}
