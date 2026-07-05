package pro.fazeclan.river.jarona.util;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

}
