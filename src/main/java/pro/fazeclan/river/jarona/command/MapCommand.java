package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.map.GameMapEditorSession;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class MapCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("map")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.map"))
                .then(
                        Commands.literal("reload")
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
                                .then(
                                        Commands.literal("setname")
                                                .then(Commands.argument("name", StringArgumentType.string())
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
                                                .then(Commands.argument("credit", StringArgumentType.string())
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
                                                .then(Commands.argument("entry", StringArgumentType.string())
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
