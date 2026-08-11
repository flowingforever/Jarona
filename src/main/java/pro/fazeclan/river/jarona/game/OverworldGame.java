package pro.fazeclan.river.jarona.game;

import org.bukkit.NamespacedKey;

public abstract class OverworldGame extends Game {
    public OverworldGame(String name, NamespacedKey key, int minimumPlayers) {
        super(name, key, false, false, minimumPlayers);
    }
}
