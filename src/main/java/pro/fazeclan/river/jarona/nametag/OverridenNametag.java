package pro.fazeclan.river.jarona.nametag;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class OverridenNametag extends Nametag {

    @Getter
    private BiFunction<Player, Player, String> override;

    public OverridenNametag(Player player, BiFunction<Player, Player, String> override) {
        super(player);
        this.override = override;
    }

    @Override
    public Component getText(Player viewer) {
        if (override.apply(viewer, getPlayer()).isBlank()) return super.getText(viewer);
        return MiniMessage.miniMessage().deserialize(this.override.apply(viewer, getPlayer()));
    }
}
