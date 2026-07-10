package pro.fazeclan.river.jarona;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.command.StartCommand;
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

        this.gameManager = new GameManager();
        this.conditionManager = new ConditionManager();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        // Plugin startup logic
        this.conditionManager.initTasks();

        var command = Commands.literal("jarona")
                .then(StartCommand.command())
                .build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(command);
        });
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
