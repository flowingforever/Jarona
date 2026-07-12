package pro.fazeclan.river.jarona.nametag.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClientTextDisplay extends ClientDisplay {

    public ClientTextDisplay(Location location) {
        super(EntityType.TEXT_DISPLAY, location);
    }

    protected Component text = null;
    protected int background = -1;
    protected int flags = 0;

    @Override
    public List<EntityData<?>> getEntityData() {
        ArrayList<EntityData<?>> data = new ArrayList<>(super.getEntityData());
        if (this.text != null) {
            data.add(new EntityData<>(
                    23,
                    EntityDataTypes.ADV_COMPONENT,
                    this.text
            ));
        }
        if (this.background != -1) {
            data.add(new EntityData<>(
                    25,
                    EntityDataTypes.INT,
                    this.background
            ));
        }
        if (this.flags != 0) {
            data.add(new EntityData<>(
                    27,
                    EntityDataTypes.BYTE,
                    (byte) this.flags
            ));
        }
        return data;
    }

    public void setTextShadow(boolean enabled) {
        setFlag(0x01, enabled);
    }

    public void setSeeThrough(boolean enabled) {
        setFlag(0x02, enabled);
    }

    public void setTextAlignment(TextAlignment alignment) {
        flags &= ~(0x08 | 0x10);
        switch (alignment) {
            case CENTER -> {}
            case LEFT -> flags |= 0x08;
            case RIGHT -> flags |= 0x10;
        }
    }

    private void setFlag(int mask, boolean enabled) {
        if (enabled) {
            flags |= mask;
        } else {
            flags &= ~mask;
        }
    }
}
