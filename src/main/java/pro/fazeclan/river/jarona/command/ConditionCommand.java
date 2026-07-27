package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;

public class ConditionCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("conditions")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.conditions"))
                .then(
                        Commands.literal("reset")
                                .executes(ctx -> {
                                    var manager = Jarona.getInstance().getConditionManager();
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        manager.getPlayerConditions(player).clear();
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.argument("players", ArgumentTypes.players())
                                        .executes(ctx -> {
                                            var targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                            var players = targetResolver.resolve(ctx.getSource());
                                            var manager = Jarona.getInstance().getConditionManager();
                                            for (Player player : players) {
                                                manager.getPlayerConditions(player).clear();
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                );
    }

}
