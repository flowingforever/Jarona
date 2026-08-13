package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.command.argument.GameMapArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;
import pro.fazeclan.river.jarona.map.GameMapEditorSession;
import pro.fazeclan.river.jarona.screen.MapVotingScreen;
import pro.fazeclan.river.jarona.util.ServerUtil;
import pro.fazeclan.river.jarona.util.WorldUtil;

import java.io.IOException;

public class MapCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("map")
                .then(
                        Commands.literal("vote")
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var queue = Jarona.getInstance().getQueueManager();
                                    var queuedPlayer = queue.getQueuedPlayer(player);
                                    if (queuedPlayer == null) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not in the queue.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    MapVotingScreen.handleScreen(queuedPlayer, null);

                                    return Command.SINGLE_SUCCESS;
                                })
                )
                .then(
                        Commands.literal("reload")
                                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.map.reload"))
                                .executes(ctx -> {
                                    var manager = Jarona.getInstance().getMapManager();
                                    manager.reloadRegistry();
                                    ctx.getSource().getSender()
                                            .sendMessage(ServerUtil.formatComponent(
                                                    "<yellow>The map registry has been reloaded!</yellow>"
                                            ));

                                    return Command.SINGLE_SUCCESS;
                                })
                )
                .then(
                        Commands.literal("editor")
                                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.map.editor"))
                                .then(
                                        Commands.literal("loadmap")
                                                .then(Commands.argument("map", new GameMapArgument())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var map = ctx.getArgument("map", GameMap.class);
                                                            var worldFolder = Jarona.getInstance().getServer().getLevelDirectory()
                                                                    .toAbsolutePath()
                                                                    .resolve("dimensions/jarona/" + map.id())
                                                                    .toFile();
                                                            if (!worldFolder.exists()) {
                                                                worldFolder.mkdirs();
                                                                try {
                                                                    FileUtils.copyDirectory(map.world(), worldFolder);
                                                                } catch (IOException e) {
                                                                    Jarona.getInstance().getLogger().warning("The world may not have been entirely created.");
                                                                }
                                                            }

                                                            var world = WorldUtil.createWorld(Jarona.getKey(map.id()));
                                                            player.teleport(map.spawn().toLocation(world));
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>Loaded map " + map.name() + "!</green>"
                                                            ));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("setname")
                                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var session = new GameMapEditorSession(player.getWorld());
                                                            var name = ctx.getArgument("name", String.class);
                                                            session.setName(name);
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>Set map name to " + name + "!</green>"
                                                            ));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("setcredit")
                                                .then(Commands.argument("credit", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var session = new GameMapEditorSession(player.getWorld());
                                                            var credit = ctx.getArgument("credit", String.class);
                                                            session.setCredit(credit);
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>Set map credit to " + credit + "!</green>"
                                                            ));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("addconfigentry")
                                                .then(Commands.argument("entry", StringArgumentType.greedyString())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var session = new GameMapEditorSession(player.getWorld());
                                                            var entry = ctx.getArgument("entry", String.class);
                                                            session.addConfigEntry(entry, player);
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>Added " + entry + " to the config!</green>"
                                                            ));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("setspawn")
                                                .executes(ctx -> {
                                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                    var session = new GameMapEditorSession(player.getWorld());
                                                    session.setSpawn(player.getLocation());
                                                    player.sendMessage(ServerUtil.formatComponent(
                                                            "<green>Set map spawn to your current position!</green>"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("addsupportedgame")
                                                .then(Commands.argument("game", new GameArgument())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var session = new GameMapEditorSession(player.getWorld());
                                                            session.addSupportedGame(ctx.getArgument("game", Game.class));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("removesupportedgame")
                                                .then(Commands.argument("game", new GameArgument())
                                                        .executes(ctx -> {
                                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                            var session = new GameMapEditorSession(player.getWorld());
                                                            session.removeSupportedGame(ctx.getArgument("game", Game.class));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                )
                                .then(
                                        Commands.literal("removeconfigentry")
                                                .executes(ctx -> {
                                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                    var session = new GameMapEditorSession(player.getWorld());
                                                    player.sendMessage(ServerUtil.formatComponent(
                                                            "<green>Removed " + session.removePreviousConfigEntry() + " from the config!</green>"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("save")
                                                .executes(ctx -> {
                                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                                    var session = new GameMapEditorSession(player.getWorld());
                                                    session.saveAsMap();
                                                    player.sendMessage(ServerUtil.formatComponent(
                                                            "<green>Saved map " + session.getName() + "!</green>"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                );
    }

}
