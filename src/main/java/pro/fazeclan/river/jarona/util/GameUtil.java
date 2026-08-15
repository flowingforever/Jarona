package pro.fazeclan.river.jarona.util;

import de.tr7zw.nbtapi.NBT;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GameUtil {

    public static void startGame(NamespacedKey key, boolean voidWorld, Player... players) {
        var uuid = UUID.randomUUID();
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), uuid.toString());
        if (voidWorld) {
            startGame(key, WorldUtil.createVoidWorld(worldKey), players);
        } else {
            startGame(key, WorldUtil.createWorld(worldKey), players);
        }
    }

    public static void startGameWithRandomMap(Game game, Player... players) {
        startGameWithRandomMap(game.getKey(), players);
    }

    public static void startGameWithRandomMap(NamespacedKey key, Player... players) {
        var mapCollection = Jarona.getInstance().getMapManager().getAllMapsSupporting(key);
        int index = ThreadLocalRandom.current().nextInt(mapCollection.size());
        var map = mapCollection.stream()
                .skip(index)
                .findFirst()
                .orElse(null);
        if (map == null) {
            return;
        }
        startGameWithMap(key, map, players);
    }

    public static void startGameWithVotedMap(Game game, Player... players) {
        startGameWithVotedMap(game.getKey(), players);
    }

    public static void startGameWithVotedMap(NamespacedKey key, Player... players) {
        var queue = Jarona.getInstance().getQueueManager();
        var map = queue.getMostVotedMap(key);
        if (map == null) {
            startGameWithRandomMap(key, players);
        } else {
            startGameWithMap(key, map, players);
        }
    }

    public static void startGameWithMap(NamespacedKey key, GameMap map, Player... players) {
        var uuid = UUID.randomUUID();
        NamespacedKey worldKey = new NamespacedKey(key.namespace(), uuid.toString());

        var worldFolder = Jarona.getInstance().getServer().getLevelDirectory()
                .toAbsolutePath()
                .resolve("dimensions/" + key.namespace() + "/" + uuid)
                .toFile();
        worldFolder.mkdirs();
        try {
            FileUtils.copyDirectory(map.world(), worldFolder);
        } catch (IOException e) {
            Jarona.getInstance().getLogger().warning("The world may not have been entirely created.");
        }

        var world = WorldUtil.createWorld(worldKey);
        startGame(key, world, players);
    }

    public static void startGame(NamespacedKey key, World world, Player... players) {
        var plugin = Jarona.getInstance();
        var queueManager = plugin.getQueueManager();
        var gameManager = plugin.getGameManager();
        var game = gameManager.getRegistry().get(key);
        if (game == null) {
            return;
        }
        world.getPersistentDataContainer().set(Jarona.getKey("game"), PersistentDataType.STRING, key.toString());
        if (players.length == 0) {
            queueManager.getAndRemovePlayersQueued(game).forEach(player -> {
                player.teleport(new Location(world, 0, 10, 0));
                savePlayer(player);
                resetPlayer(player, GameMode.SPECTATOR);
            });
        } else {
            Arrays.stream(players).forEach(player -> {
                player.teleport(new Location(world, 0, 10, 0));
                savePlayer(player);
                resetPlayer(player, GameMode.SPECTATOR);
            });
        }
        game.init(world, world.getPlayers());
        var task = Bukkit.getScheduler().runTaskTimer(Jarona.getInstance(), () -> game.tick(world, world.getPlayers()), 1, 1);
        world.getPersistentDataContainer().set(Jarona.getKey("loop_id"), PersistentDataType.INTEGER, task.getTaskId());
    }

    public static void endGame(World world) {
        var game = getGame(world);
        if (game == null) {
            return;
        }
        game.removeGameValues(world.getUID());
        game.end(world, world.getPlayers());
        cleanUpGame(world);
    }

    public static void cleanUpGame(World world) {
        var mainWorld = WorldUtil.getMainWorld();
        if (mainWorld == null) {
            return;
        }
        for (Player player : world.getPlayers()) {
            player.teleport(mainWorld.getSpawnLocation());
            resetPlayer(player, GameMode.ADVENTURE);
            loadPlayer(player);
        }
        var taskId = world.getPersistentDataContainer().get(Jarona.getKey("loop_id"), PersistentDataType.INTEGER);
        WorldUtil.removeWorld(world);
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

    public static Game getGame(Player player) {
        return getGame(player.getWorld());
    }

    public static Game getGame(World world) {
        var plugin = Jarona.getInstance();
        var manager = plugin.getGameManager();
        if (!world.getPersistentDataContainer().has(Jarona.getKey("game"))) {
            return null;
        }
        var gameId = NamespacedKey.fromString(world.getPersistentDataContainer().get(Jarona.getKey("game"), PersistentDataType.STRING));
        return manager.getRegistry().get(gameId);
    }

    public static boolean hasGame(World world) {
        return world.getPersistentDataContainer().has(Jarona.getKey("game"));
    }

    public static boolean hasGame(World world, NamespacedKey key) {
        if (!world.getPersistentDataContainer().has(Jarona.getKey("game"))) return false;
        var gameId = NamespacedKey.fromString(world.getPersistentDataContainer().get(Jarona.getKey("game"), PersistentDataType.STRING));
        if (gameId == null) return false;
        return gameId.equals(key);
    }

    public static boolean hasGame(World world, Game game) {
        return hasGame(world, game.getKey());
    }

    public static void savePlayer(Player player) {
        var inventory = NBT.get(player, nbt -> "{Inventory:" + nbt.getCompoundList("Inventory") + ", equipment:" + nbt.getCompound("equipment") + "}");

        var key = Jarona.getKey("saved_inventory");
        if (!player.getPersistentDataContainer().has(key)) {
            player.getPersistentDataContainer().set(key, PersistentDataType.STRING, inventory);
        }
        player.getInventory().clear();
    }

    public static void loadPlayer(Player player) {
        var key = Jarona.getKey("saved_inventory");
        var inventory = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, "{}");
        player.getPersistentDataContainer().remove(key);

        NBT.modify(player, nbt -> {
            nbt.mergeCompound(NBT.parseNBT(inventory));
        });
        player.updateInventory();
    }

    public static List<? extends Player> getAllPlayersNotInGame() {
        return Bukkit.getOnlinePlayers().stream().filter(p -> !hasGame(p.getWorld())).toList();
    }

}
