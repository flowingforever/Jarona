package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.game.Game;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StatisticManager {

    private final DatabaseManager db;
    private final PlayerDataAccess pda;
    private final StatisticWriteBuffer buffer;
    private final Map<NamespacedKey, List<StatisticDefinition>> definitions = new HashMap<>();

    public StatisticManager(DatabaseManager db, PlayerDataAccess pda, StatisticWriteBuffer buffer) {
        this.db = db;
        this.pda = pda;
        this.buffer = buffer;
    }

    public void registerDefinitions(Game game) {
        definitions.put(game.getKey(), game.getStatDefinitions());
    }

    public CompletableFuture<Void> ensurePlayerTracked(Player player) {
        return ensurePlayerTracked(player.getUniqueId(), player.getName());
    }

    public CompletableFuture<Void> ensurePlayerTracked(UUID uuid, String username) {
        return CompletableFuture.runAsync(() -> pda.ensureTracked(uuid, username));
    }

    public CompletableFuture<Void> incrementStatistic(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey, long amount) {
        if (isStatUsable(gameKey, statKey)) {
            return CompletableFuture.runAsync(() -> buffer.queueIncrement(uuid, gameKey, statKey, amount));
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> setStatistic(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey, long value) {
        if (isStatUsable(gameKey, statKey)) {
            return CompletableFuture.runAsync(() -> buffer.queueSet(uuid, gameKey, statKey, value));
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Long> getStatistic(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        if (isStatUsable(gameKey, statKey)) {
            return CompletableFuture.supplyAsync(() -> {
                long dbValue = read(uuid, gameKey, statKey);
                long pending = buffer.peekIncrement(uuid, gameKey, statKey);
                var pendingSet = buffer.peekSet(uuid, gameKey, statKey);
                return pendingSet != null ? pendingSet : dbValue + pending;
            });
        }
        return null;
    }

    public CompletableFuture<Map<NamespacedKey, Long>> getAllStats(UUID uuid, NamespacedKey gameKey) {
        return CompletableFuture.supplyAsync(() -> {
            var result = new LinkedHashMap<NamespacedKey, Long>();
            var definitionList = definitions.get(gameKey);
            if (definitionList != null) {
                for (var def : definitionList) {
                    result.put(def.key(), def.defaultValue());
                }
            }
            var sql = "SELECT stat_key, stat_value FROM game_stats WHERE uuid = ? AND game_id = ?";
            try (var ps = db.getConnection().prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, gameKey.toString());
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.put(NamespacedKey.fromString(rs.getString("stat_key")), rs.getLong("stat_value"));
                    }
                }
            } catch (SQLException _) {}

            if (definitionList != null) {
                for (var def : definitionList) {
                    long pending = buffer.peekIncrement(uuid, gameKey, def.key());
                    var pendingSet = buffer.peekSet(uuid, gameKey, def.key());
                    if (pendingSet != null) {
                        result.put(def.key(), pendingSet);
                    } else if (pending != 0) {
                        result.merge(def.key(), pending, Long::sum);
                    }
                }
            }
            return result;
        });
    }

    private long read(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        var sql = "SELECT stat_value FROM game_stats WHERE uuid = ? AND game_id = ? AND stat_key = ?";
        try (var ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, gameKey.toString());
            ps.setString(3, statKey.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("stat_value");
            }
        } catch (SQLException _) {}
        return getDefaultValue(gameKey, statKey);
    }

    public void write(StatisticWriteBuffer.Batch batch) {
        if (batch.increments().isEmpty() && batch.sets().isEmpty()) return;

        var connection = db.getConnection();
        var incrementSQL = """
                INSERT INTO game_stats (uuid, game_id, stat_key, stat_value)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid, game_id, stat_key) DO UPDATE SET stat_value = stat_value + excluded.stat_value
                """;
        var setSQL = """
                INSERT INTO game_stats (uuid, game_id, stat_key, stat_value)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid, game_id, stat_key) DO UPDATE SET stat_value = excluded.stat_value
                """;

        try {
            connection.setAutoCommit(false);

            try (var incStatement = connection.prepareStatement(incrementSQL);
                 var setStatement = connection.prepareStatement(setSQL)) {

                for (var entry : batch.increments().entrySet()) {
                    String[] parts = entry.getKey().split(",", 3);
                    incStatement.setString(1, parts[0]);
                    incStatement.setString(2, parts[1]);
                    incStatement.setString(3, parts[2]);
                    incStatement.setLong(4, entry.getValue().get());
                    incStatement.addBatch();
                }

                for (var entry : batch.sets().entrySet()) {
                    String[] parts = entry.getKey().split(",", 3);
                    setStatement.setString(1, parts[0]);
                    setStatement.setString(2, parts[1]);
                    setStatement.setString(3, parts[2]);
                    setStatement.setLong(4, entry.getValue());
                    setStatement.addBatch();
                }

                if (!batch.increments().isEmpty()) incStatement.executeBatch();
                if (!batch.sets().isEmpty()) setStatement.executeBatch();

            }

            connection.commit();
        } catch (SQLException _) {
            try {
                connection.rollback();
            } catch (SQLException _) {}
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException _) {}
        }
    }

    private boolean isStatUsable(NamespacedKey gameKey, NamespacedKey statKey) {
        var definitionList = definitions.get(gameKey);
        if (definitionList == null) {
            return false;
        }
        return definitionList.stream().anyMatch(d -> d.key().equals(statKey));
    }

    private long getDefaultValue(NamespacedKey gameKey, NamespacedKey statKey) {
        var definitionList = definitions.get(gameKey);
        if (definitionList == null) return 0L;
        return definitionList.stream()
                .filter(d -> d.key().equals(statKey))
                .findFirst()
                .map(StatisticDefinition::defaultValue)
                .orElse(0L);
    }

}
