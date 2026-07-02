package pro.fazeclan.river.jarona.condition;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

public abstract class Condition implements Comparable<Condition> {

    /**
     * -- GETTER --
     *  Returns the player UUID linked under this condition, null if it does not exist.
     *
     * @return a player's UUID
     */
    @Getter
    protected UUID playerUUID = null;

    /**
     * -- SETTER --
     *  Sets the HUD function for the condition. A String output is needed.
     * -- GETTER --
     *  Gets the HUD function for the condition.
     *
     @param hud The HUD function
      * @return a function that takes a Condition input and String output
     */
    @Getter
    @Setter
    protected Function<Condition, String> hud = (condition) -> null;

    /**
     * -- SETTER --
     *  Sets the condition for the HUD function to be shown.
     * -- GETTER --
     *  Gets the HUD condition that decides whether it shows up or not.
     *
     @param hudCondition The HUD condition
      * @return the HUD condition
     */
    @Getter
    @Setter
    protected Function<Condition, Boolean> hudCondition = (condition) -> false;

    /**
     * -- SETTER --
     *  Sets the priority of the condition.
     * -- GETTER --
     *  Gets the priority of the condition.
     *
     @param priority an integer
      * @return an integer representing the priority
     */
    @Getter
    @Setter
    protected int priority = 100; // Higher values meaning showing up later compared to other conditions

    /**
     * Returns the player linked under this condition, null if the uuid is null or the player isn't online.
     * @return A player
     */
    public Player getPlayer() {
        if (playerUUID == null) {
            return null;
        }
        return Bukkit.getPlayer(playerUUID);
    }

    /**
     * Returns whether or not the condition is currently active.
     * @return a boolean
     */
    public abstract boolean getAvailable();

    /**
     * Gets the HUD function as a component to be displayed. Returns an empty component if there is nothing to display.
     * @return the HUD component
     */
    public Component getHudComponent() {
        var h = hud.apply(this);
        if (h == null) {
            return Component.empty();
        }
        return MiniMessage.miniMessage().deserialize(hud.apply(this));
    }

    /**
     * Gets whether the hud should be visible or not.
     * @return A boolean
     */
    public boolean getHudVisible() {
        return hudCondition.apply(this);
    }

    /**
     * Resets the condition to defaults.
     */
    abstract void reset();

    @Override
    public int compareTo(@NotNull Condition o) {
        return Integer.compare(priority, o.priority);
    }
}
