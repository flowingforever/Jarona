package pro.fazeclan.river.jarona.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.command.argument.GameArgument;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.party.Party;
import pro.fazeclan.river.jarona.util.ServerUtil;

public class PartyCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("party")
                .then(Commands.literal("invite")
                        .then(Commands.argument("players", ArgumentTypes.players())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player leader)) return Command.SINGLE_SUCCESS;

                                    var manager = Jarona.getInstance().getPartyManager();
                                    Party party = null;
                                    if (!manager.hostingParty(leader)) {
                                        if (manager.isInParty(leader)) {
                                            leader.sendMessage(ServerUtil.formatComponent(
                                                    "<red>You cannot do this while you aren't the party leader!</red>"
                                            ));
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        party = manager.createParty(leader);
                                    }

                                    if (party == null) {
                                        party = manager.getParty(leader);
                                    }

                                    var targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                    var players = targetResolver.resolve(ctx.getSource());

                                    for (var player : players) {
                                        party.invitePlayer(player);
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<yellow>You've been invited to " + leader.getName() + "'s party! <click:run_command:'/jarona:party accept " + leader.getName() + "'><b><green>Click here to join!</green></b></click>"
                                        ));
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<yellow>You've invited " + player.getName() + " to your party!</yellow>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("accept")
                        .then(Commands.argument("leader", ArgumentTypes.player())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var targetResolver = ctx.getArgument("leader", PlayerSelectorArgumentResolver.class);
                                    var leader = targetResolver.resolve(ctx.getSource()).getFirst();

                                    var manager = Jarona.getInstance().getPartyManager();
                                    if (!manager.hostingParty(leader)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>This player is not hosting a party!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    var party = manager.getParty(leader);
                                    if (party == null) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>This party does not exist or has not been initialized yet! Please try again if the latter.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (!party.hasInvitedPlayer(player)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>This party has not invited you.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    party.addPlayer(player);
                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<green>You've joined " + leader.getName() + "'s party!</green>"
                                    ));

                                    var partyPlayers = party.getPlayers();
                                    for (var member : partyPlayers) {
                                        member.sendMessage(ServerUtil.formatComponent(
                                                "<green>" + player.getName() + " has joined the party! [" + partyPlayers.size() + " players]</green>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("kick")
                        .then(Commands.argument("players", ArgumentTypes.players())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player leader)) return Command.SINGLE_SUCCESS;

                                    var targetResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                    var players = targetResolver.resolve(ctx.getSource());

                                    var manager = Jarona.getInstance().getPartyManager();
                                    if (!manager.hostingParty(leader)) {
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not hosting a party!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    var party = manager.getParty(leader);
                                    if (party == null) {
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<red>This party does not exist or has not been initialized yet! Please try again if the latter.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    for (var player : players) {
                                        party.kickPlayer(player);
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You've been kicked from " + leader.getName() + "'s party!</red>"
                                        ));

                                        var partyPlayers = party.getPlayers();
                                        for (var member : partyPlayers) {
                                            member.sendMessage(ServerUtil.formatComponent(
                                                    "<red>" + player.getName() + " has been kicked from the party! [" + partyPlayers.size() + " players]</red>"
                                            ));
                                        }
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("queue")
                        .then(Commands.argument("game", new GameArgument())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player leader)) return Command.SINGLE_SUCCESS;

                                    var manager = Jarona.getInstance().getPartyManager();
                                    if (!manager.isInParty(leader)) {
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not in a party!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (!manager.hostingParty(leader)) {
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not the party leader!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    var game = ctx.getArgument("game", Game.class);
                                    var party = manager.getParty(leader);
                                    if (game.getKey().equals(Jarona.getKey("empty"))) {
                                        leader.sendMessage(ServerUtil.formatComponent(
                                                "<red>This game is not queuable.</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    party.queueMembers(game);
                                    for (var member : party.getPlayers()) {
                                        member.sendMessage(ServerUtil.formatComponent(
                                                "<green>The party has been put into the queue for " + game.getName() + "!</green>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("unqueue")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player leader)) return Command.SINGLE_SUCCESS;

                            var manager = Jarona.getInstance().getPartyManager();
                            if (!manager.isInParty(leader)) {
                                leader.sendMessage(ServerUtil.formatComponent(
                                        "<red>You are not in a party!</red>"
                                ));
                                return Command.SINGLE_SUCCESS;
                            }
                            if (!manager.hostingParty(leader)) {
                                leader.sendMessage(ServerUtil.formatComponent(
                                        "<red>You are not the party leader!</red>"
                                ));
                                return Command.SINGLE_SUCCESS;
                            }

                            var party = manager.getParty(leader);
                            party.unqueueMembers();
                            for (var member : party.getPlayers()) {
                                member.sendMessage(ServerUtil.formatComponent(
                                        "<red>The party has been removed from the queue!</red>"
                                ));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player member)) return Command.SINGLE_SUCCESS;

                            var mm = MiniMessage.miniMessage();
                            var manager = Jarona.getInstance().getPartyManager();
                            if (!manager.isInParty(member)) {
                                member.sendMessage(ServerUtil.formatComponent(
                                        "<red>You are not in a party!</red>"
                                ));
                                return Command.SINGLE_SUCCESS;
                            }
                            var party = manager.getParty(member);

                            member.sendMessage(ServerUtil.formatComponent(
                                    "<green>Party Members [" + party.getMembers().size() + "]</green>"
                            ));
                            var leader = party.getLeaderPlayer();
                            if (leader == null || !leader.isOnline()) {
                                member.sendMessage(mm.deserialize(
                                        "<blue>The party leader is offline. No games can be queued.</blue>"
                                ));
                            } else {
                                member.sendMessage(mm.deserialize(
                                        "<blue> - " + leader.getName() + " (Party Leader)</blue>"
                                ));
                            }

                            for (var player : party.getPlayers()) {
                                if (player.equals(leader)) {
                                    continue;
                                }

                                member.sendMessage(mm.deserialize(
                                        "<green> - " + player.getName() + "</green>"
                                ));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("open")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player leader)) return Command.SINGLE_SUCCESS;

                            var manager = Jarona.getInstance().getPartyManager();
                            if (!manager.hostingParty(leader)) {
                                leader.sendMessage(ServerUtil.formatComponent(
                                        "<red>You are not the party leader!</red>"
                                ));
                                return Command.SINGLE_SUCCESS;
                            }
                            var party = manager.getParty(leader);
                            party.setOpen(!party.isOpen());
                            if (party.isOpen()) {
                                leader.sendMessage(ServerUtil.formatComponent(
                                        "<green>Your party is now open! People can now join your party using <b>/party join " + leader.getName() + "</b>!</green>"
                                ));
                            } else {
                                leader.sendMessage(ServerUtil.formatComponent(
                                        "<red>Your party is now invite only!</red>"
                                ));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("join")
                        .then(Commands.argument("leader", ArgumentTypes.player())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var targetResolver = ctx.getArgument("leader", PlayerSelectorArgumentResolver.class);
                                    var leader = targetResolver.resolve(ctx.getSource()).getFirst();

                                    var manager = Jarona.getInstance().getPartyManager();
                                    if (manager.isInParty(player)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are in a party already!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (!manager.hostingParty(leader)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>This player is not hosting a party!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    var party = manager.getParty(leader);
                                    if (!party.isOpen()) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>That party is not open!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    party.addPlayer(player);
                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<green>You've joined " + leader.getName() + "'s party!</green>"
                                    ));

                                    var partyPlayers = party.getPlayers();
                                    for (var member : partyPlayers) {
                                        member.sendMessage(ServerUtil.formatComponent(
                                                "<green>" + player.getName() + " has joined the party! [" + partyPlayers.size() + " players]</green>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("leave")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                            var manager = Jarona.getInstance().getPartyManager();
                            if (!manager.isInParty(player)) {
                                player.sendMessage(ServerUtil.formatComponent(
                                        "<red>You are not in a party!</red>"
                                ));
                                return Command.SINGLE_SUCCESS;
                            }

                            var party = manager.getParty(player);
                            if (manager.hostingParty(player)) {
                                party.kickLeaderAndReassign();

                                var leader = party.getLeaderPlayer();
                                if (leader != null) {
                                    leader.sendMessage(ServerUtil.formatComponent(
                                            "<green>You have been assigned as the new party leader!</green>"
                                    ));

                                    for (var member : party.getPlayers()) {
                                        member.sendMessage(ServerUtil.formatComponent(
                                                "<green>" + leader.getName() + " has been assigned as the new party leader!</green>"
                                        ));
                                    }
                                } else {
                                    manager.disbandEmptyParties();
                                    player.sendMessage(ServerUtil.formatComponent(
                                            "<red>The party has been disbanded as there were no players left.</red>"
                                    ));
                                }
                            } else {
                                party.kickPlayer(player);
                            }

                            player.sendMessage(ServerUtil.formatComponent(
                                    "<red>You have left the party!</red>"
                            ));
                            for (var member : party.getPlayers()) {
                                member.sendMessage(ServerUtil.formatComponent(
                                        "<red>" + player.getName() + " has left the party!</red>"
                                ));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("transfer")
                        .then(Commands.argument("leader", ArgumentTypes.player())
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) return Command.SINGLE_SUCCESS;

                                    var manager = Jarona.getInstance().getPartyManager();
                                    if (!manager.isInParty(player)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not in a party!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if (!manager.hostingParty(player)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>You are not the party leader!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    var targetResolver = ctx.getArgument("leader", PlayerSelectorArgumentResolver.class);
                                    var leader = targetResolver.resolve(ctx.getSource()).getFirst();

                                    var party = manager.getParty(player);
                                    var compare = manager.getParty(leader);
                                    if (!party.equals(compare)) {
                                        player.sendMessage(ServerUtil.formatComponent(
                                                "<red>This player isn't in the same party as you!</red>"
                                        ));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    party.setLeader(leader);
                                    leader.sendMessage(ServerUtil.formatComponent(
                                            "<green>The party has been transferred over to you!</green>"
                                    ));
                                    for (var member : party.getPlayers()) {
                                        member.sendMessage(ServerUtil.formatComponent(
                                                "<green>" + leader.getName() + " has been assigned as the new party leader!</green>"
                                        ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }

}
