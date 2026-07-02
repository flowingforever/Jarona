package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.UUID;

public class GameManager {

    @Getter
    protected HashMap<NamespacedKey, Game> registry = new HashMap<>();
    @Getter
    protected HashMap<UUID, Game> activeGames = new HashMap<>();

    public <G extends Game> G register(G game) {
        this.registry.put(game.key, game);
        return game;
    }

    public void addActiveGame(UUID uuid, NamespacedKey key) {
        this.activeGames.put(uuid, this.registry.get(key));
    }

    public UUID addActiveGame(NamespacedKey key) {
        var uuid = UUID.randomUUID();
        addActiveGame(uuid, key);
        return uuid;
    }

    public void removeActiveGame(UUID uuid) {
        this.activeGames.remove(uuid);
    }

}
