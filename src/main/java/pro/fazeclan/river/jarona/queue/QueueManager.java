package pro.fazeclan.river.jarona.queue;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueManager {

    private final ConcurrentLinkedQueue<QueuedPlayer> playerQueue = new ConcurrentLinkedQueue<>();
    private BukkitTask queueCheckLoop;

    public void startLoop() {
        if (this.queueCheckLoop == null) {
            this.queueCheckLoop = new BukkitRunnable() {
                private final Map<Game, Integer> possibleGames = new HashMap<>(16);
                private final Jarona plugin = Jarona.getInstance();

                @Override
                public void run() {
                    var initialSeconds = plugin.getConfig().getInt("start-wait-period", 30);
                    for (var game : plugin.getGameManager().getRegistry().values()) {
                        if (areEnoughPlayersQueued(game)) {
                            possibleGames.compute(game, (_, integer) -> {
                                if (integer == null) {
                                    return initialSeconds;
                                } else {
                                    return integer - 1;
                                }
                            });
                        } else {
                            possibleGames.remove(game);
                        }
                    }

                    for (var entry : possibleGames.entrySet()) {
                        var seconds = entry.getValue();
                        var game = entry.getKey();
                        if (seconds == initialSeconds
                                || seconds == (initialSeconds / 2)
                                || seconds == 5
                                || seconds == 3
                                || seconds == 2
                                || seconds == 1) {
                            for (var player : GameUtil.getAllPlayersNotInGame()) {
                                player.sendMessage(ServerUtil.formatComponent(
                                        "<red>" + game.getName() + " will be starting in " + seconds + " seconds!</red>"
                                ));
                            }
                        }

                        if (seconds <= 0) {
                            if (game.isRequiresMap()) {
                                GameUtil.startGameWithRandomMap(game);
                            } else {
                                GameUtil.startGame(game.getKey(), game.isVoidWorld());
                            }
                        }
                    }
                }

            }.runTaskTimer(Jarona.getInstance(), 0L, 20L);
        }
    }

    public List<Player> getPlayersQueued(Game game) {
        return getPlayersQueued(game.getKey());
    }

    public List<Player> getPlayersQueued(NamespacedKey gameKey) {
        return playerQueue.stream()
                .filter(qp -> qp.isQueuedFor(gameKey))
                .map(QueuedPlayer::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<QueuedPlayer> getQueuedPlayers(NamespacedKey gameKey) {
        return playerQueue.stream()
                .filter(qp -> qp.isQueuedFor(gameKey) && qp.getPlayer() != null)
                .toList();
    }

    public List<QueuedPlayer> getQueuedPlayers(Game game) {
        return playerQueue.stream()
                .filter(qp -> qp.isQueuedFor(game) && qp.getPlayer() != null)
                .toList();
    }

    public List<UUID> getUUIDsQueued(Game game) {
        return getUUIDsQueued(game.getKey());
    }

    public List<UUID> getUUIDsQueued(NamespacedKey gameKey) {
        return playerQueue.stream()
                .filter(qp -> qp.isQueuedFor(gameKey))
                .map(QueuedPlayer::getPlayerUUID)
                .toList();
    }

    public List<Player> getAndRemovePlayersQueued(Game game) {
        var queued = getQueuedPlayers(game);
        for (QueuedPlayer player : queued) {
            playerQueue.remove(player);
        }
        return queued.stream().map(QueuedPlayer::getPlayer).toList();
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
        playerQueue.add(new QueuedPlayer(uuid, gameKey));
    }

    public void unqueuePlayer(Player player) {
        playerQueue.removeIf(qp -> qp.getPlayer().equals(player));
    }

    public void unqueuePlayer(UUID uuid) {
        playerQueue.removeIf(qp -> qp.getPlayerUUID().equals(uuid));
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
        return playerQueue.stream().anyMatch(qp -> qp.getPlayerUUID().equals(uuid) && qp.getGameKey().equals(gameKey));
    }

    public boolean areEnoughPlayersQueued(Game game) {
        return getPlayersQueued(game.getKey()).size() >= game.getMinimumPlayers();
    }

}
