package pro.fazeclan.river.jarona.condition;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConditionManager {

    @Getter
    private final ConcurrentHashMap<UUID, Conditions> playerConditionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Conditions> gameConditionMap = new ConcurrentHashMap<>();

    public Conditions getPlayerConditions(Player player) {
        return playerConditionMap.compute(player.getUniqueId(), (k, c) -> {
            if (c == null) {
                return new Conditions();
            }
            return c;
        });
    }

    public Conditions getGameConditions(UUID game) {
        return gameConditionMap.compute(game, (k, c) -> {
            if (c == null) {
                return new Conditions();
            }
            return c;
        });
    }

    public void initTasks() {
        new ConditionTask().runTaskTimerAsynchronously(Jarona.getInstance(), 5, 5);
    }

    @SuppressWarnings("unchecked")
    public class Conditions implements Iterable<Condition> {
        private final ConcurrentHashMap<String, Condition> nameConditionMap = new ConcurrentHashMap<>();

        public <T extends Condition> T getOrCreate(String key, T condition) {
            return (T) nameConditionMap.compute(key, (k, c) -> {
                if (c == null) {
                    return condition;
                }
                return c;
            });
        }

        public Condition get(String key) {
            return nameConditionMap.get(key);
        }

        public void remove(String key) {
            this.nameConditionMap.remove(key);
        }

        public Map<String, Condition> getConditionMap() {
            return nameConditionMap;
        }

        @Override
        public @NotNull Iterator<Condition> iterator() {
            return this.nameConditionMap.values().iterator();
        }
    }

    private class ConditionTask extends BukkitRunnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                StringBuilder actionbar = new StringBuilder();

                var worldName = player.getWorld().getKey().getKey();
                try {
                    var sortedGameConditions = getGameConditions(UUID.fromString(worldName)).getConditionMap().values().stream().sorted().toList();
                    for (Condition condition : sortedGameConditions) {
                        if (condition.getHudCondition().apply(condition)) {
                            if (condition.getHud() != null) {
                                if (!actionbar.isEmpty()) {
                                    actionbar.append(" <gray>||</gray> ");
                                }
                                actionbar.append(condition.getHud().apply(condition));
                            }
                        }
                    }
                } catch (Exception ignored) {}

                var sortedPlayerConditions = getPlayerConditions(player).getConditionMap().values().stream().sorted().toList();
                for (Condition condition : sortedPlayerConditions) {
                    if (condition.getHudCondition().apply(condition)) {
                        if (condition.getHud() != null) {
                            if (!actionbar.isEmpty()) {
                                actionbar.append(" <gray>||</gray> ");
                            }
                            actionbar.append(condition.getHud().apply(condition));
                        }
                    }
                }

                if (actionbar.isEmpty()) {
                    continue;
                }
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerActionBar(
                        MiniMessage.miniMessage().deserialize(actionbar.toString())
                ));
            }
        }

    }

}
