package pro.fazeclan.river.jarona.util;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.condition.Condition;
import pro.fazeclan.river.jarona.condition.ConditionManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WorldUtil {

    public static World createWorld(NamespacedKey key) {
        var worldCreator = new WorldCreator(key);
        return worldCreator.seed(ThreadLocalRandom.current().nextLong())
                .environment(World.Environment.NORMAL)
                .type(WorldType.NORMAL)
                .generatorSettings("{}")
                .generateStructures(true)
                .createWorld();
    }

    public static World createWorld(NamespacedKey key, World.Environment env) {
        var worldCreator = new WorldCreator(key);
        return worldCreator.seed(ThreadLocalRandom.current().nextLong())
                .environment(env)
                .type(WorldType.NORMAL)
                .generatorSettings("{}")
                .generateStructures(true)
                .createWorld();
    }

    public static World createWorld(NamespacedKey key, WorldType type) {
        var worldCreator = new WorldCreator(key);
        return worldCreator.seed(ThreadLocalRandom.current().nextLong())
                .environment(World.Environment.NORMAL)
                .type(type)
                .generatorSettings("{}")
                .generateStructures(true)
                .createWorld();
    }

    public static World createWorld(NamespacedKey key, String genSet) {
        var worldCreator = new WorldCreator(key);
        return worldCreator.seed(ThreadLocalRandom.current().nextLong())
                .environment(World.Environment.NORMAL)
                .type(WorldType.NORMAL)
                .generatorSettings(genSet)
                .generateStructures(true)
                .createWorld();
    }

    public static World createWorld(NamespacedKey key, boolean structures) {
        var worldCreator = new WorldCreator(key);
        return worldCreator.seed(ThreadLocalRandom.current().nextLong())
                .environment(World.Environment.NORMAL)
                .type(WorldType.NORMAL)
                .generatorSettings("{}")
                .generateStructures(structures)
                .createWorld();
    }

    public static World createWorld(WorldCreator wc) {
        return wc.createWorld();
    }

    public static World createVoidWorld(NamespacedKey key) {
        return new WorldCreator(key)
                .seed(ThreadLocalRandom.current().nextLong())
                .type(WorldType.FLAT)
                .generatorSettings("{\"layers\":[{\"block\":\"minecraft:air\",\"height\":99}],\"biome\":\"minecraft:the_void\"}")
                .generateStructures(false)
                .createWorld();
    }

    public static void removeWorld(World world) {
        var key = world.getKey();
        var dimNamespace = new File(Bukkit.getServer().getLevelDirectory() + "/dimensions/" + key.namespace());
        var dimValue = new File(dimNamespace, key.value());
        do {
            if (!Bukkit.isTickingWorlds()) Bukkit.unloadWorld(world, false);
        } while (!FileUtils.deleteQuietly(dimValue));

        if (dimNamespace.listFiles().length == 0) {
            FileUtils.deleteQuietly(dimNamespace);
        }
    }

    public static void removeWorld(NamespacedKey key) {
        var namespace = new File(Bukkit.getServer().getLevelDirectory() + "/dimensions/" + key.namespace());
        var world = new File(namespace, key.value());
        do {
            if (!Bukkit.isTickingWorlds()) Bukkit.unloadWorld(key.value(), false);
        } while (!FileUtils.deleteQuietly(world));

        if (namespace.listFiles().length == 0) {
            FileUtils.deleteQuietly(namespace);
        }
    }

    public static World getMainWorld() {
        var levelName = ServerUtil.readServerProperties("level-name");
        if (levelName == null) {
            return null;
        }
        return Bukkit.getWorld(levelName);
    }

}
