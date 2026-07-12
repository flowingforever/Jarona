package pro.fazeclan.river.jarona.nametag.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ClientDisplay extends ClientEntity {

    @Getter
    @Setter
    protected Vector3f translation = null;
    @Getter
    protected Vector3f scale = null;
    @Getter
    @Setter
    protected DisplayBillboard billboard = null;

    public ClientDisplay(EntityType type, Location location) {
        super(type, location);
    }

    @Override
    public List<EntityData<?>> getEntityData() {
        ArrayList<EntityData<?>> data = new ArrayList<>(super.getEntityData());
        if (this.scale != null) {
            data.add(new EntityData<>(
                    12,
                    EntityDataTypes.VECTOR3F,
                    this.scale
            ));
        }
        if (this.translation != null) {
            data.add(new EntityData<>(
                    11,
                    EntityDataTypes.VECTOR3F,
                    this.translation
            ));
        }
        if (this.billboard != null) {
            data.add(new EntityData<>(
                    15,
                    EntityDataTypes.BYTE,
                    (byte) this.billboard.getValue()
            ));
        }
        return data;
    }

    public void setScale(Vector scale) {
        this.scale = new Vector3f().add(
                (float) scale.getX(),
                (float) scale.getY(),
                (float) scale.getZ()
        );
    }

}
