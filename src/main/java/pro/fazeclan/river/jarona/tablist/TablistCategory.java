package pro.fazeclan.river.jarona.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.util.NametagUtil;

import java.util.ArrayList;
import java.util.List;

public class TablistCategory {

    private final TablistEntry title;
    private final List<TablistEntry> entries;
    private final List<TablistEntry> emptyEntries;
    private final String fakeName;
    private final Component displayComponent;

    public TablistCategory(String fakeName, String component, List<TablistEntry> entries) {
        this.entries = entries;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public TablistCategory(String fakeName, Component component, List<TablistEntry> entries) {
        this.entries = entries;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public TablistCategory(String fakeName, String component, TablistEntry... entries) {
        this.entries = List.of(entries);
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public TablistCategory(String fakeName, Component component, TablistEntry... entries) {
        this.entries = List.of(entries);
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public TablistCategory(String fakeName, String component, Player... players) {
        var list = new ArrayList<TablistEntry>();
        for (var player : players) {
            list.add(new TablistEntry(player));
        }
        this.entries = list;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public TablistCategory(String fakeName, Component component, Player... players) {
        var list = new ArrayList<TablistEntry>();
        for (var player : players) {
            list.add(new TablistEntry(player));
        }
        this.entries = list;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
    }

    public void organizeCategory(int listOrder) {
        title.setListOrder(listOrder);
        for (var entry : entries) {
            entry.setListOrder(listOrder + 1);
        }

        for (var entry : emptyEntries) {
            entry.setListOrder(listOrder + 2);
        }
    }

    public void broadcastCategory(Player viewer) {
        // remove any empty entries (if there is any)
        for (var entry : emptyEntries) {
            entry.removeEntry(viewer);
        }
        emptyEntries.clear();

        // broadcast initial entries
        title.broadcastEntry(viewer);
        for (var entry : entries) {
            entry.broadcastEntry(viewer);
        }

        // fill the empty slots with empty entries
        var remainder = (entries.size() + 1) % 20;
        if (remainder != 0) {
            for (int i = 0; i < remainder; i++) {
                var entry = new TablistEntry(NametagUtil.generateUsername(12), Component.empty());
                emptyEntries.add(entry);
                entry.broadcastEntry(viewer);
            }
        }
    }

    public void broadcastCategory(Player viewer, int listOrder) {
        // remove any empty entries (if there is any)
        for (var entry : emptyEntries) {
            entry.removeEntry(viewer);
        }
        emptyEntries.clear();

        var entryOrder = listOrder;

        // broadcast initial entries
        title.setListOrder(entryOrder);
        title.broadcastEntry(viewer);
        for (var entry : entries) {
            entry.setListOrder(--entryOrder);
            entry.broadcastEntry(viewer);
        }

        // fill the empty slots with empty entries
        var remainder = (entries.size() + 1) % 20;
        if (remainder != 0) {
            for (int i = 0; i < (20 - remainder); i++) {
                var entry = new TablistEntry(NametagUtil.generateUsername(12), Component.empty());
                entry.setListOrder(--entryOrder);
                emptyEntries.add(entry);
                entry.broadcastEntry(viewer);
            }
        }
    }

    public void removeCategory(Player viewer) {
        // remove any empty entries (if there is any)
        for (var entry : emptyEntries) {
            entry.removeEntry(viewer);
        }
        emptyEntries.clear();

        // remove any remaining entries
        title.removeEntry(viewer);
        for (var entry : entries) {
            entry.removeEntry(viewer);
        }
    }

}
