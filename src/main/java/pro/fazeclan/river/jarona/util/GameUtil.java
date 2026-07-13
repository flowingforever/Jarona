package pro.fazeclan.river.jarona.util;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.map.GameMap;

import java.io.IOException;
import java.util.UUID;

public class GameUtil {

    public static void startGame(NamespacedKey key, boolean voidWorld) {
        var uuid = UUID.randomUUID();
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), uuid.toString());
        if (voidWorld) {
            startGame(key, WorldUtil.createVoidWorld(worldKey));
        } else {
            startGame(key, WorldUtil.createWorld(worldKey));
        }
    }

    public static void startGameWithMap(NamespacedKey key, GameMap map) {
        var uuid = UUID.randomUUID();
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), uuid.toString());

        var worldFolder = Jarona.getInstance().getServer().getLevelDirectory()
                .toAbsolutePath()
                .resolve("dimensions/" + key.namespace() + "/" + uuid)
                .toFile();
        worldFolder.mkdirs();
        try {
            FileUtils.copyDirectory(map.getWorld(), worldFolder);
        } catch (IOException e) {
            Jarona.getInstance().getLogger().warning("The world may not have been entirely created.");
        }

        var world = WorldUtil.createWorld(worldKey);
        startGame(key, world);
    }

    public static void startGame(NamespacedKey key, World world) {
        var plugin = Jarona.getInstance();
        var queueManager = plugin.getQueueManager();
        var gameManager = plugin.getGameManager();
        var game = gameManager.getRegistry().get(key);
        if (game == null) {
            return;
        }
        world.getPersistentDataContainer().set(Jarona.getKey("game"), PersistentDataType.STRING, key.toString());
        queueManager.getAndRemovePlayersQueued(game).forEach(player -> {
            player.teleport(new Location(world, 0, 10, 0));
            resetPlayer(player, GameMode.SPECTATOR);
        });
        game.init(world, world.getPlayers());
        var task = Bukkit.getScheduler().runTaskTimer(Jarona.getInstance(), () -> game.tick(world, world.getPlayers()), 1, 1);
        world.getPersistentDataContainer().set(Jarona.getKey("loop_id"), PersistentDataType.INTEGER, task.getTaskId());
    }

    public static void endGame(World world) {
        var plugin = Jarona.getInstance();
        var manager = plugin.getGameManager();
        var logger = plugin.getLogger();
        if (!world.getPersistentDataContainer().has(Jarona.getKey("game"))) {
            return;
        }
        var gameId = NamespacedKey.fromString(world.getPersistentDataContainer().get(Jarona.getKey("game"), PersistentDataType.STRING));
        var game = manager.getRegistry().get(gameId);
        game.end(world, world.getPlayers());
        cleanUpGame(world);
    }

    public static void cleanUpGame(World world) {
        var logger = Jarona.getInstance().getLogger();
        var mainWorld = WorldUtil.getMainWorld();
        if (mainWorld == null) {
            return;
        }
        var highestZeroCoordinate = mainWorld.getHighestBlockYAt(0, 0);
        for (Player player : world.getPlayers()) {
            player.teleport(new Location(mainWorld, 0.5, highestZeroCoordinate + 1.0, 0.0));
            resetPlayer(player, GameMode.ADVENTURE);
        }
        var taskId = world.getPersistentDataContainer().get(Jarona.getKey("loop_id"), PersistentDataType.INTEGER);
        WorldUtil.removeWorld(world.getName());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public static void resetPlayer(Player player, GameMode gameMode) {
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(gameMode);
    }

}
