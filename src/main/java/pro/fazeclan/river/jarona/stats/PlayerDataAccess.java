package pro.fazeclan.river.jarona.stats;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataAccess {

    private final DatabaseManager db;

    public PlayerDataAccess(DatabaseManager db) {
        this.db = db;
    }

    public void ensureTracked(UUID uuid, String username) {
        var sql = """
                INSERT INTO players (uuid, username) VALUES (?, ?)
                ON CONFLICT(uuid) DO UPDATE SET username = excluded.username
                """;
        try (var ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException _) {}
    }

}
