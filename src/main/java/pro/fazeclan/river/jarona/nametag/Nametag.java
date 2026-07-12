package pro.fazeclan.river.jarona.nametag;

import com.github.retrooper.packetevents.util.Vector3f;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pro.fazeclan.river.jarona.nametag.entity.ClientTextDisplay;
import pro.fazeclan.river.jarona.nametag.entity.DisplayBillboard;
import pro.fazeclan.river.jarona.nametag.entity.TextAlignment;
import pro.fazeclan.river.jarona.util.NametagUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

import java.util.*;

public class Nametag {

    @Getter
    private final Player player;
    private final ClientTextDisplay display;
    private final Set<UUID> viewers;

    private Component cachedText;

    public Nametag(Player player) {
        this.player = player;
        this.viewers = new HashSet<>();

        this.display = new ClientTextDisplay(player.getLocation().setRotation(0, 0));
        this.display.setTranslation(new Vector3f(0, 0.25f, 0));
        this.display.setScale(new Vector(1.0, 1.0, 1.0));
        this.display.setTextShadow(true);
        this.display.setTextAlignment(TextAlignment.CENTER);
        this.display.setSeeThrough(false);
        this.display.setBillboard(DisplayBillboard.VERTICAL);

        this.cachedText = getText();
    }

    public void hideForAll() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            this.hide(viewer);
        }
    }

    public void updateVisibilityForAll() {
        this.cachedText = getText();
        this.display.setLocation(player.getLocation().setRotation(0, 0));

        viewers.removeIf(uuid -> {
            var viewer = Bukkit.getPlayer(uuid);
            return viewer == null || !viewer.isOnline();
        });

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            boolean shouldSee = shouldSee(viewer);
            boolean isVisible = this.viewers.contains(viewer.getUniqueId());

            if (shouldSee) {
                if (isVisible) {
                    this.update(viewer);
                } else {
                    this.show(viewer);
                }
            } else {
                if (isVisible) this.hide(viewer);
            }
        }
    }

    private boolean shouldSee(Player viewer) {
        if (viewer == null || !viewer.isOnline() || viewer.isDead()) return false;
        if (player.getUniqueId().equals(viewer.getUniqueId())) return false;
        if (player.isDead() || player.getGameMode().equals(GameMode.SPECTATOR)) return false;
        if (!viewer.getWorld().getName().equals(player.getWorld().getName())) return false;
        if (player.isInvisible() || viewer.canSee(player)) return false;

        return viewer.getLocation().distanceSquared(player.getLocation()) < 144;
    }

    public void show(Player viewer) {
        NametagUtil.hidePlayerNametag(player, viewer);
        if (player.getUniqueId().equals(viewer.getUniqueId())) return;

        this.viewers.add(viewer.getUniqueId());
        this.display.spawn(viewer);
        this.update(viewer);
    }

    public void hide(Player viewer) {
        this.viewers.remove(viewer.getUniqueId());
        this.display.despawn(viewer);
    }

    public void update(Player viewer) {
        this.display.setLocation(player.getLocation());
        this.display.setText(this.cachedText);
        this.display.mount(this.player, viewer);
        this.display.update(viewer);
    }

    public void teleportForAll() {
        for (UUID uuid : this.viewers) {
            var viewer = Bukkit.getPlayer(uuid);
            this.display.teleportFor(viewer);
        }
    }

    public Component getText() {
        return ServerUtil.formatComponent("<gray>" + player.getName() + "</gray>");
    }

}
