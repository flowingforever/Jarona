package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class AchievementCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("achievements")
                .then(
                        Commands.argument("game", new GameArgument())
                                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                                        .executes(ctx -> {
                                            var source = ctx.getSource().getSender();
                                            var plugin = Jarona.getInstance();
                                            var manager = plugin.getAchievementManager();
                                            var game = ctx.getArgument("game", Game.class);
                                            var profiles = ctx.getArgument("player", PlayerProfileListResolver.class).resolve(ctx.getSource());
                                            var mm = MiniMessage.miniMessage();

                                            for (var profile : profiles) {
                                                if (profile.getId() == null) {
                                                    continue;
                                                }
                                                source.sendMessage(ServerUtil.formatComponent(
                                                        "<green>" + profile.getName() + "'s achievements on " + game.getName() + "!<newline><red>[Note: This may take a bit to fetch!]</red></green>"
                                                ));
                                                manager.getAllAchievementProgress(profile.getId(), game.getKey()).thenAccept(map -> {
                                                    for (var entry : map.entrySet()) {
                                                        var display = game.getAchievementDefinitions().stream()
                                                                .filter(definition -> definition.key().equals(entry.getKey()))
                                                                .findFirst().orElse(null);
                                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                                            assert display != null;
                                                            source.sendMessage(mm.deserialize("<yellow>" + display.display() + ": " + entry.getValue() + "</yellow>"));
                                                        });
                                                    }
                                                });
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var plugin = Jarona.getInstance();
                                    var manager = plugin.getAchievementManager();
                                    var game = ctx.getArgument("game", Game.class);
                                    var mm = MiniMessage.miniMessage();

                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<green>Your achievements on " + game.getName() + "!<newline><red>[Note: This may take a bit to fetch!]</red></green>"
                                    ));
                                    manager.getAllAchievementProgress(player.getUniqueId(), game.getKey()).thenAccept(map -> {
                                        for (var entry : map.entrySet()) {
                                            var display = game.getAchievementDefinitions().stream()
                                                    .filter(definition -> definition.key().equals(entry.getKey()))
                                                    .findFirst().orElse(null);
                                            Bukkit.getScheduler().runTask(plugin, () -> {
                                                assert display != null;
                                                if (entry.getValue() >= display.max()) {
                                                    player.sendMessage(mm.deserialize("<yellow>" + display.display() + ": <green>Completed!</green></yellow>"));
                                                } else {
                                                    player.sendMessage(mm.deserialize("<yellow>" + display.display() + ": <red>[" + entry.getValue() + "/" + display.max() + "]</red></yellow>"));
                                                }
                                            });
                                        }
                                    });


                                    return Command.SINGLE_SUCCESS;
                                })
                );
    }

}
