package pro.fazeclan.river.jarona.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

public class JaronaExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "jarona";
    }

    @Override
    public @NotNull String getAuthor() {
        return "riversflowing";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("game_condition")) {
            return ConditionUtil.worldConditionsToFormattedString(player.getWorld(), player);
        } else if (params.equals("nickname")) {
            return NicknameUtil.getNickname(player);
        }
        return null;
    }

}
