package pro.fazeclan.river.jarona;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.condition.ConditionManager;
import pro.fazeclan.river.jarona.game.GameManager;

public final class Jarona extends JavaPlugin {

    @Getter
    ConditionManager conditionManager;

    @Getter
    GameManager gameManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        // Plugin startup logic
        this.gameManager = new GameManager();
        this.conditionManager = new ConditionManager();

        this.conditionManager.initTasks();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
    }

    public static Jarona getInstance() {
        return JavaPlugin.getPlugin(Jarona.class);
    }

    public static NamespacedKey getKey(String value) {
        return new NamespacedKey(getInstance(), value);
    }

}
