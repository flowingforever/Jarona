package pro.fazeclan.river.jarona.tablist;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.util.NametagUtil;

import java.util.*;
import java.util.function.Predicate;

public class TablistCategory {

    private final TablistEntry title;
    private final List<TablistEntry> entries;
    private final List<TablistEntry> emptyEntries;
    private final String fakeName;
    private final Component displayComponent;
    @Getter
    private final UUID worldUID;
    private final Predicate<Player> predicate;

    public TablistCategory(String fakeName, String component, List<TablistEntry> entries) {
        this.entries = entries;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = null;
    }

    public TablistCategory(String fakeName, Component component, List<TablistEntry> entries) {
        this.entries = entries;
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = null;
    }

    public TablistCategory(String fakeName, String component, TablistEntry... entries) {
        this.entries = List.of(entries);
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = null;
    }

    public TablistCategory(String fakeName, Component component, TablistEntry... entries) {
        this.entries = List.of(entries);
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = null;
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
        this.worldUID = null;
        this.predicate = null;
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
        this.worldUID = null;
        this.predicate = null;
    }

    public TablistCategory(String fakeName, Component component, Predicate<Player> predicate) {
        this.entries = List.of();
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = component;
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = predicate;
    }

    public TablistCategory(String fakeName, String component, Predicate<Player> predicate) {
        this.entries = List.of();
        this.emptyEntries = new ArrayList<>();
        this.displayComponent = MiniMessage.miniMessage().deserialize(component);
        this.fakeName = fakeName;
        this.title = new TablistEntry(this.fakeName, this.displayComponent);
        this.worldUID = null;
        this.predicate = predicate;
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

        if (predicate != null && getWorld() != null) {
            for (var entry : entries) {
                entry.removeEntry(viewer);
            }
            entries.clear();

            for (var player : getWorld().getPlayers()) {
                if (predicate.test(player)) {
                    entries.add(new TablistEntry(
                            player
                    ));
                }
            }
        }

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

    public void broadcastCategory(int listOrder) {
        var players = getViewers();

        // remove any empty entries (if there is any)
        for (var entry : emptyEntries) {
            for (var viewer : players) {
                entry.removeEntry(viewer);
            }
        }
        emptyEntries.clear();

        if (predicate != null && getWorld() != null) {
            for (var entry : entries) {
                for (var viewer : players) {
                    entry.removeEntry(viewer);
                }
            }
            entries.clear();

            for (var player : getWorld().getPlayers()) {
                if (predicate.test(player)) {
                    entries.add(new TablistEntry(
                            player
                    ));
                }
            }
        }

        var entryOrder = listOrder;

        // broadcast initial entries
        title.setListOrder(entryOrder);
        for (var viewer : players) {
            title.broadcastEntry(viewer);
        }
        for (var entry : entries) {
            entry.setListOrder(--entryOrder);
            for (var viewer : players) {
                entry.broadcastEntry(viewer);
            }
        }

        // fill the empty slots with empty entries
        var remainder = (entries.size() + 1) % 20;
        if (remainder != 0) {
            for (int i = 0; i < (20 - remainder); i++) {
                var entry = new TablistEntry(NametagUtil.generateUsername(12), Component.empty());
                entry.setListOrder(--entryOrder);
                emptyEntries.add(entry);
                for (var viewer : players) {
                    entry.broadcastEntry(viewer);
                }
            }
        }
    }

    public void removeCategory() {
        var players = getViewers();

        // remove any empty entries (if there is any)
        for (var entry : emptyEntries) {
            for (var viewer : players) {
                entry.removeEntry(viewer);
            }
        }
        emptyEntries.clear();

        // remove any remaining entries
        for (var viewer : players) {
            title.removeEntry(viewer);
        }
        for (var entry : entries) {
            for (var viewer : players) {
                entry.removeEntry(viewer);
            }
        }
    }

    public World getWorld() {
        if (worldUID == null) return null;
        return Bukkit.getWorld(worldUID);
    }

    public List<Player> getViewers() {
        if (worldUID == null) return List.of();
        var world = Bukkit.getWorld(worldUID);
        if (world == null) return List.of();
        return world.getPlayers();
    }

}
