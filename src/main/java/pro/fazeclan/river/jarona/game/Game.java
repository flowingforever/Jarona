package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Game {

    @Getter
    private final String name;
    @Getter
    private final NamespacedKey key;
    @Getter
    private final boolean voidWorld;
    @Getter
    private final boolean requiresMap;
    @Getter
    private final int minimumPlayers;

    public Game(String name, NamespacedKey key, boolean voidWorld) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = false;
        this.minimumPlayers = 1;
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, int minimumPlayers) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = false;
        this.minimumPlayers = minimumPlayers;
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, boolean requiresMap) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = requiresMap;
        this.minimumPlayers = 1;
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, boolean requiresMap, int minimumPlayers) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = requiresMap;
        this.minimumPlayers = minimumPlayers;
    }

    public abstract void init(World world, List<Player> players);
    public abstract void tick(World world, List<Player> players);
    public abstract void end(World world, List<Player> players);

}
