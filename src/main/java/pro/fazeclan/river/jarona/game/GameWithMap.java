package pro.fazeclan.river.jarona.game;

import org.bukkit.NamespacedKey;

public abstract class GameWithMap extends Game {

    public GameWithMap(String name, NamespacedKey key, int minimumPlayers) {
        super(name, key, true, true, minimumPlayers);
    }

}
