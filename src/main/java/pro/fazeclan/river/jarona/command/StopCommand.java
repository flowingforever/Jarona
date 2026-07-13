package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.util.GameUtil;

public class StopCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("stop")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.start"))
                .then(
                        Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    var targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                    var players = targetResolver.resolve(ctx.getSource());

                                    for (Player player : players) {
                                        GameUtil.endGame(player.getWorld());
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
