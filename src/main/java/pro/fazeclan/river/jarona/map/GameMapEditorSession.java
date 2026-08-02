package pro.fazeclan.river.jarona.map;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.codehaus.plexus.util.StringUtils;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class GameMapEditorSession {

    final World world;

    public GameMapEditorSession(World world) {
        this.world = world;
    }

    public String getId() {
        return world.getKey().value();
    }

    public String getName() {
        return getValue(
                Jarona.getKey("map_name"),
                PersistentDataType.STRING,
                "Empty"
        );
    }

    public void setName(String name) {
        setValue(
                Jarona.getKey("map_name"),
                PersistentDataType.STRING,
                name
        );
    }

    public String getCredit() {
        return getValue(
                Jarona.getKey("map_credit"),
                PersistentDataType.STRING,
                "Empty"
        );
    }

    public void setCredit(String credit) {
        setValue(
                Jarona.getKey("map_credit"),
                PersistentDataType.STRING,
                credit
        );
    }

    public void setSpawn(Location location) {
        setValue(
                Jarona.getKey("map_spawn"),
                DataType.LOCATION,
                location
        );
    }

    public Location getSpawn() {
        return getValue(
                Jarona.getKey("map_spawn"),
                DataType.LOCATION,
                new Location(world, 0, 0, 0, 0, 0)
        );
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
        var config = getOrCreateConfig();
        config.set("name", getName());
        config.set("credits", getCredit());
        WorldlessLocation.fromLocation(getSpawn()).serialize("spawn", config);
        try { config.save(configFile); } catch (IOException e) { throw new RuntimeException(e); }

        // reload registry
        Jarona.getInstance().getMapManager().reloadRegistry();
    }

    private <P, C> C getValue(
            NamespacedKey key,
            PersistentDataType<P, C> type,
            C defaultValue
    ) {
        return world.getPersistentDataContainer().getOrDefault(key, type, defaultValue);
    }

    private <P, C> void setValue(
            NamespacedKey key,
            PersistentDataType<P, C> type,
            C value
    ) {
        world.getPersistentDataContainer().set(key, type, value);
    }

}
