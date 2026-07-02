package pro.fazeclan.river.jarona.game;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Game {

    protected final NamespacedKey key;

    public Game(NamespacedKey key) {
        this.key = key;
    }

    abstract void init(World world, List<Player> players);
    abstract void tick(World world, List<Player> players);
    abstract void end(World world, List<Player> players);

}
