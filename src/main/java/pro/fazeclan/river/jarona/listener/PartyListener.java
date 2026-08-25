package pro.fazeclan.river.jarona.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pro.fazeclan.river.jarona.Jarona;

public class PartyListener implements Listener {

    private final Jarona plugin;

    public PartyListener(Jarona jarona) {
        this.plugin = jarona;
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        var manager = plugin.getPartyManager();
        var player = event.getPlayer();
        if (manager.isInParty(player)) {
            var party = manager.getParty(player);
            if (manager.hostingParty(player)) {
                party.kickLeaderAndReassign();
            } else {
                party.kickPlayer(player);
            }
        }
    }

}
