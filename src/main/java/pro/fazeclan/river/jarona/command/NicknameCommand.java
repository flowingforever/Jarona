package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.util.NicknameUtil;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class NicknameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("nickname")
                .then(
                        Commands.literal("set")
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                            var string = ctx.getArgument("name", String.class);
                                            NicknameUtil.setNickname(player, string);
                                            player.sendMessage(ServerUtil.formatComponent(
                                                    "<green>Your nickname has been set to " + string + "!</green>"
                                            ));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                )
                .then(
                        Commands.literal("setother")
                                .requires(ctx -> ctx.getSender().hasPermission("jarona.nickname.setother"))
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                                .executes(ctx -> {

                                                    var player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource())
                                                            .getFirst();
                                                    var string = ctx.getArgument("name", String.class);
                                                    NicknameUtil.setNickname(player, string);
                                                    ctx.getSource().getSender().sendMessage(ServerUtil.formatComponent(
                                                            "<green>Set " + player.getName() + "'s nickname to " + string + "!</green>"
                                                    ));
                                                    player.sendMessage(ServerUtil.formatComponent(
                                                            "<green>Your nickname has been set to " + string + "!</green>"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                )
                .then(
                        Commands.literal("reset")
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    NicknameUtil.resetNickname(player);
                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<green>Your nickname has been reset to your username!</green>"
                                    ));

                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.argument("player", ArgumentTypes.players())
                                        .requires(ctx -> ctx.getSender().hasPermission("jarona.nickname.reset.other"))
                                        .executes(ctx -> {
                                            var players = ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());

                                            for (var player : players) {
                                                NicknameUtil.resetNickname(player);
                                                player.sendMessage(ServerUtil.formatComponent(
                                                        "<green>Your nickname has been forcefully reset to your username!</green>"
                                                ));
                                            }
                                            ctx.getSource().getSender().sendMessage(ServerUtil.formatComponent(
                                                    "<green>Reset each players' nicknames to their usernames!"
                                            ));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                )
                .then(Commands.literal("edit")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                            var nickname = NicknameUtil.getNickname(player);
                            player.sendMessage(Component.text("Click here to edit your nickname!")
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.runCommand("/jarona:nickname set " + nickname)));

                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

}
