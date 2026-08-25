package pro.fazeclan.river.jarona.tablist;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.alexdev.unlimitednametags.api.UNTPaperAPI;
import org.alexdev.unlimitednametags.api.UntNametagManagerPaper;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.*;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.function.BiFunction;

public class TablistManager {

    private Closeable task;

    public void startTask() {
        this.task = SchedulingUtil.asyncInterval(10, 10, () -> {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                setTabHeaderFooter(viewer);
                for (Player target : Bukkit.getOnlinePlayers()) {
                    var game = GameUtil.getGame(target.getWorld());
                    setTabEntry(viewer, target, game);
                    setNametag(viewer, target, game);
                }
            }
        });
    }

    public void stopTask() {
        try {
            this.task.close();
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                resetTabHeaderFooter(viewer);
                for (Player target : Bukkit.getOnlinePlayers()) {
                    resetTabEntry(viewer, target);
                }
            }
        } catch (IOException ignored) {}
    }

    private void setTabEntry(Player viewer, Player target, @Nullable Game game) {
        String text = PlaceholderAPI.setPlaceholders(target, NicknameUtil.getNickname(target));
        if (game != null) {
            var gameValues = game.getGameValues(viewer.getWorld().getUID());
            QuadFunction<Player, Player, NameContext, GameValues, String> name = gameValues.getValue(
                    "name_" + target.getUniqueId(),
                    (t, v, nameContext, values) -> "%jarona_nickname%"
            );
            text = PlaceholderAPI.setPlaceholders(target, name.apply(target, viewer, NameContext.TABLIST, gameValues));
        }
        NametagUtil.modifyTabName(target, viewer, text);
    }

    private void setNametag(Player viewer, Player target, @Nullable Game game) {
        if (game != null) {
            var api = UNTPaperAPI.getInstance();
            var gameValues = game.getGameValues(viewer.getWorld().getUID());
            QuadFunction<Player, Player, NameContext, GameValues, String> name = gameValues.getValue(
                    "name_" + target.getUniqueId(),
                    (t, v, nameContext, values) -> "%jarona_nickname%"
            );
            api.setForcedNametag(
                    target,
                    viewer,
                    MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(
                                    target,
                                    name.apply(target, viewer, NameContext.NAMETAG, gameValues)
                            )
                    )
            );
        }
    }

    private void setTabHeaderFooter(Player viewer) {
        var worldPDC = viewer.getWorld().getPersistentDataContainer();
        var miniMessage = MiniMessage.miniMessage();
        boolean set = false;
        if (worldPDC.has(Jarona.getKey("tablist_header"))) {
            viewer.sendPlayerListHeader(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_header"), PersistentDataType.STRING)));
            set = true;
        }
        if (worldPDC.has(Jarona.getKey("tablist_footer"))) {
            viewer.sendPlayerListFooter(miniMessage.deserialize(worldPDC.get(Jarona.getKey("tablist_footer"), PersistentDataType.STRING)));
            set = true;
        }

        if (!set) {
            viewer.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
        }
    }

    private void resetTabEntry(Player viewer, Player target) {
        NametagUtil.modifyTabName(target, viewer, target.displayName());
    }

    private void resetTabHeaderFooter(Player viewer) {
        viewer.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
    }

}