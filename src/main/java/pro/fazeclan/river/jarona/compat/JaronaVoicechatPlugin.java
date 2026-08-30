package pro.fazeclan.river.jarona.compat;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JaronaVoicechatPlugin implements VoicechatPlugin {
    private VoicechatServerApi serverApi;
    private final Map<UUID, Group> spectatorGroups; // game to spectator group map

    public JaronaVoicechatPlugin() {
        this.spectatorGroups = new ConcurrentHashMap<>();
    }

    @Override
    public String getPluginId() {
        return "jarona_voicechat";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, event -> {
            serverApi = event.getVoicechat();
        });

        registration.registerEvent(MicrophonePacketEvent.class, event -> {
            var connection = event.getSenderConnection();
            if (connection == null) return;
            var player = Bukkit.getPlayer(connection.getPlayer().getUuid());
            if (player == null) return;
            if (!player.hasPotionEffect(PotionEffectType.UNLUCK)) return;
            event.cancel();
        });
    }

    public void addSpectator(Player player) {
        if (serverApi == null) return;
        var connection = serverApi.getConnectionOf(player.getUniqueId());
        if (connection == null) return;
        var world = player.getWorld();
        try {
            var gameUUID = UUID.fromString(world.getKey().value());
            Group group;
            if (spectatorGroups.containsKey(gameUUID)) {
                group = spectatorGroups.get(gameUUID);
            } else {
                group = serverApi.groupBuilder()
                        .setHidden(true)
                        .setId(UUID.randomUUID())
                        .setName("Game Spectators")
                        .setPersistent(false)
                        .setType(Group.Type.OPEN)
                        .build();
                spectatorGroups.put(gameUUID, group);
            }
            connection.setGroup(group);
        } catch (RuntimeException ignored) {}
    }

    public void removePlayer(Player player) {
        if (serverApi == null) return;
        var connection = serverApi.getConnectionOf(player.getUniqueId());
        if (connection == null) return;
        connection.setGroup(null);
    }

}
