package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.command.argument.GameMapArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class GameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("game")
                .then(Commands.literal("start")
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
                                                            if (!map.isGameSupported(game)) {
                                                                ctx.getSource().getSender().sendMessage(ServerUtil.formatComponent(
                                                                        "<red>This map does not support this game!</red>"
                                                                ));
                                                            }

                                                            GameUtil.startGameWithMap(game.getKey(), map);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        ))
                .then(Commands.literal("stop")
                        .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.stop"))
                        .then(
                                Commands.argument("player", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            var targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                            var player = targetResolver.resolve(ctx.getSource()).getFirst();

                                            GameUtil.endGame(player.getWorld());

                                            return Command.SINGLE_SUCCESS;
                                        })
                        ))
                .then(Commands.literal("spectate")
                        .then(
                                Commands.argument("player", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                            var targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                            var gp = targetResolver.resolve(ctx.getSource()).getFirst();

                                            if (!GameUtil.hasGame(gp.getWorld())) {
                                                player.sendMessage(ServerUtil.formatComponent(
                                                        "<red>This player is not in a game!</red>"
                                                ));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            player.setGameMode(GameMode.SPECTATOR);
                                            player.teleport(gp);

                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
                );
    }

}
