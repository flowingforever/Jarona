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

    public TablistCategories addViewer(Player player) {
        for (var category : categories) {
            category.addViewer(player);
        }
        return this;
    }

    public TablistCategories removeViewer(Player player) {
        for (var category : categories) {
            category.removeViewer(player);
        }
        return this;
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
