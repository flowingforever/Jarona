package pro.fazeclan.river.jarona.queue;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.SchedulingUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueManager {

    private final Map<UUID, NamespacedKey> playerQueueMap = new HashMap<>();
    private final Set<NamespacedKey> gamesStarting = new HashSet<>();

    public List<Player> getPlayersQueued(Game game) {
        return getPlayersQueued(game.getKey());
    }

    public List<Player> getPlayersQueued(NamespacedKey gameKey) {
        return playerQueueMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), gameKey))
                .map(Map.Entry::getKey)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
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

    public boolean areEnoughPlayersQueued(Game game) {
        return getPlayersQueued(game.getKey()).size() >= game.getMinimumPlayers();
    }

    public void queueGameToStart(Game game) {
        gamesStarting.add(game.getKey());
    }

    public void unqueueGameToStart(Game game) {
        gamesStarting.remove(game.getKey());
    }

    public boolean isGameQueuedToStart(Game game) {
        return gamesStarting.contains(game.getKey());
    }

    public void startGameQueueLoop(Game game) {
        if (!isGameQueuedToStart(game) && areEnoughPlayersQueued(game)) {
            var config = Jarona.getInstance().getConfig();
            queueGameToStart(game);

            int initialTime = config.getInt("start-wait-period", 15) * 20;
            AtomicInteger tick = new AtomicInteger(initialTime);
            SchedulingUtil.interval(0L, 1L, () -> {
                if (!areEnoughPlayersQueued(game)) {
                    unqueueGameToStart(game);
                    return false;
                }

                if (tick.get() == initialTime) {
                    Bukkit.broadcast(ServerUtil.formatComponent(
                            "<green>" + game.getName() + " is starting in " + (tick.get() / 20) + " seconds!</green>"
                    ));
                }

                if (tick.get() == 100) {
                    Bukkit.broadcast(ServerUtil.formatComponent(
                            "<green>" + game.getName() + " is starting in 5 seconds!</green>"
                    ));
                }

                if (tick.get() == 60) {
                    Bukkit.broadcast(ServerUtil.formatComponent(
                            "<green>" + game.getName() + " is starting in 3 seconds!</green>"
                    ));
                }

                if (tick.get() == 40) {
                    Bukkit.broadcast(ServerUtil.formatComponent(
                            "<green>" + game.getName() + " is starting in 2 seconds!</green>"
                    ));
                }

                if (tick.get() == 20) {
                    Bukkit.broadcast(ServerUtil.formatComponent(
                            "<green>" + game.getName() + " is starting in 1 second!</green>"
                    ));
                }

                if (tick.get() == 0) {
                    GameUtil.startGameWithRandomMap(game.getKey());
                    return false;
                }

                tick.getAndDecrement();

                return true;
            });
        }
    }

}
