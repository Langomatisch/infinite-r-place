package de.langomatisch.infiniterplace.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.langomatisch.infiniterplace.api.data.Grid;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class MySQLDatabase {

    private final static int MAX_SIZE = 128;

    private final static Material[] MATERIALS = Material.values();

    private final HikariDataSource pool;

    public MySQLDatabase(SQLCredentials credentials) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase());
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.setMinimumIdle(16);
        config.setMaximumPoolSize(128);
        pool = new HikariDataSource(config);
        System.out.println("connected to database");
    }

    public void closeConnection() {
        pool.close();
    }

    // FIXME make this a completable future and make it async
    public Grid loadGrid() throws SQLException {
        Connection connection = pool.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT x, y, material FROM grid");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("we got a result set with size " + resultSet.getFetchSize());
        Grid grid = new Grid(MAX_SIZE, MAX_SIZE, new Material[MAX_SIZE][MAX_SIZE]);
        while (resultSet.next()) {
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            Material material = MATERIALS[resultSet.getInt("material")];
            grid.getGrid()[x + 64][y + 64] = material;
            System.out.println("loaded material " + material + " at " + x + " " + y + "");
        }
        preparedStatement.close();
        return grid;
    }

    public CompletableFuture<Void> set(int x, int y, Material material) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = pool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO grid (x, y, material) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE material = ?");
                preparedStatement.setInt(1, x);
                preparedStatement.setInt(2, y);
                preparedStatement.setInt(3, material.ordinal());
                preparedStatement.setInt(4, material.ordinal());
                preparedStatement.execute();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

}
