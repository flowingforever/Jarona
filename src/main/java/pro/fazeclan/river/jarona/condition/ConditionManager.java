package pro.fazeclan.river.jarona.condition;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.util.ConditionUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConditionManager {

    @Getter
    private final ConcurrentHashMap<UUID, Conditions> playerConditionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Conditions> gameConditionMap = new ConcurrentHashMap<>();
    private BukkitTask task;

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
        var jarona = Jarona.getInstance();
        task = new ConditionTask().runTaskTimerAsynchronously(
                jarona,
                5,
                jarona.getConfig().getInt("condition-update-period", 5)
        );
    }

    public void stopTask() {
        task.cancel();
    }

    public void reloadTask() {
        var jarona = Jarona.getInstance();
        task.cancel();
        task = new ConditionTask().runTaskTimerAsynchronously(
                jarona,
                5,
                jarona.getConfig().getInt("condition-update-period", 5)
        );
    }

    @SuppressWarnings("unchecked")
    public static class Conditions implements Iterable<Condition> {
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

        public void clear() {
            this.nameConditionMap.clear();
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
                actionbar.append(ConditionUtil.worldConditionsToFormattedString(player.getWorld()));

                if (!actionbar.isEmpty()) {
                    actionbar.append(" <gray>◇</gray> ");
                }

                actionbar.append(ConditionUtil.conditionsToFormattedString(getPlayerConditions(player)));

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
