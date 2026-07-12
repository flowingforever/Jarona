package pro.fazeclan.river.jarona.nametag.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientEntity {

    private int entityId;
    private UUID uuid;
    private EntityType type;
    @Getter
    @Setter
    protected Location location;

    public ClientEntity(org.bukkit.entity.EntityType type, Location location) {
        this.entityId = SpigotReflectionUtil.generateEntityId();
        this.uuid = UUID.randomUUID();
        this.type = SpigotConversionUtil.fromBukkitEntityType(type);
        this.location = location;
    }

    public void spawn(Player viewer) {
        this.sendPacket(new WrapperPlayServerSpawnEntity(
                this.entityId,
                this.uuid,
                this.type,
                SpigotConversionUtil.fromBukkitLocation(this.location),
                this.location.getYaw(),
                0,
                null
        ), viewer);
    }

    public void update(Player viewer) {
        this.sendPacket(new WrapperPlayServerEntityMetadata(
                this.entityId,
                this.getEntityData()
        ), viewer);
    }

    public void despawn(Player viewer) {
        this.sendPacket(new WrapperPlayServerDestroyEntities(
                this.entityId
        ), viewer);
    }

    public void mount(Entity entity, Player viewer) {
        this.sendPacket(new WrapperPlayServerSetPassengers(
                entity.getEntityId(),
                new int[]{this.entityId}
        ), viewer);
    }

    public void teleportFor(Player viewer) {
        this.sendPacket(new WrapperPlayServerEntityTeleport(
                this.entityId,
                SpigotConversionUtil.fromBukkitLocation(this.location),
                true
        ), viewer);
    }

    public List<EntityData<?>> getEntityData() {
        return new ArrayList<>();
    }

    private void sendPacket(PacketWrapper<?> packet, Player player) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

}
