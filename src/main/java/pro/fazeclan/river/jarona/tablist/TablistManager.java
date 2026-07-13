package pro.fazeclan.river.jarona.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.nametag.NametagManager;
import pro.fazeclan.river.jarona.util.NametagUtil;
import pro.fazeclan.river.jarona.util.SchedulingUtil;

import java.io.Closeable;
import java.io.IOException;

public class TablistManager {

    private Closeable task;

    public void startTask() {
        var plugin = Jarona.getInstance();
        this.task = SchedulingUtil.asyncInterval(10, 10, () -> {
            var manager = plugin.getNametagManager();
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                setTabHeaderFooter(viewer);
                for (Player target : Bukkit.getOnlinePlayers()) {
                    setTabEntry(manager, viewer, target);
                }
            }
        });
    }

    public void stopTask() {
        try {
            this.task.close();
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                resetTabHeaderFooter(viewer);
                for (Player target : Bukkit.getOnlinePlayers()) {
                    resetTabEntry(viewer, target);
                }
            }
        } catch (IOException ignored) {}
    }

    private void setTabEntry(NametagManager manager, Player viewer, Player target) {
        if (!viewer.getWorld().equals(target.getWorld())) {
            return;
        }
        var nametag = manager.get(target);
        if (nametag == null) {
            return;
        }
        NametagUtil.modifyTabName(target, viewer, nametag.getText(viewer));
    }

    private void setTabHeaderFooter(Player viewer) {
        var worldPDC = viewer.getWorld().getPersistentDataContainer();
        var miniMessage = MiniMessage.miniMessage();
        if (worldPDC.has(Jarona.getKey("tablist_header"))) {
            viewer.sendPlayerListHeader(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_header"), PersistentDataType.STRING)));
        }
        if (worldPDC.has(Jarona.getKey("tablist_footer"))) {
            viewer.sendPlayerListFooter(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_footer"), PersistentDataType.STRING)));
        }
    }

    private void resetTabEntry(Player viewer, Player target) {
        NametagUtil.modifyTabName(target, viewer, target.displayName());
    }

    private void resetTabHeaderFooter(Player viewer) {
        viewer.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
    }

}