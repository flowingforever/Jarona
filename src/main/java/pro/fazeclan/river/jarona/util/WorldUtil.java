package pro.fazeclan.river.jarona.util;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.Jarona;

import java.io.File;
import java.io.IOException;
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

    public static void removeWorld(String name) {
        var world = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + name);
        try {
            do {
                Bukkit.unloadWorld(name, false);
                FileUtils.deleteDirectory(world);
            } while (world.exists());
        } catch (IOException e) {
            JavaPlugin.getPlugin(Jarona.class).getLogger().warning("Failed to remove world \"" + name + "\".");
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
