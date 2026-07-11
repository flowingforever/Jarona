package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.GameUtil;

public class StartCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("start")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.start"))
                .then(
                        Commands.argument("game", new GameArgument())
                                .executes(ctx -> {
                                    final Game game = ctx.getArgument("game", Game.class);

                                    GameUtil.startGame(game.getKey(), game.isVoidWorld());

                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
