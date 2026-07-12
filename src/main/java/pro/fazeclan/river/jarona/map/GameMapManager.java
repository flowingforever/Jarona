package pro.fazeclan.river.jarona.map;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class GameMapManager {

    @Getter
    protected HashMap<String, GameMap> registry = new HashMap<>();

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
                            WorldlessLocation.deserialize("spawn", config)
                    ));
                });

    }

}
