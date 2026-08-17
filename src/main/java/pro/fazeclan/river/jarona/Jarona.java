package pro.fazeclan.river.jarona;

import com.github.retrooper.packetevents.PacketEvents;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.tr7zw.nbtapi.NBT;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.jarona.command.*;
import pro.fazeclan.river.jarona.condition.ConditionManager;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.game.GameManager;
import pro.fazeclan.river.jarona.listener.PlayerListener;
import pro.fazeclan.river.jarona.map.GameMapManager;
import pro.fazeclan.river.jarona.party.PartyManager;
import pro.fazeclan.river.jarona.placeholder.JaronaExpansion;
import pro.fazeclan.river.jarona.queue.QueueManager;
import pro.fazeclan.river.jarona.stats.DatabaseManager;
import pro.fazeclan.river.jarona.stats.PlayerDataAccess;
import pro.fazeclan.river.jarona.stats.StatisticManager;
import pro.fazeclan.river.jarona.stats.StatisticWriteBuffer;
import pro.fazeclan.river.jarona.tablist.TablistManager;

import java.util.ArrayList;
import java.util.List;

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

    @Getter
    PartyManager partyManager;

    private DatabaseManager databaseManager;
    private StatisticWriteBuffer buffer;
    @Getter
    private StatisticManager statisticManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        this.gameManager = new GameManager();
        this.conditionManager = new ConditionManager();
        this.mapManager = new GameMapManager();
        this.tablistManager = new TablistManager();
        this.queueManager = new QueueManager();
        this.partyManager = new PartyManager();
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

        this.databaseManager = new DatabaseManager(getDataFolder());
        this.databaseManager.connect();

        var pda = new PlayerDataAccess(databaseManager);
        this.buffer = new StatisticWriteBuffer();
        this.statisticManager = new StatisticManager(databaseManager, pda, buffer);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.statisticManager.write(buffer.drain()), 600, 600);

        // commands
        List<LiteralArgumentBuilder<CommandSourceStack>> subcommands = new ArrayList<>();
        var command = Commands.literal("jarona");
        subcommands.add(ConditionCommand.command());
        subcommands.add(ConfigCommand.command());
        subcommands.add(MapCommand.command());
        subcommands.add(QueueCommand.command());
        subcommands.add(GameCommand.command());
        subcommands.add(PartyCommand.command());
        subcommands.add(NicknameCommand.command());
        subcommands.add(StatisticCommand.command());

        gameManager.register(new Game("Empty", Jarona.getKey("empty"), true) {
            @Override
            public void init(World world, List<Player> players) {

            }

            @Override
            public void tick(World world, List<Player> players) {

            }

            @Override
            public void end(World world, List<Player> players) {

            }
        });

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            // add each subcommand and register them
            subcommands.forEach(subcommand -> {
                command.then(subcommand);
                commands.registrar().register(subcommand.build());
            });

            // root command
            commands.registrar().register(command.build());
        });

        // config
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
        this.tablistManager.stopTask();

        statisticManager.write(buffer.drain());
        databaseManager.disconnect();
    }

    public static Jarona getInstance() {
        return JavaPlugin.getPlugin(Jarona.class);
    }

    public static NamespacedKey getKey(String value) {
        return new NamespacedKey("jarona", value);
    }

}
