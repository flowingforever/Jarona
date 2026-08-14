package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class StatisticCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("statistic")
                .then(
                        Commands.argument("game", new GameArgument())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var plugin = Jarona.getInstance();
                                    var manager = plugin.getStatisticManager();
                                    var game = ctx.getArgument("game", Game.class);

                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<green>Your statistics on " + game.getName() + "! <red>[Note: This may take a bit to fetch!]</red></green>"
                                    ));
                                    manager.getAllStats(player.getUniqueId(), game.getKey()).thenAccept(map -> {
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            for (var entry : map.entrySet()) {
                                                player.sendMessage(ServerUtil.formatComponent(
                                                        "<yellow>" + entry.getKey() + ": " + entry.getValue() + "</yellow>"
                                                ));
                                            }
                                        });
                                    });


                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
