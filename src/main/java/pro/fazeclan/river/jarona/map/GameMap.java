package pro.fazeclan.river.jarona.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.Arrays;

@Getter
@Setter
public class GameMap {

    final String name;
    final String id;
    final String credit;
    final File world;
    final WorldlessLocation spawn;

    public GameMap(String name, String id, String credit, File world, WorldlessLocation spawn) {
        this.name = name;
        this.id = id;
        this.credit = credit;
        this.world = world;
        this.spawn = spawn;
    }

    public YamlConfiguration getConfig() {
        var configFile = Arrays.stream(world.listFiles()).filter(file -> file.getName().equals("map_config.yml")).findFirst().get();
        return YamlConfiguration.loadConfiguration(configFile);
    }

}
