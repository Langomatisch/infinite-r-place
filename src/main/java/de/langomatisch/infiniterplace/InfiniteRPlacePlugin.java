package de.langomatisch.infiniterplace;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import de.langomatisch.infiniterplace.api.data.Grid;
import de.langomatisch.infiniterplace.api.database.MySQLDatabase;
import de.langomatisch.infiniterplace.api.database.SQLCredentials;
import de.langomatisch.infiniterplace.api.pubsub.*;
import de.langomatisch.infiniterplace.listener.BlockPlaceListener;
import de.langomatisch.infiniterplace.listener.PlayerJoinListener;
import lombok.Getter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialMapColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import oshi.software.os.linux.LinuxOSThread;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Plugin(name = "InfiniteRPlace", version = "1.0")
@ApiVersion(ApiVersion.Target.v1_19)
public class InfiniteRPlacePlugin extends JavaPlugin {


    public final static Material[] MATERIALS = Material.values();

    @Getter
    private static InfiniteRPlacePlugin instance;

    private MySQLDatabase database;
    private RedisDatabase redisDatabase;
    private World world;
    private Grid grid;

    @Override
    public void onEnable() {
        instance = this;

        world = Bukkit.createWorld(new WorldCreator("place"));
        Preconditions.checkNotNull(world, "world is null");

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);

        // FIXME: do correct configuration stuff
        database = new MySQLDatabase(new SQLCredentials(
                "localhost",
                3306,
                "infiniterplace",
                "root",
                ""
        ));

        try {
            grid = database.loadGrid();
            grid.rebuild(world);
            System.out.println("loaded grid with size " + grid.getGrid().length);
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }

        redisDatabase = new RedisDatabase(new RedisCredentials(
                "redis-15070.c135.eu-central-1-1.ec2.cloud.redislabs.com", 15070, "bLUkI4T4eAmjwkTQOYOxgiabJ4qxvtLE"
        ));

        redisDatabase.subscribe(PixelUpdate.class, (channel, message) -> {
            System.out.println("received message " + message);
            Bukkit.getScheduler().runTask(this, () ->
                    grid.update(message.getX(), message.getY(), MATERIALS[message.getMaterial()]));
        });

    }

    private void exportColors() {
        try (FileWriter fileWriter = new FileWriter("colors.json")) {
            final Map<Integer, String> mapping = new HashMap<>();
            for (Material value : Material.values()) {
                if (!value.isBlock()) {
                    mapping.put(value.ordinal(), "#ffffff");
                    continue;
                }
                net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(new ItemStack(value));
                Block a = Block.a(itemStack.d().a());
                MaterialMapColor s = a.s();
                if (s == null) continue;
                int al = s.ak;
                Color color = Color.fromRGB(al);
                String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                mapping.put(value.ordinal(), hex);
                System.out.println(value.name() + " " + al);
            }
            new Gson().toJson(mapping.values(), fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record WrappedColor(String hex) {

    }

    @Override
    public void onDisable() {
        database.closeConnection();
    }

}
