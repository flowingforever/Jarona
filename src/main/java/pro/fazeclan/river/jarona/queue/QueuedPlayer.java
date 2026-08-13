package pro.fazeclan.river.jarona.queue;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;

import java.util.UUID;

public class QueuedPlayer {

    @Getter
    private final UUID playerUUID;
    @Getter
    @Setter
    private NamespacedKey gameKey;

    public QueuedPlayer(UUID playerUUID, Game game) {
        this.playerUUID = playerUUID;
        this.gameKey = game.getKey();
    }

    public QueuedPlayer(UUID playerUUID, NamespacedKey gameKey) {
        this.playerUUID = playerUUID;
        this.gameKey = gameKey;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerUUID);
    }

    public boolean isQueuedFor(NamespacedKey gameKey) {
        return this.gameKey.equals(gameKey);
    }

    public boolean isQueuedFor(Game game) {
        return isQueuedFor(game.getKey());
    }

}
