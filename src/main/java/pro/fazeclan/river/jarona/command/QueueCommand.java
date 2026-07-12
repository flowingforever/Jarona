package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class QueueCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("queue")
                .then(
                        Commands.argument("game", new GameArgument())
                                .executes(ctx -> {
                                    var source = ctx.getSource().getSender();
                                    if (!(source instanceof Player player)) {
                                        source.sendMessage(ServerUtil.formatComponent(
                                                "<red>A player must be executing this command.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    var manager = Jarona.getInstance().getQueueManager();
                                    var game = ctx.getArgument("game", Game.class);

                                    if (manager.isQueued(player, game)) {
                                        manager.unqueuePlayer(player);
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You have been unqueued for " + game.getKey() + "!</red>"
                                        ));
                                    } else {
                                        manager.queuePlayer(player, game);
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<green>You have been queued for " + game.getKey() + "!</green>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(
                                        Commands.argument("players", ArgumentTypes.players())
                                                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.queue"))
                                                .executes(ctx -> {
                                                    var source = ctx.getSource().getSender();
                                                    var targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                                    var players = targetResolver.resolve(ctx.getSource());
                                                    var manager = Jarona.getInstance().getQueueManager();
                                                    var game = ctx.getArgument("game", Game.class);

                                                    for (Player player : players) {
                                                        if (manager.isQueued(player, game)) {
                                                            manager.unqueuePlayer(player);
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<red>You have been unqueued for " + game.getKey() + "!</red>"
                                                            ));
                                                            source.sendMessage(ServerUtil.formatComponent(
                                                                    "<red>" + player.getName() + " has been unqueued for " + game.getKey() + "!</red>"
                                                            ));
                                                        } else {
                                                            manager.queuePlayer(player, game);
                                                            player.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>You have been queued for " + game.getKey() + "!</green>"
                                                            ));
                                                            source.sendMessage(ServerUtil.formatComponent(
                                                                    "<green>" + player.getName() + " has been queued for " + game.getKey() + "!</green>"
                                                            ));
                                                        }
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("list")
                                                .executes(ctx -> {
                                                    var source = ctx.getSource().getSender();
                                                    var game = ctx.getArgument("game", Game.class);
                                                    var manager = Jarona.getInstance().getQueueManager();
                                                    var minimessage = MiniMessage.miniMessage();

                                                    source.sendMessage(ServerUtil.formatComponent(
                                                            "<green>Here are the queued players for " + game.getKey() + "!</green>"
                                                    ));
                                                    for (Player player : manager.getPlayersQueued(game)) {
                                                        source.sendMessage(minimessage.deserialize(
                                                                "- <yellow>" + player.getName() + "</yellow>"
                                                        ));
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                );
    }

}
