package pro.fazeclan.river.jarona.tablist;

import org.bukkit.entity.Player;

import java.util.List;

public class TablistCategories {

    private final List<TablistCategory> categories;

    public TablistCategories(TablistCategory... categories) {
        this.categories = List.of(categories);
    }

    public TablistCategories(List<TablistCategory> categories) {
        this.categories = categories;
    }

    public void broadcastCategories(Player viewer) {
        var i = 5000;
        for (var category : categories) {
            category.broadcastCategory(viewer, i);
            i -= 20;
        }
    }

    public void removeCategories(Player viewer) {
        for (var category : categories) {
            category.removeCategory(viewer);
        }
    }

}
