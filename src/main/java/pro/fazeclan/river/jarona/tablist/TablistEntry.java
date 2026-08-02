package pro.fazeclan.river.jarona.tablist;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

public class TablistEntry {

    private final WrapperPlayServerPlayerInfoUpdate.PlayerInfo info;

    public TablistEntry(WrapperPlayServerPlayerInfoUpdate.PlayerInfo info) {
        this.info = info;
    }

    public TablistEntry(String fakeName, String component) {
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                new UserProfile(UUID.randomUUID(), fakeName, Collections.emptyList()),
                true,
                0,
                GameMode.SURVIVAL,
                MiniMessage.miniMessage().deserialize(component),
                null
        );
    }

    public TablistEntry(String fakeName, String component, int listOrder) {
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                new UserProfile(UUID.randomUUID(), fakeName, Collections.emptyList()),
                true,
                0,
                GameMode.SURVIVAL,
                MiniMessage.miniMessage().deserialize(component),
                null,
                listOrder
        );
    }

    public TablistEntry(String fakeName, Component component) {
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                new UserProfile(UUID.randomUUID(), fakeName, Collections.emptyList()),
                true,
                0,
                GameMode.SURVIVAL,
                component,
                null
        );
    }

    public TablistEntry(String fakeName, Component component, int listOrder) {
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                new UserProfile(UUID.randomUUID(), fakeName, Collections.emptyList()),
                true,
                0,
                GameMode.SURVIVAL,
                component,
                null,
                listOrder
        );
    }

    public TablistEntry(Player player) {
        var user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        assert user != null;
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                user.getProfile(),
                true,
                player.getPing(),
                GameMode.SURVIVAL,
                Component.text(player.getName()),
                null
        );
    }

    public TablistEntry(Player player, int listOrder) {
        var user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        assert user != null;
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                user.getProfile(),
                true,
                player.getPing(),
                GameMode.SURVIVAL,
                Component.text(player.getName()),
                null,
                listOrder
        );
    }

    public TablistEntry(Player player, String component) {
        var manager = PacketEvents.getAPI().getPlayerManager();
        var user = manager.getUser(player);
        assert user != null;
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                user.getProfile(),
                true,
                manager.getPing(player),
                GameMode.SURVIVAL,
                MiniMessage.miniMessage().deserialize(component),
                null
        );
    }

    public TablistEntry(Player player, String component, int listOrder) {
        var manager = PacketEvents.getAPI().getPlayerManager();
        var user = manager.getUser(player);
        assert user != null;
        this.info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                user.getProfile(),
                true,
                manager.getPing(player),
                GameMode.SURVIVAL,
                MiniMessage.miniMessage().deserialize(component),
                null,
                listOrder
        );
    }

    public int getListOrder() {
        return info.getListOrder();
    }

    public void setListOrder(int listOrder) {
        info.setListOrder(listOrder);
    }

    public void broadcastEntry(Player viewer) {
        var actions = EnumSet.of(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LIST_ORDER
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, new WrapperPlayServerPlayerInfoUpdate(actions, info));
    }

    public void removeEntry(Player viewer) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, new WrapperPlayServerPlayerInfoRemove(info.getProfileId()));
    }

}
