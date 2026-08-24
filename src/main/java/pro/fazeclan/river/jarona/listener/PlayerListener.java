package pro.fazeclan.river.jarona.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

public class PlayerListener implements Listener {

    @EventHandler
    private void handleOutsideGameChat(AsyncChatEvent event) {
        if (GameUtil.hasGame(event.getPlayer().getWorld())) {
            return;
        }
        if (!Jarona.getInstance().getConfig().getBoolean("chat-format.outside-game")) {
            return;
        }
        var mm = MiniMessage.miniMessage();
        event.renderer(
                (source, sourceDisplayName, message, viewer) ->
                        mm.deserialize("<head:" + source.getUniqueId() + "> <hover:show_text:'<gray>" + source.getName() + "</gray>'>" + NicknameUtil.getNickname(source) + "<reset><gray>: ")
                                .append(message.style(Style.style().build()).color(NamedTextColor.GRAY))
        );
    }

    @EventHandler
    private void handleInGameChat(AsyncChatEvent event) {
        if (!GameUtil.hasGame(event.getPlayer().getWorld())) {
            return;
        }
        if (!Jarona.getInstance().getConfig().getBoolean("chat-format.in-game")) {
            return;
        }
        var mm = MiniMessage.miniMessage();
        event.renderer(
                (source, sourceDisplayName, message, viewer) ->
                        mm.deserialize("<head:" + source.getUniqueId() + "> <hover:show_text:'<gray>" + source.getName() + "</gray>'>" + NicknameUtil.getNickname(source) + "<reset><gray>: ")
                                .append(message.style(Style.style().build()).color(NamedTextColor.GRAY))
        );
    }

    @EventHandler
    private void handlePlayerJoin(PlayerJoinEvent event) {
        Jarona.getInstance().getStatisticManager().ensurePlayerTracked(event.getPlayer());
    }

}
