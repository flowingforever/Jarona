package pro.fazeclan.river.jarona.tablist;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TablistCategories {

    private final List<TablistCategory> categories;

    public TablistCategories(TablistCategory... categories) {
        this.categories = List.of(categories);
    }

    public TablistCategories(List<TablistCategory> categories) {
        this.categories = categories;
    }

    public List<UUID> getWorldUUIDs() {
        return categories.stream()
                .map(TablistCategory::getWorldUID)
                .filter(Objects::nonNull)
                .toList();
    }

    public UUID getWorldUUID() {
        return getWorldUUIDs().getFirst();
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldUUID());
    }

    public List<World> getWorlds() {
        return getWorldUUIDs().stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();
    }

    public void broadcastCategories() {
        var i = 5000;
        for (var category : categories) {
            category.broadcastCategory(i);
            i -= 20;
        }
    }

    public void removeCategories() {
        for (var category : categories) {
            category.removeCategory();
        }
    }

}
