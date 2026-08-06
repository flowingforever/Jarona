package pro.fazeclan.river.jarona.condition;

import java.util.UUID;
import java.util.function.Function;

public class SwitchCondition extends Condition {

    final boolean initial;
    boolean on;

    public SwitchCondition(boolean on, Function<Condition, String> hud, UUID playerUUID) {
        this.on = on;
        this.initial = on;
        setHud(hud);
        this.playerUUID = playerUUID;
    }

    public SwitchCondition(boolean on, UUID playerUUID) {
        this.on = on;
        this.initial = on;
        this.playerUUID = playerUUID;
    }

    public SwitchCondition(boolean on) {
        this.on = on;
        this.initial = on;
    }

    @Override
    public boolean getAvailable() {
        return on;
    }

    public void setAvailable(boolean on) {
        this.on = false;
    }

    public void toggle() {
        this.on = !on;
    }

    @Override
    public void reset() {
        on = initial;
    }

}
