package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AchievementWriteBuffer {

    private final ConcurrentHashMap<String, AtomicLong> pendingGrants = new ConcurrentHashMap<>();

    public void queueGrant(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        pendingGrants.computeIfAbsent(
                makeKey(uuid, gameKey, achKey),
                k -> new AtomicLong(1)
        );
    }

    public void queueGrantProgress(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey, long amount) {
        pendingGrants.computeIfAbsent(
                makeKey(uuid, gameKey, achKey),
                k -> new AtomicLong(0)
        ).addAndGet(amount);
    }

    public boolean isEmpty() {
        return pendingGrants.isEmpty();
    }

    public Batch drain() {
        var grantsSnapshot = new ConcurrentHashMap<>(pendingGrants);
        grantsSnapshot.clear();
        return new Batch(grantsSnapshot);
    }

    public long peekGrant(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        var value = pendingGrants.get(makeKey(uuid, gameKey, statKey));
        return value != null ? value.get() : 0L;
    }

    public record Batch(
            ConcurrentHashMap<String, AtomicLong> grants
    ) {}

    private String makeKey(UUID uuid, NamespacedKey gameKey, NamespacedKey achKey) {
        return uuid + "," + gameKey.toString() + "," + achKey.toString();
    }

}
