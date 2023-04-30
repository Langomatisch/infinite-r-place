package de.langomatisch.infiniterplace.api.data;

import com.google.common.base.Preconditions;
import de.langomatisch.infiniterplace.InfiniteRPlacePlugin;
import de.langomatisch.infiniterplace.api.pubsub.PixelUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Grid {

    private int width;
    private int height;
    // [x][z]
    private Material[][] grid;

    public void rebuild(World world) {
        Preconditions.checkNotNull(world, "world is null");
        Preconditions.checkArgument(Bukkit.isPrimaryThread(), "not on main thread");
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(128);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(6000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setPVP(false);
        for (int x = 0; x < grid.length; x++) {
            Material[] row = grid[x];
            for (int z = 0; z < row.length; z++) {
                Material material = row[z];
                if (material == null) {
                    material = Material.STONE;
                }
                world.getBlockAt(x - 64, 100, z - 64).setType(material);
                world.getBlockAt(x - 64, 99, z - 64).setType(Material.STONE);
            }
        }
    }

    public void set(Player player, Location location, ItemStack itemStack) {
        // TODO: Logging
        int x = location.getBlockX();
        int z = location.getBlockZ();
        Material material = itemStack.getType();
        InfiniteRPlacePlugin.getInstance().getDatabase().set(x, z, material).whenComplete((unused, throwable) -> {
            System.out.println("block set at " + x + " " + z);
            InfiniteRPlacePlugin.getInstance().getRedisDatabase().publish(new PixelUpdate(x, z, material.ordinal()));
            Bukkit.getScheduler().runTask(InfiniteRPlacePlugin.getInstance(), () -> update(x, z, material));
        });
    }

    public void update(int x, int z, Material material) {
        Preconditions.checkArgument(Bukkit.isPrimaryThread(), "not on main thread");
        System.out.println("setting block " + x + " " + z + " to " + material);
        grid[x + 64][z + 64] = material;
        World world = InfiniteRPlacePlugin.getInstance().getWorld();
        world.getBlockAt(x, 100, z).setType(material);
    }

}
