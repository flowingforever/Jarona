package pro.fazeclan.river.jarona.game;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.stats.StatisticDefinition;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<UUID, GameValues> gameVariables;

    public Game(String name, NamespacedKey key, boolean voidWorld) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = false;
        this.minimumPlayers = 1;
        this.gameVariables = new ConcurrentHashMap<>();
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, int minimumPlayers) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = false;
        this.minimumPlayers = minimumPlayers;
        this.gameVariables = new ConcurrentHashMap<>();
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, boolean requiresMap) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = requiresMap;
        this.minimumPlayers = 1;
        this.gameVariables = new ConcurrentHashMap<>();
    }

    public Game(String name, NamespacedKey key, boolean voidWorld, boolean requiresMap, int minimumPlayers) {
        this.name = name;
        this.key = key;
        this.voidWorld = voidWorld;
        this.requiresMap = requiresMap;
        this.minimumPlayers = minimumPlayers;
        this.gameVariables = new ConcurrentHashMap<>();
    }

    public abstract void init(World world, List<Player> players);
    public abstract void tick(World world, List<Player> players);
    public abstract void end(World world, List<Player> players);

    public GameValues getGameValues(UUID worldUUID) {
        return this.gameVariables.compute(worldUUID, (uuid, gameValues) -> {
            if (gameValues == null) {
                return new GameValues();
            }

            return gameValues;
        });
    }

    public void removeGameValues(UUID worldUUID) {
        this.gameVariables.remove(worldUUID);
    }

    public List<StatisticDefinition> getStatDefinitions() {
        return List.of();
    }

}
