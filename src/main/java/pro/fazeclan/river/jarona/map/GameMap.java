package pro.fazeclan.river.jarona.map;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record GameMap(
        String name,
        String id,
        String credit,
        File world,
        WorldlessLocation spawn,
        List<NamespacedKey> supportedGames
) {

    public YamlConfiguration getConfig() {
        var configFile = Arrays.stream(world.listFiles()).filter(file -> file.getName().equals("map_config.yml")).findFirst().get();
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean isGameSupported(NamespacedKey key) {
        if (key == null) return true;
        return supportedGames.contains(key) || supportedGames.isEmpty();
    }

    public boolean isGameSupported(Game game) {
        return isGameSupported(game.getKey());
    }

}
