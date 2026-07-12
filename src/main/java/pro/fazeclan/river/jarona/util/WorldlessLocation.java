package pro.fazeclan.river.jarona.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;

public class WorldlessLocation {

    @Getter @Setter
    double x;
    @Getter @Setter
    double y;
    @Getter @Setter
    double z;
    @Getter @Setter
    float yaw;
    @Getter @Setter
    float pitch;

    public WorldlessLocation() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0;
        this.pitch = 0;
    }

    public WorldlessLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public WorldlessLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void serialize(String name, PersistentDataContainer pdc) {
        var container = pdc.getAdapterContext().newPersistentDataContainer();

        container.set(
                Jarona.getKey("x"),
                PersistentDataType.DOUBLE,
                x
        );
        container.set(
                Jarona.getKey("y"),
                PersistentDataType.DOUBLE,
                y
        );
        container.set(
                Jarona.getKey("z"),
                PersistentDataType.DOUBLE,
                z
        );
        container.set(
                Jarona.getKey("yaw"),
                PersistentDataType.FLOAT,
                yaw
        );
        container.set(
                Jarona.getKey("pitch"),
                PersistentDataType.FLOAT,
                pitch
        );

        pdc.set(
                Jarona.getKey(name),
                PersistentDataType.TAG_CONTAINER,
                container
        );
    }

    public static WorldlessLocation deserialize(String name, PersistentDataContainer pdc) {
        var container = pdc.get(Jarona.getKey(name), PersistentDataType.TAG_CONTAINER);
        var location = new WorldlessLocation();

        if (container == null) {
            return location;
        }
        location.setX(container.getOrDefault(Jarona.getKey("x"), PersistentDataType.DOUBLE, 0d));
        location.setY(container.getOrDefault(Jarona.getKey("y"), PersistentDataType.DOUBLE, 0d));
        location.setZ(container.getOrDefault(Jarona.getKey("z"), PersistentDataType.DOUBLE, 0d));
        location.setYaw(container.getOrDefault(Jarona.getKey("yaw"), PersistentDataType.FLOAT, 0f));
        location.setPitch(container.getOrDefault(Jarona.getKey("pitch"), PersistentDataType.FLOAT, 0f));

        return location;
    }

    public static WorldlessLocation deserialize(String name, YamlConfiguration config) {
        return new WorldlessLocation(
                config.getDouble(name + ".x", 0.0),
                config.getDouble(name + ".y", 0.0),
                config.getDouble(name + ".z", 0.0),
                (float) config.getDouble(name + ".yaw", 0.0),
                (float) config.getDouble(name + ".pitch", 0.0)
        );
    }

}
