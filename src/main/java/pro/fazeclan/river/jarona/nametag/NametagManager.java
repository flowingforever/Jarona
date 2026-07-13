package pro.fazeclan.river.jarona.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.util.NametagUtil;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class NametagManager {

    private final Map<UUID, Nametag> nametags;

    public NametagManager() {
        this.nametags = new ConcurrentHashMap<>();
    }

    public Nametag get(Player player) {
        return this.nametags.get(player.getUniqueId());
    }

    public Collection<Nametag> getAll() {
        return this.nametags.values();
    }

    public void create(Player player) {
        var nametag = new Nametag(player);
        nametag.updateVisibilityForAll();
        this.nametags.put(player.getUniqueId(), nametag);
    }

    public void createOverride(Player player, BiFunction<Player, Player, String> override) {
        var nametag = new OverridenNametag(player, override);
        nametag.updateVisibilityForAll();
        this.nametags.put(player.getUniqueId(), nametag);
    }

    public void remove(Player player) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            NametagUtil.showPlayerNametag(player, viewer);
        }

        var nametag = nametags.get(player.getUniqueId());
        if (nametag != null) {
            nametag.hideForAll();
            nametags.remove(player.getUniqueId());
        }
    }

    public void createAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.create(player);
        }
    }

    public void removeAll() {
        for (Nametag nametag : nametags.values()) {
            this.remove(nametag.getPlayer());
        }
    }

}
