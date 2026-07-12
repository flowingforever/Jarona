package pro.fazeclan.river.jarona.queue;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.game.Game;

import java.util.*;

public class QueueManager {

    private final Map<UUID, NamespacedKey> playerQueueMap = new HashMap<>();

    public List<Player> getPlayersQueued(Game game) {
        return getPlayersQueued(game.getKey());
    }

    public List<Player> getPlayersQueued(NamespacedKey gameKey) {
        return playerQueueMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), gameKey))
                .map(Map.Entry::getKey)
                .map(Bukkit::getPlayer)
                .filter(Objects::isNull)
                .toList();
    }

    public List<UUID> getUUIDsQueued(Game game) {
        return getUUIDsQueued(game.getKey());
    }

    public List<UUID> getUUIDsQueued(NamespacedKey gameKey) {
        return playerQueueMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), gameKey))
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<Player> getAndRemovePlayersQueued(Game game) {
        var queued = getPlayersQueued(game);
        for (Player player : queued) {
            playerQueueMap.remove(player.getUniqueId());
        }
        return queued;
    }

    public void queuePlayer(Player player, Game game) {
        queuePlayer(player.getUniqueId(), game.getKey());
    }

    public void queuePlayer(UUID uuid, Game game) {
        queuePlayer(uuid, game.getKey());
    }

    public void queuePlayer(Player player, NamespacedKey gameKey) {
        queuePlayer(player.getUniqueId(), gameKey);
    }

    public void queuePlayer(UUID uuid, NamespacedKey gameKey) {
        playerQueueMap.put(uuid, gameKey);
    }

    public void unqueuePlayer(Player player) {
        playerQueueMap.remove(player.getUniqueId());
    }

    public void unqueuePlayer(UUID uuid) {
        playerQueueMap.remove(uuid);
    }

    public boolean isQueued(Player player, Game game) {
        return isQueued(player.getUniqueId(), game.getKey());
    }

    public boolean isQueued(UUID uuid, Game game) {
        return isQueued(uuid, game.getKey());
    }

    public boolean isQueued(Player player, NamespacedKey gameKey) {
        return isQueued(player.getUniqueId(), gameKey);
    }

    public boolean isQueued(UUID uuid, NamespacedKey gameKey) {
        var gameQueued = playerQueueMap.get(uuid);
        return gameQueued != null && gameQueued.equals(gameKey);
    }

}
