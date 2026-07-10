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
    final File structure;
    final WorldlessLocation spawn;

    public GameMap(String name, NamespacedKey id, String credit, File structure, WorldlessLocation spawn) {
        this.name = name;
        this.id = id;
        this.credit = credit;
        this.structure = structure;
        this.spawn = spawn;
    }

}
