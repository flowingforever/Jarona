package pro.fazeclan.river.jarona.util;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.condition.Condition;
import pro.fazeclan.river.jarona.condition.ConditionManager;

import javax.annotation.Nullable;
import java.util.UUID;

public class ConditionUtil {

    @Nullable
    public static ConditionManager.Conditions getWorldConditions(World world) {
        try {
            return Jarona.getInstance().getConditionManager().getGameConditions(UUID.fromString(world.getKey().value()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ConditionManager.Conditions getPlayerConditions(Player player) {
        return Jarona.getInstance().getConditionManager().getPlayerConditions(player);
    }

    public static String worldConditionsToFormattedString(World world) {
        var text = new StringBuilder();
        var manager = Jarona.getInstance().getConditionManager();
        var worldName = world.getKey().getKey();
        try {
            text.append(conditionsToFormattedString(manager.getGameConditions(UUID.fromString(worldName))));
        } catch (Exception ignored) {
            return "";
        }
        return text.toString();
    }

    public static String conditionsToFormattedString(ConditionManager.Conditions conditions) {
        var sortedConditions = conditions.getConditionMap().values().stream().sorted().toList();
        var builder = new StringBuilder();
        for (Condition condition : sortedConditions) {
            if (condition.getHudCondition().apply(condition)) {
                if (condition.getHud() != null) {
                    if (!builder.isEmpty()) {
                        builder.append(" <gray>◇</gray> ");
                    }
                    builder.append(condition.getHud().apply(condition));
                }
            }
        }
        return builder.toString();
    }

}
