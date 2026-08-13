package pro.fazeclan.river.jarona.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameValues {

    private final Map<String, Object> variables = new HashMap<>(32);

    public <T> T setValue(String name, T value) {
        this.variables.put(name, value);
        return value;
    }

    public <T> T getValue(String name) {
        return (T) this.variables.get(name);
    }

    public <T> T getValue(String name, T def) {
        return (T) this.variables.getOrDefault(name, def);
    }

    public void resetValues(UUID gameUUID) {
        this.variables.clear();
    }

    public void removeValue(String name) {
        this.variables.remove(name);
    }

}
