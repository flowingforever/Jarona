package pro.fazeclan.river.jarona.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NametagUtil {

    public static void hidePlayerNametag(Player target, Player viewer) {
        hidePlayerNametagWithGlow(target, viewer, NamedTextColor.WHITE);
    }

    public static void hidePlayerNametagWithGlow(Player target, Player viewer, NamedTextColor color) {
        var name = getTeamName(target);
        var teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(),
                Component.empty(),
                Component.empty(),
                WrapperPlayServerTeams.NameTagVisibility.NEVER,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                color,
                WrapperPlayServerTeams.OptionData.NONE
        );
        sendPacket(new WrapperPlayServerTeams(
                name,
                WrapperPlayServerTeams.TeamMode.CREATE,
                teamInfo,
                target.getName()
        ), viewer);
    }

    public static void hidePlayerNametagWithGlowToAll(Player target, NamedTextColor color) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            hidePlayerNametagWithGlow(target, player, color);
        }
    }

    public static void showPlayerNametag(Player target, Player viewer) {
        String name = getTeamName(target);
        sendPacket(new WrapperPlayServerTeams(
                name,
                WrapperPlayServerTeams.TeamMode.REMOVE,
                (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
                target.getName()
        ), viewer);
    }

    public static void showPlayerNametagToAll(Player target) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showPlayerNametag(target, player);
        }
    }

    public static void modifyTabName(Player target, Player viewer, String component) {
        sendPacket(new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(
                                target.getUniqueId(),
                                target.getName()
                        ),
                        true,
                        5000,
                        GameMode.SURVIVAL,
                        MiniMessage.miniMessage().deserialize(component),
                        null
                )
        ), viewer);
    }

    public static void modifyTabName(Player target, Player viewer, Component component) {
        sendPacket(new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(
                                target.getUniqueId(),
                                target.getName()
                        ),
                        true,
                        5000,
                        GameMode.SURVIVAL,
                        component,
                        null
                )
        ), viewer);
    }

    public static void modifyDisplayedGamemode(Player target, Player viewer, GameMode gameMode) {
        sendPacket(new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(
                                target.getUniqueId(),
                                target.getName()
                        ),
                        true,
                        5000,
                        gameMode,
                        null,
                        null
                )
        ), viewer);
    }

    private static String getTeamName(Player player) {
        return "jarona_" + player.getEntityId();
    }

    private static void sendPacket(PacketWrapper<?> packet, Player player) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

}
