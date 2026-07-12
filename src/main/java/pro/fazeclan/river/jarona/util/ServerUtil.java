package pro.fazeclan.river.jarona.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ServerUtil {

    public static String readServerProperties(String property) {
        try {
            var properties = new Properties();
            properties.load(
                    new FileInputStream(
                            new File(Bukkit.getWorldContainer(), "server.properties")
                    )
            );
            return properties.getProperty(property);
        } catch (Exception e) {
            return null;
        }
    }

    public static Component formatComponent(String message) {
        return MiniMessage.miniMessage().deserialize(
                "<yellow>Jarona</yellow> <gray>></gray> " + message
        );
    }

}
