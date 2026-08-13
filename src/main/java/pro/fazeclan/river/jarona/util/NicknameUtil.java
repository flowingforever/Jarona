package pro.fazeclan.river.jarona.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;

public class NicknameUtil {

    public static void setNickname(Player player, String miniMessageString) {
        player.getPersistentDataContainer().set(
                Jarona.getKey("nickname"),
                PersistentDataType.STRING,
                miniMessageString
        );
    }

    public static void resetNickname(Player player) {
        player.getPersistentDataContainer().remove(Jarona.getKey("nickname"));
    }

    public static String getNickname(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                Jarona.getKey("nickname"),
                PersistentDataType.STRING,
                player.getName()
        );
    }

    public static Component getNicknameComponent(Player player) {
        return MiniMessage.miniMessage().deserialize(getNickname(player));
    }

}
