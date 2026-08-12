package pro.fazeclan.river.jarona.party;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PartyManager {

    private final List<Party> parties;

    public PartyManager() {
        this.parties = new CopyOnWriteArrayList<>();
    }

    public Party createParty(Player leader) {
        var party = new Party(leader);
        parties.add(party);
        return party;
    }

    public void disbandParty(Player leader) {
        disbandParty(leader.getUniqueId());
    }

    public void disbandParty(UUID leader) {
        var potentialParty = parties.stream().filter(p -> p.getLeader().equals(leader)).findFirst();
        if (potentialParty.isEmpty()) {
            return;
        }
        var party = potentialParty.get();
        party.unqueueMembers();
        parties.remove(party);
    }

    public void disbandEmptyParties() {
        parties.removeIf(party -> party.getPlayers().isEmpty());
    }

    public boolean hostingParty(UUID player) {
        return parties.stream().anyMatch(party -> party.getLeader().equals(player));
    }

    public boolean hostingParty(Player player) {
        return hostingParty(player.getUniqueId());
    }

    public boolean isInParty(UUID player) {
        return parties.stream().anyMatch(party -> party.getMembers().contains(player));
    }

    public boolean isInParty(Player player) {
        return isInParty(player.getUniqueId());
    }

    public Party getParty(Player player) {
        return getParty(player.getUniqueId());
    }

    public Party getParty(UUID uuid) {
        return parties.stream().filter(party -> party.getMembers().contains(uuid)).findFirst().orElse(null);
    }

}
