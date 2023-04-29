package de.langomatisch.infiniterplace;

import com.google.common.base.Preconditions;
import de.langomatisch.infiniterplace.api.data.Grid;
import de.langomatisch.infiniterplace.api.database.MySQLDatabase;
import de.langomatisch.infiniterplace.api.database.SQLCredentials;
import de.langomatisch.infiniterplace.listener.BlockPlaceListener;
import de.langomatisch.infiniterplace.listener.PlayerJoinListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Getter
@Plugin(name = "InfiniteRPlace", version = "1.0")
@ApiVersion(ApiVersion.Target.v1_19)
public class InfiniteRPlacePlugin extends JavaPlugin {

    @Getter
    private static InfiniteRPlacePlugin instance;

    private MySQLDatabase database;
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
    }

    @Override
    public void onDisable() {
        database.closeConnection();
    }

}
