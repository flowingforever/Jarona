package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.command.argument.GameMapArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

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
                                .then(
                                        Commands.argument("map", new GameMapArgument())
                                                .executes(ctx -> {
                                                    final Game game = ctx.getArgument("game", Game.class);
                                                    final GameMap map = ctx.getArgument("map", GameMap.class);
                                                    if (!game.isRequiresMap()) {
                                                        ctx.getSource().getSender().sendMessage(ServerUtil.formatComponent(
                                                                "<red>This game does not require a map! Please select another game that will use a map!</red>"
                                                        ));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    GameUtil.startGameWithMap(game.getKey(), map);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                );
    }

}
