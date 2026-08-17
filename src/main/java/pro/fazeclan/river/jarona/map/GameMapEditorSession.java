package pro.fazeclan.river.jarona.map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameMapEditorSession {

    final World world;
    final YamlConfiguration config;

    public GameMapEditorSession(World world) {
        this.world = world;
        config = getOrCreateConfig();
    }

    public String getId() {
        return world.getKey().value();
    }

    public String getName() {
        return getValue("name", "...");
    }

    public void setName(String name) {
        setValue("name", name);
    }

    public String getCredit() {
        return getValue("credits", "...");
    }

    public void setCredit(String credit) {
        setValue("credits", credit);
    }

    public void setSpawn(Location location) {
        WorldlessLocation.fromLocation(location).serialize("spawn", config);
    }

    public Location getSpawn() {
        return WorldlessLocation.deserialize("spawn", config).toLocation(world);
    }

    public void addSupportedGame(Game game) {
        var supportedGames = new ArrayList<>(getStringList("supported-games"));
        if (!supportedGames.contains(game.getKey().asString())) {
            supportedGames.add(game.getKey().asString());
        }
        setValue("supported-games", (List<String>) supportedGames);
    }

    public void removeSupportedGame(Game game) {
        var supportedGames = new ArrayList<>(getStringList("supported-games"));
        supportedGames.remove(game.getKey().asString());
        setValue("supported-games", (List<String>) supportedGames);
    }

    public void addConfigEntry(String input, @Nullable Player player) {
        // load config
        var configFile = new File(world.getWorldFolder(), "map_config.yml");
        var config = getOrCreateConfig();

        // interpret string to configuration entry
        if (!input.contains("=")) {
            return;
        }
        var variable = input.substring(0, input.indexOf("="));
        var value = input.substring(input.indexOf("=") + 1);

        // value cases
        if (value.equals("%location%") && player != null) { // location
            WorldlessLocation.fromLocation(player.getLocation()).serialize(variable, config);
            try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }
            return;
        }

        if (StringUtils.isNumeric(value)) { // int
            config.set(variable, Integer.parseInt(value));
            try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }
            return;
        }

        if (NumberUtils.isParsable(value)) { // double/float
            config.set(variable, Double.parseDouble(value));
            try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }
            return;
        }

        config.set(variable, value); // string
        try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public String removePreviousConfigEntry() {
        // load config
        var configFile = new File(world.getWorldFolder(), "map_config.yml");
        var config = getOrCreateConfig();

        // delete last entry
        var entries = config.getValues(false).keySet();
        String entry = null;

        for (String element : entries) {
            entry = element;
        }
        if (entry == null) {
            return null;
        }
        config.set(entry, null);
        try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }
        return entry;
    }

    public YamlConfiguration getOrCreateConfig() {
        var configFile = new File(world.getWorldFolder(), "map_config.yml");
        if (!configFile.exists()) {
            try { configFile.createNewFile(); } catch (Exception e) { throw new RuntimeException(e); }
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        var configFile = new File(world.getWorldFolder(), "map_config.yml");
        CompletableFuture.runAsync(() -> {
            try {
                config.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void saveAsMap() {
        var worldFolder = Jarona.getInstance().getDataFolder();
        var saveLocation = new File(worldFolder, "maps/" + getId());
        saveLocation.mkdirs();

        // copy everything over
        try {
            var filter = FileFilterUtils.notFileFilter(
                    FileFilterUtils.or(
                            FileFilterUtils.directoryFileFilter()
                                    .and(FileFilterUtils.nameFileFilter("paper")),
                            FileFilterUtils.fileFileFilter()
                                    .and(FileFilterUtils.nameFileFilter("paper-world.yml"))
                    )
            );

            FileUtils.copyDirectory(world.getWorldFolder(), saveLocation, filter);
        } catch (Exception e) { throw new RuntimeException(e); }

        // delete folders that will crash the server
        var paperWorldFile = new File(saveLocation, "paper-world.yml");
        paperWorldFile.delete();

        var paperWorldData = new File(saveLocation, "data/paper");
        try { FileUtils.deleteDirectory(paperWorldData); } catch (Exception e) { throw new RuntimeException(e); }

        // set the last few things in the config
        var configFile = new File(saveLocation, "map_config.yml");
        try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }

        // reload registry
        Jarona.getInstance().getMapManager().reloadRegistry();
    }

    private <T> T getValue(String path, T def) {
        return (T) config.get(path, def);
    }

    private List<String> getStringList(String path) {
        return config.getStringList("path");
    }

    private <T> void setValue(String path, T value) {
        config.set(path, value);
        save();
    }

}
