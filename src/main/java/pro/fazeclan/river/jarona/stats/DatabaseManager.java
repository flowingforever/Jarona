package pro.fazeclan.river.jarona.stats;

import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    @Getter
    private Connection connection;
    private final File dbFile;

    public DatabaseManager(File dataFolder) {
        this.dbFile = new File(dataFolder, "player_stats.db");
    }

    public void connect() {
        try {
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement pragma = connection.createStatement()) {
                pragma.execute("PRAGMA journal_mode=WAL;");
                pragma.execute("PRAGMA foreign_keys=ON;");
            }

            initTables();
        } catch (SQLException _) {}
    }

    private void initTables() throws SQLException  {
        var players = """
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL
                );
                """;

        var gameStats = """
                CREATE TABLE IF NOT EXISTS game_stats (
                    uuid TEXT NOT NULL,
                    game_id TEXT NOT NULL,
                    stat_key TEXT NOT NULL,
                    stat_value INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (uuid, game_id, stat_key),
                    FOREIGN KEY (uuid) REFERENCES players(uuid)
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(players);
            statement.execute(gameStats);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException _) {}
    }

}
