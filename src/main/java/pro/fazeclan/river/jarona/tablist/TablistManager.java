package pro.fazeclan.river.jarona.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.alexdev.unlimitednametags.api.UNTPaperAPI;
import org.alexdev.unlimitednametags.api.UntNametagManagerPaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.NametagUtil;
import pro.fazeclan.river.jarona.util.SchedulingUtil;

import java.io.Closeable;
import java.io.IOException;

public class TablistManager {

    private Closeable task;

    public void startTask() {
        this.task = SchedulingUtil.asyncInterval(10, 10, () -> {
            var manager = (UntNametagManagerPaper) UNTPaperAPI.getInstance().nametagManager();
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

    private void setTabEntry(UntNametagManagerPaper manager, Player viewer, Player target) {
        if (!viewer.getWorld().equals(target.getWorld())) {
            NametagUtil.modifyTabName(target, viewer, Component.text(target.getName()).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
            return;
        }
        if (!manager.hasNametagOverride(target)) {
            return;
        }
        var nametag = manager.getNametagOverride(target).get().displayGroups().getFirst();
        NametagUtil.modifyTabName(target, viewer, nametag.lines().getFirst().text());
    }

    private void setTabHeaderFooter(Player viewer) {
        var worldPDC = viewer.getWorld().getPersistentDataContainer();
        var miniMessage = MiniMessage.miniMessage();
        boolean set = false;
        if (worldPDC.has(Jarona.getKey("tablist_header"))) {
            viewer.sendPlayerListHeader(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_header"), PersistentDataType.STRING)));
            set = true;
        }
        if (worldPDC.has(Jarona.getKey("tablist_footer"))) {
            viewer.sendPlayerListFooter(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_footer"), PersistentDataType.STRING)));
            set = true;
        }

        if (!set) {
            viewer.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
        }
    }

    private void resetTabEntry(Player viewer, Player target) {
        NametagUtil.modifyTabName(target, viewer, target.displayName());
    }

    private void resetTabHeaderFooter(Player viewer) {
        viewer.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
    }

}