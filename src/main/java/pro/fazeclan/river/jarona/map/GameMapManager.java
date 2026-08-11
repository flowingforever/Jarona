package pro.fazeclan.river.jarona.map;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameMapManager {

    @Getter
    private final HashMap<String, GameMap> registry = new HashMap<>();

    public void reloadRegistry() {
        registry.clear();

        var folder = new File(Jarona.getInstance().getDataFolder(), "maps");
        if (!folder.exists()) {
            return;
        }
        var files = folder.listFiles();
        if (files == null) {
            return;
        }

        Arrays.stream(files)
                .filter(File::isDirectory)
                .filter(world -> Arrays.stream(world.listFiles()).anyMatch(file -> file.getName().equals("map_config.yml")))
                .forEach(world -> {
                    var configFile = Arrays.stream(world.listFiles()).filter(file -> file.getName().equals("map_config.yml")).findFirst().get();
                    var config = YamlConfiguration.loadConfiguration(configFile);
                    var id = world.getName().replace(".yml", "");
                    registry.put(id, new GameMap(
                            config.getString("name", "..."),
                            id,
                            config.getString("credits", "..."),
                            world,
                            WorldlessLocation.deserialize("spawn", config),
                            config.getStringList("supported-games").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList()
                    ));
                });

    }

    public List<GameMap> getAllMapsSupporting(NamespacedKey gameKey) {
        return registry.values().stream()
                .filter(map -> map.isGameSupported(gameKey))
                .toList();
    }

    public List<GameMap> getAllMapsSupporting(Game game) {
        return registry.values().stream()
                .filter(map -> map.isGameSupported(game))
                .toList();
    }

}
