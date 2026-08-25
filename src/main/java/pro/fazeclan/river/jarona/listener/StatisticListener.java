package pro.fazeclan.river.jarona.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pro.fazeclan.river.jarona.Jarona;

public class StatisticListener implements Listener {

    @EventHandler
    private void handlePlayerJoin(PlayerJoinEvent event) {
        Jarona.getInstance().getStatisticManager().ensurePlayerTracked(event.getPlayer());
    }

}
