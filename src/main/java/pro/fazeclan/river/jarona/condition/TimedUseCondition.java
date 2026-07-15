package pro.fazeclan.river.jarona.condition;

import lombok.Getter;
import lombok.Setter;
import pro.fazeclan.river.jarona.Jarona;

import java.util.UUID;
import java.util.function.Function;

public class TimedUseCondition extends TimedCondition {

    /**
     * -- GETTER --
     *  Gets the max amount of uses under this condition.
     * -- SETTER --
     *  Sets the max amount of uses under this condition.
     *
     * @return The max uses
     * @param value New value representing the max uses
     */
    @Setter
    @Getter
    protected int maxUses;

    public TimedUseCondition(Type type) {
        super(type);
        this.maxUses = 1;
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud) {
        super(type, hud);
        this.maxUses = 1;
    }

    public TimedUseCondition(Type type, UUID playerUUID) {
        super(type, playerUUID);
        this.maxUses = 1;
    }

    public TimedUseCondition(Type type, Function<Condition, String> hud, UUID playerUUID) {
        super(type, hud, playerUUID);
        this.maxUses = 1;
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

    /**
     * -- SETTER --
     *  Sets the amount of uses directly.
     * -- GETTER --
     *  Gets the amount of uses under this condition.
     *
     * @param value New value representing the uses
     * @return The uses
     */
    @Getter
    @Setter
    protected int uses = 0;

    @Override
    public boolean getAvailable() {
        return super.getAvailable() && uses < maxUses;
    }

    /**
     * Increments the amount of uses by one.
     */
    public void increaseUses() {
        this.uses++;
    }

    @Override
    public void reset() {
        super.reset();
        this.uses = 0;
    }
}
