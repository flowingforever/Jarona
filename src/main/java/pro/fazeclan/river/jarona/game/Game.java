package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Game {

    @Getter
    protected final NamespacedKey key;

    public Game(NamespacedKey key) {
        this.key = key;
    }

    public abstract void init(World world, List<Player> players);
    public abstract void tick(World world, List<Player> players);
    public abstract void end(World world, List<Player> players);

}
