package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    @Getter
    protected Map<NamespacedKey, Game> registry = new HashMap<>();

    public <G extends Game> G register(G game) {
        this.registry.put(game.getKey(), game);
        return game;
    }

}
