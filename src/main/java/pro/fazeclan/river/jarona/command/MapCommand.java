package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class MapCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("map")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.start"))
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
                );
    }

}
