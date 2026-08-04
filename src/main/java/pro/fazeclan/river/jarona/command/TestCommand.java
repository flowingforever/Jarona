package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.tablist.TablistCategories;
import pro.fazeclan.river.jarona.tablist.TablistCategory;
import pro.fazeclan.river.jarona.tablist.TablistEntry;
import pro.fazeclan.river.jarona.util.NametagUtil;

public class TestCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("test")
                .requires(ctx -> ctx.getSender().hasPermission("jarona.dev.test"))
                .then(
                        Commands.literal("tablist")
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player viewer)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    var categories = new TablistCategories(
                                            new TablistCategory(
                                                    NametagUtil.generateUsername(12),
                                                    Component.text("Example Team").color(NamedTextColor.RED),
                                                    viewer
                                            ),
                                            new TablistCategory(
                                                    NametagUtil.generateUsername(12),
                                                    Component.text("Example Team 2").color(NamedTextColor.GREEN),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12)))
                                            ),
                                            new TablistCategory(
                                                    NametagUtil.generateUsername(12),
                                                    Component.text("Example Team 3").color(NamedTextColor.BLUE),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12))),
                                                    new TablistEntry(NametagUtil.generateUsername(12), Component.text(NametagUtil.generateUsername(12)))
                                            )
                                    );
                                    categories.broadcastCategories(viewer);

                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
