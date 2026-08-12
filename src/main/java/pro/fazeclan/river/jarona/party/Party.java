package pro.fazeclan.river.jarona.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Party {

    @Getter
    private UUID leader;

    @Getter
    private final Set<UUID> members;

    @Getter
    private final Set<UUID> invited;

    @Getter
    @Setter
    private boolean open;

    public Party(Player leader) {
        this.open = false;
        this.members = new HashSet<>(16);
        this.invited = new HashSet<>(16);
        this.leader = leader.getUniqueId();
        members.add(leader.getUniqueId());
    }

    public Party(UUID leader) {
        this.open = false;
        this.members = new HashSet<>(16);
        this.invited = new HashSet<>(16);
        this.leader = leader;
        members.add(leader);
    }

    public Party(Player leader, Player... players) {
        this.open = false;
        this.members = new HashSet<>(players.length + 1);
        this.invited = new HashSet<>(16);
        this.leader = leader.getUniqueId();
        members.add(leader.getUniqueId());
        for (var player : players) {
            members.add(player.getUniqueId());
        }
    }

    public Party(UUID leader, Player... players) {
        this.open = false;
        this.members = new HashSet<>(players.length + 1);
        this.invited = new HashSet<>(16);
        this.leader = leader;
        members.add(leader);
        for (var player : players) {
            members.add(player.getUniqueId());
        }
    }

    public Party(Player leader, UUID... players) {
        this.open = false;
        this.members = new HashSet<>(players.length + 1);
        this.invited = new HashSet<>(16);
        this.leader = leader.getUniqueId();
        members.add(leader.getUniqueId());
        members.addAll(Arrays.asList(players));
    }

    public Party(UUID leader, UUID... players) {
        this.open = false;
        this.members = new HashSet<>(players.length + 1);
        this.invited = new HashSet<>(16);
        this.leader = leader;
        members.add(leader);
        members.addAll(Arrays.asList(players));
    }

    public List<Player> getPlayers() {
        return members.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    public void setLeader(Player player) {
        setLeader(player.getUniqueId());
    }

    public void setLeader(UUID player) {
        this.leader = player;
    }

    public Player getLeaderPlayer() {
        if (leader == null) return null;
        return Bukkit.getPlayer(leader);
    }

    public void queueMembers(Game game) {
        queueMembers(game.getKey());
    }

    public void queueMembers(NamespacedKey gameKey) {
        var manager = Jarona.getInstance().getQueueManager();
        for (var player : getPlayers()) {
            manager.queuePlayer(player, gameKey);
        }
    }

    public void unqueueMembers() {
        var manager = Jarona.getInstance().getQueueManager();
        for (var player : getPlayers()) {
            manager.unqueuePlayer(player);
        }
    }

    public void invitePlayer(UUID uuid) {
        this.invited.add(uuid);
    }

    public void invitePlayer(Player player) {
        invitePlayer(player.getUniqueId());
    }

    public boolean hasInvitedPlayer(UUID uuid) {
        return this.invited.contains(uuid);
    }

    public boolean hasInvitedPlayer(Player player) {
        return hasInvitedPlayer(player.getUniqueId());
    }

    public void addPlayer(UUID uuid) {
        this.invited.remove(uuid);
        this.members.add(uuid);
    }

    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
    }

    public void kickPlayer(UUID uuid) {
        this.invited.remove(uuid);
        this.members.remove(uuid);
    }

    public void kickPlayer(Player player) {
        kickPlayer(player.getUniqueId());
    }

    public UUID kickLeaderAndReassign() {
        this.members.remove(this.leader);
        var players = getPlayers();
        if (!players.isEmpty()) {
            this.leader = players.get(ThreadLocalRandom.current().nextInt(players.size())).getUniqueId();
        } else {
            this.leader = null;
        }
        return this.leader;
    }

}
