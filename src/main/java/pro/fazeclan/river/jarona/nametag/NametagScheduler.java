package pro.fazeclan.river.jarona.nametag;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import pro.fazeclan.river.jarona.Jarona;

public class NametagScheduler {

    private BukkitTask task;

    public void start() {
        var plugin = Jarona.getInstance();
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Nametag nametag : plugin.getNametagManager().getAll()) {
                nametag.updateVisibilityForAll();
            }
        }, 20, 20);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

}
