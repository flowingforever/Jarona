package pro.fazeclan.river.jarona;

import com.github.retrooper.packetevents.PacketEvents;
import de.tr7zw.nbtapi.NBT;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.command.*;
import pro.fazeclan.river.jarona.condition.ConditionManager;
import pro.fazeclan.river.jarona.game.GameManager;
import pro.fazeclan.river.jarona.map.GameMapManager;
import pro.fazeclan.river.jarona.placeholder.JaronaExpansion;
import pro.fazeclan.river.jarona.queue.QueueManager;
import pro.fazeclan.river.jarona.tablist.TablistManager;

public final class Jarona extends JavaPlugin {

    @Getter
    ConditionManager conditionManager;

    @Getter
    GameManager gameManager;

    @Getter
    GameMapManager mapManager;

    @Getter
    TablistManager tablistManager;

    @Getter
    QueueManager queueManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        this.gameManager = new GameManager();
        this.conditionManager = new ConditionManager();
        this.mapManager = new GameMapManager();
        this.tablistManager = new TablistManager();
        this.queueManager = new QueueManager();
    }

    @Override
    public void onEnable() {
        // dependencies
        PacketEvents.getAPI().init();
        NBT.preloadApi();
        new JaronaExpansion().register();

        // managers
        this.conditionManager.initTasks();
        this.mapManager.reloadRegistry();
        this.tablistManager.startTask();
        this.queueManager.startLoop();

        // commands
        var command = Commands.literal("jarona")
                .then(StartCommand.command())
                .then(MapCommand.command())
                .then(QueueCommand.command())
                .then(StopCommand.command())
                .then(ConditionCommand.command())
                .then(ConfigCommand.command())
                .build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(command);
        });

        // config
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
        this.tablistManager.stopTask();
    }

    public static Jarona getInstance() {
        return JavaPlugin.getPlugin(Jarona.class);
    }

    public static NamespacedKey getKey(String value) {
        return new NamespacedKey("jarona", value);
    }

}
