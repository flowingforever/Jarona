package pro.fazeclan.river.jarona.util;

import org.bukkit.*;
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
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), key.value() + "_" + uuid);
        if (voidWorld) {
            world = WorldUtil.createVoidWorld(worldKey);
        } else {
            world = WorldUtil.createWorld(worldKey);
        }
        world.getPersistentDataContainer().set(Jarona.getKey("game"), PersistentDataType.STRING, key.toString());
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(world, 0, 10, 0));
            // todo: reset player attributes
        });
        game.init(world, world.getPlayers());
        var task = Bukkit.getScheduler().runTaskTimer(Jarona.getInstance(), () -> {
            game.tick(world, world.getPlayers());
        }, 1, 1);
        world.getPersistentDataContainer().set(Jarona.getKey("loop_id"), PersistentDataType.INTEGER, task.getTaskId());
        manager.addActiveGame(uuid, key);
    }

}
