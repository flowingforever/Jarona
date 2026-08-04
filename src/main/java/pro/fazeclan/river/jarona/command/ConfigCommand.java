package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class ConfigCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("config")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.config"))
                .then(
                        Commands.literal("reload")
                                .executes(ctx -> {
                                    var jarona = Jarona.getInstance();
                                    jarona.reloadConfig();
                                    jarona.getConditionManager().reloadTask();

                                    ctx.getSource().getSender().sendMessage(ServerUtil.formatComponent(
                                            "Config reloaded."
                                    ));

                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
