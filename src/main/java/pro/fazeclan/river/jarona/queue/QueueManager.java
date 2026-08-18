package pro.fazeclan.river.jarona.queue;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;
import pro.fazeclan.river.jarona.screen.MapVotingScreen;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueueManager {

    private final CopyOnWriteArrayList<QueuedPlayer> playerQueue = new CopyOnWriteArrayList<>();
    private final ArrayList<QueuedGame> possibleGames = new ArrayList<>(16);
    private BukkitTask queueCheckLoop;

    public void startLoop() {
        if (this.queueCheckLoop == null) {
            this.queueCheckLoop = new BukkitRunnable() {
                private final Jarona plugin = Jarona.getInstance();

                @Override
                public void run() {
                    var initialSeconds = plugin.getConfig().getInt("start-wait-period", 30);
                    for (var game : plugin.getGameManager().getRegistry().values()) {
                        if (areEnoughPlayersQueued(game)) {
                            if (possibleGames.stream().noneMatch(qg -> qg.getGame().equals(game))) {
                                possibleGames.add(new QueuedGame(game, initialSeconds + 1));
                            }
                            possibleGames.forEach(qg -> {
                                if (qg.getGame().equals(game)) {
                                    qg.decrementSeconds();
                                }
                            });
                        } else {
                            possibleGames.removeIf(qg -> qg.getGame().equals(game));
                        }
                    }

                    for (var entry : possibleGames) {
                        var seconds = entry.getSecondsRemain();
                        var game = entry.getGame();

                        if (seconds == initialSeconds && game.isRequiresMap()) {
                            for (var player : getQueuedPlayers(game)) {
                                MapVotingScreen.handleScreen(player, null);
                                player.getPlayer().sendMessage(ServerUtil.formatComponent(
                                        "<b><green><click:run_command:'/jarona:map vote'>Click here to vote for the next map!</green></b>"
                                ));
                            }
                        }

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
                                GameUtil.startGameWithVotedMap(game);
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

    public QueuedPlayer getQueuedPlayer(Player player) {
        return playerQueue.stream()
                .filter(qp -> qp.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
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

    public boolean setPlayerVote(Player player, Game game, GameMap map) {
        var queuedGame = possibleGames.stream().filter(qg -> qg.getGame().equals(game)).findFirst().orElse(null);
        if (queuedGame == null) return false;
        queuedGame.vote(player, map);
        return true;
    }

    public boolean setPlayerVote(Player player, NamespacedKey gameKey, GameMap map) {
        var queuedGame = possibleGames.stream().filter(qg -> qg.getGame().getKey().equals(gameKey)).findFirst().orElse(null);
        if (queuedGame == null) return false;
        queuedGame.vote(player, map);
        return true;
    }

    public long getMapVotes(Game game, GameMap map) {
        var queuedGame = possibleGames.stream().filter(qg -> qg.getGame().equals(game)).findFirst().orElse(null);
        if (queuedGame == null) {
            return 0;
        }
        return queuedGame.getMapVotes(map);
    }

    public long getMapVotes(NamespacedKey gameKey, GameMap map) {
        var queuedGame = possibleGames.stream().filter(qg -> qg.getGame().getKey().equals(gameKey)).findFirst().orElse(null);
        if (queuedGame == null) {
            return 0;
        }
        return queuedGame.getMapVotes(map);
    }

    public GameMap getMostVotedMap(NamespacedKey gameKey) {
        var queuedGame = possibleGames.stream().filter(qg -> qg.getGame().getKey().equals(gameKey)).findFirst().orElse(null);
        if (queuedGame == null) {
            return null;
        }
        return queuedGame.getHighestVoted();
    }

}
