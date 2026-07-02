package pro.fazeclan.river.jarona.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;

@Getter
@Setter
public class GameMap {

    final String name;
    final NamespacedKey id;
    final String credit;
    final File world;
    final WorldlessLocation spawn;

    public GameMap(String name, NamespacedKey id, String credit, File world, WorldlessLocation spawn) {
        this.name = name;
        this.id = id;
        this.credit = credit;
        this.world = world;
        this.spawn = spawn;
    }

}
