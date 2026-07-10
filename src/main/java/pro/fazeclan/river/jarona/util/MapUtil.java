package pro.fazeclan.river.jarona.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.Random;

public class MapUtil {

    public static void placeStructure(World world, NamespacedKey structure) {
        var manager = Bukkit.getStructureManager();
        var str = manager.getStructure(structure);
        str.place(
                new Location(world, 0, 0, 0),
                true,
                StructureRotation.NONE,
                Mirror.NONE,
                0,
                1f,
                new Random()
        );
    }

}
