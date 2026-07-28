package pro.fazeclan.river.jarona.map;

import org.bukkit.configuration.file.YamlConfiguration;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.Arrays;

public record GameMap(String name, String id, String credit, File world, WorldlessLocation spawn) {

    public YamlConfiguration getConfig() {
        var configFile = Arrays.stream(world.listFiles()).filter(file -> file.getName().equals("map_config.yml")).findFirst().get();
        return YamlConfiguration.loadConfiguration(configFile);
    }

}
