package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.game.Game;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AchievementManager {

    private final DatabaseManager db;
    private final PlayerDataAccess pda;
    private final AchievementWriteBuffer buffer;
    private final Map<NamespacedKey, List<AchievementDefinition>> definitions = new HashMap<>();

    public AchievementManager(DatabaseManager db, PlayerDataAccess pda, AchievementWriteBuffer buffer) {
        this.db = db;
        this.pda = pda;
        this.buffer = buffer;
    }

    public void registerDefinitions(Game game) {
        definitions.put(game.getKey(), game.getAchievementDefinitions());
    }

    public CompletableFuture<Void> ensurePlayerTracked(Player player) {
        return ensurePlayerTracked(player.getUniqueId(), player.getName());
    }

    public CompletableFuture<Void> ensurePlayerTracked(UUID uuid, String username) {
        return CompletableFuture.runAsync(() -> pda.ensureTracked(uuid, username));
    }

    public CompletableFuture<Void> giveAchievement(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        if (isAchievementObtainable(gameKey, achKey)) {
            return getAchievementProgress(uuid, gameKey, achKey).thenAcceptAsync(value -> {
                if (value < 1) {
                    buffer.queueGrant(uuid, gameKey, achKey);
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> giveAchievementProgress(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey, long amount) {
        if (isAchievementObtainable(gameKey, achKey)) {
            var def = getDefinition(gameKey, achKey);
            return getAchievementProgress(uuid, gameKey, achKey).thenAcceptAsync(value -> {
                long remaining = def.max() - amount - value;
                if (remaining < 0) {
                    buffer.queueGrantProgress(uuid, gameKey, achKey, def.max() - value);
                } else {
                    buffer.queueGrantProgress(uuid, gameKey, achKey, amount);
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Long> getAchievementProgress(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        if (isAchievementObtainable(gameKey, achKey)) {
            return CompletableFuture.supplyAsync(() -> read(uuid, gameKey, achKey) + buffer.peekGrant(uuid, gameKey, achKey));
        }
        return null;
    }

    public CompletableFuture<Boolean> hasCompletedAchievement(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        if (isAchievementObtainable(gameKey, achKey)) {
            var def = getDefinition(gameKey, achKey);
            return CompletableFuture.supplyAsync(() -> read(uuid, gameKey, achKey) + buffer.peekGrant(uuid, gameKey, achKey) > def.max());
        }
        return null;
    }

    public CompletableFuture<Map<NamespacedKey, Long>> getAllAchievementProgress(UUID uuid, NamespacedKey gameKey) {
        return CompletableFuture.supplyAsync(() -> {
            var result = new LinkedHashMap<NamespacedKey, Long>();
            var definitionList = definitions.get(gameKey);
            if (definitionList != null) {
                for (var def : definitionList) {
                    result.put(def.key(), def.min());
                }
            }
            var sql = "SELECT achievement_key, achievement_progress FROM achievements WHERE uuid = ? AND game_id = ?";
            try (var ps = db.getConnection().prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, gameKey.toString());
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.put(NamespacedKey.fromString(rs.getString("achievement_key")), rs.getLong("achievement_progress"));
                    }
                }
            } catch (SQLException _) {}

            if (definitionList != null) {
                for (var def : definitionList) {
                    long pending = buffer.peekGrant(uuid, gameKey, def.key());
                    if (pending != 0) {
                        result.merge(def.key(), pending, Long::sum);
                    }
                }
            }

            return result;
        });
    }

    private long read(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        var sql = "SELECT achievement_progress FROM achievements WHERE uuid = ? AND game_id = ? AND achievement_key = ?";
        try (var ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, gameKey.toString());
            ps.setString(3, achKey.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("achievement_progress");
            }
        } catch (SQLException _) {}
        return 0L;
    }

    public void write(AchievementWriteBuffer.Batch batch) {
        if (batch.grants().isEmpty()) return;

        var connection = db.getConnection();
        var sql = """
                INSERT INTO achievements (uuid, game_id, achievement_key, achievement_progress)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid, game_id, achievement_key) DO UPDATE SET achievement_progress = achievement_progress + excluded.achievement_progress
                """;

        try {
            connection.setAutoCommit(false);

            try (var statement = connection.prepareStatement(sql)) {

                for (var entry : batch.grants().entrySet()) {
                    String[] parts = entry.getKey().split(",", 3);
                    statement.setString(1, parts[0]);
                    statement.setString(2, parts[1]);
                    statement.setString(3, parts[2]);
                    statement.setLong(4, entry.getValue().get());
                    statement.addBatch();
                }

                if (!batch.grants().isEmpty()) statement.executeBatch();

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

    private boolean isAchievementObtainable(NamespacedKey gameKey, NamespacedKey achKey) {
        var definitionList = definitions.get(gameKey);
        if (definitionList == null) {
            return false;
        }
        return definitionList.stream().anyMatch(d -> d.key().equals(achKey));
    }

    private AchievementDefinition getDefinition(NamespacedKey gameKey, NamespacedKey achKey) {
        var definitionList = definitions.get(gameKey);
        if (definitionList == null) {
            return null;
        }
        return definitionList.stream()
                .filter(d -> d.key().equals(achKey))
                .findFirst()
                .orElse(null);
    }

}
