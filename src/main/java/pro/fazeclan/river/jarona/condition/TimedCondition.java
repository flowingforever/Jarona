package pro.fazeclan.river.jarona.condition;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;

import java.util.UUID;
import java.util.function.Function;

public class TimedCondition extends Condition {

    public enum Type {
        PLAYER_TICK, GAME_TICK, MILLIS
    }

    private final Type type;

    public TimedCondition(Type type) {
        this.type = type;
    }

    public TimedCondition(Type type, Function<Condition, String> hud) {
        this.type = type;
        this.hud = hud;
    }

    public TimedCondition(Type type, UUID playerUUID) {
        this.type = type;
        this.playerUUID = playerUUID;
    }

    public TimedCondition(Type type, Function<Condition, String> hud, UUID playerUUID) {
        this.type = type;
        this.hud = hud;
        this.playerUUID = playerUUID;
    }

    protected long endTime = -1;

    /**
     * Sets the duration of the condition based on the type of timed condition.
     * @param value The long representing the new duration of the timed condition.
     */
    public void setDuration(long value) {
        if (type == null) {
            this.endTime = -1;
        }
        this.endTime = switch (type) {
            case PLAYER_TICK -> getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) + value;
            case GAME_TICK -> Bukkit.getCurrentTick() + value;
            case MILLIS -> System.currentTimeMillis() + value;
        };
    }

    /**
     * Gets the duration of the condition based on the type of timed condition.
     * @return A long representing the duration
     */
    public long getDuration() {
        if (type == null) {
            return 0;
        }
        switch (type) {
            case PLAYER_TICK -> {
                return Math.max(0, this.endTime - getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE));
            }
            case GAME_TICK -> {
                return Math.max(0, this.endTime - Bukkit.getCurrentTick());
            }
            case MILLIS -> {
                return Math.max(0, this.endTime - System.currentTimeMillis());
            }
        }
        return 0;
    }

    @Override
    public boolean getAvailable() {
        if (endTime == -1 || type == null) {
            return false;
        }
        switch (type) {
            case PLAYER_TICK -> {
                return getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) > endTime;
            }
            case GAME_TICK -> {
                return Bukkit.getCurrentTick() > endTime;
            }
            case MILLIS -> {
                return System.currentTimeMillis() > endTime;
            }
        }
        return false;
    }

    @Override
    void reset() {
        this.endTime = -1;
    }

}
