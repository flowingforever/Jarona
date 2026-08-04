package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.jarona.Jarona;

public class ConfigCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("config")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.admin.config"))
                .then(
                        Commands.literal("reload")
                                .executes(ctx -> {
                                    Jarona.getInstance().reloadConfig();

                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
