package pro.fazeclan.river.jarona.util;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;

import java.util.UUID;

public class GameUtil {

    public static void startGame(NamespacedKey key, boolean voidWorld) {
        var manager = Jarona.getInstance().getGameManager();
        var game = manager.getRegistry().get(key);
        if (game == null) {
            return;
        }
        World world;
        var uuid = UUID.randomUUID();
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), uuid.toString());
        if (voidWorld) {
            world = WorldUtil.createVoidWorld(worldKey);
        } else {
            world = WorldUtil.createWorld(worldKey);
        }
        world.getPersistentDataContainer().set(Jarona.getKey("game"), PersistentDataType.STRING, key.toString());
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(new Location(world, 0, 10, 0));
            resetPlayer(player, GameMode.SPECTATOR);
        });
        game.init(world, world.getPlayers());
        var task = Bukkit.getScheduler().runTaskTimer(Jarona.getInstance(), () -> game.tick(world, world.getPlayers()), 1, 1);
        world.getPersistentDataContainer().set(Jarona.getKey("loop_id"), PersistentDataType.INTEGER, task.getTaskId());
        manager.addActiveGame(uuid, key);
    }

    public static void endGame(World world) {
        var manager = Jarona.getInstance().getGameManager();
        if (!world.getPersistentDataContainer().has(Jarona.getKey("game"))) {
            return;
        }
        var worldName = UUID.fromString(world.getKey().getKey());
        if (!manager.getActiveGames().containsKey(worldName)) {
            return;
        }
        var game = manager.getActiveGames().get(worldName);
        game.end(world, world.getPlayers());
        cleanUpGame(world);
    }

    public static void cleanUpGame(World world) {
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
        if (taskId == null) {
            WorldUtil.removeWorld(world.getName());
            return;
        }
        Bukkit.getScheduler().cancelTask(taskId);
        WorldUtil.removeWorld(world.getName());
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
