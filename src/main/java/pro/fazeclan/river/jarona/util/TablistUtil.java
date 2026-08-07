package pro.fazeclan.river.jarona.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.tablist.TablistCategories;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TablistUtil {

    private static Map<UUID, BukkitTask> worldToCategoryTaskMap = new ConcurrentHashMap<>();

    public static void setWorldTablistHeader(World world, String header) {
        world.getPersistentDataContainer().set(
                Jarona.getKey("tablist_header"),
                PersistentDataType.STRING,
                header
        );
    }

    public static void setWorldTablistFooter(World world, String footer) {
        world.getPersistentDataContainer().set(
                Jarona.getKey("tablist_footer"),
                PersistentDataType.STRING,
                footer
        );
    }

    public static void assignCategory(World world, TablistCategories categories) {
        worldToCategoryTaskMap.put(world.getUID(), new BukkitRunnable() {
            final World w = world;
            final UUID wUid = world.getUID();
            final TablistCategories c = categories;

            @Override
            public void run() {
                if (Bukkit.getWorld(wUid) == null) {
                    cancel();
                    return;
                }

                for (var viewer : w.getPlayers()) {
                    c.addViewer(viewer);
                }

                c.removeCategories();
                c.broadcastCategories();
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                c.removeCategories();
                super.cancel();
            }
        }.runTaskTimerAsynchronously(Jarona.getInstance(), 0L, 20L));
    }

    public static void removeCategories(World world) {
        var c = worldToCategoryTaskMap.get(world.getUID());
        if (c == null) return;
        c.cancel();
    }

}
