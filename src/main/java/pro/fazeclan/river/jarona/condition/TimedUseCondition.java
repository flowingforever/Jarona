package pro.fazeclan.river.jarona.condition;

import java.util.UUID;
import java.util.function.Function;

public class TimedUseCondition extends TimedCondition {

    protected int maxUses = 0;

    public TimedUseCondition(Type type) {
        super(type);
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud) {
        super(type, hud);
    }

    public TimedUseCondition(Type type, UUID playerUUID) {
        super(type, playerUUID);
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud, UUID playerUUID) {
        super(type, hud, playerUUID);
    }

    public TimedUseCondition(Type type, int maxUses) {
        super(type);
        this.maxUses = maxUses;
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud, int maxUses) {
        super(type, hud);
        this.maxUses = maxUses;
    }

    public TimedUseCondition(Type type, UUID playerUUID, int maxUses) {
        super(type, playerUUID);
        this.maxUses = maxUses;
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud, UUID playerUUID, int maxUses) {
        super(type, hud, playerUUID);
        this.maxUses = maxUses;
    }

    protected int uses = 0;

    @Override
    public boolean getAvailable() {
        return super.getAvailable() && (uses < maxUses);
    }

    /**
     * Sets the amount of uses directly.
     * @param value New value representing the uses
     */
    public void setUses(int value) {
        this.uses = value;
    }

    /**
     * Gets the amount of uses under this condition.
     * @return The uses
     */
    public int getUses() {
        return uses;
    }

    /**
     * Increments the amount of uses by one.
     */
    public void increaseUses() {
        this.uses++;
    }

    /**
     * Sets the max amount of uses under this condition.
     * @param value New value representing the max uses
     */
    public void setMaxUses(int value) {
        this.maxUses = value;
    }

    /**
     * Gets the max amount of uses under this condition.
     * @return The max uses
     */
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    void reset() {
        super.reset();
        this.uses = 0;
    }
}
