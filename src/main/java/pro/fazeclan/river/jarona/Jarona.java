package pro.fazeclan.river.jarona;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.game.GameManager;

public final class Jarona extends JavaPlugin {

    @Getter
    GameManager gameManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.gameManager = new GameManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Jarona getInstance() {
        return JavaPlugin.getPlugin(Jarona.class);
    }

    public static NamespacedKey getKey(String value) {
        return new NamespacedKey(getInstance(), value);
    }

}
