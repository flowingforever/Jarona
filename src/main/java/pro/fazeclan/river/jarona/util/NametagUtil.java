package pro.fazeclan.river.jarona.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class NametagUtil {

    public static void hidePlayerNametag(Player target, Player viewer) {
        var name = getTeamName(target);
        var teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(),
                Component.empty(),
                Component.empty(),
                WrapperPlayServerTeams.NameTagVisibility.NEVER,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                null,
                WrapperPlayServerTeams.OptionData.NONE
        );
        sendPacket(new WrapperPlayServerTeams(
                name,
                WrapperPlayServerTeams.TeamMode.CREATE,
                teamInfo,
                target.getName()
        ), viewer);
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

    private static String getTeamName(Player player) {
        return "jarona_" + player.getEntityId();
    }

    private static void sendPacket(PacketWrapper<?> packet, Player player) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

}
