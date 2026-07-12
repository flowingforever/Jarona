package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Game {

    @Getter
    protected final NamespacedKey key;
    @Getter
    protected final boolean voidWorld;
    @Getter
    protected final boolean requiresMap;

    public Game(NamespacedKey key, boolean voidWorld, boolean requiresMap) {
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = requiresMap;
    }

    public abstract void init(World world, List<Player> players);
    public abstract void tick(World world, List<Player> players);
    public abstract void end(World world, List<Player> players);

}
