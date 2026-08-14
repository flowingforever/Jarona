package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticWriteBuffer {

    private final ConcurrentHashMap<String, AtomicLong> pendingIncrements = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> pendingSets = new ConcurrentHashMap<>();

    public void queueIncrement(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey, long amount) {
        var mapKey = makeKey(uuid, gameKey, statKey);
        pendingIncrements.computeIfAbsent(mapKey, k -> new AtomicLong(0)).addAndGet(amount);
    }

    public void queueSet(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey, long value) {
        var mapKey = makeKey(uuid, gameKey, statKey);
        pendingSets.put(mapKey, value);
        pendingIncrements.remove(mapKey);
    }

    public boolean isEmpty() {
        return pendingIncrements.isEmpty() && pendingSets.isEmpty();
    }

    public Batch drain() {
        var incrementSnapshot = new ConcurrentHashMap<>(pendingIncrements);
        var setSnapshot = new ConcurrentHashMap<>(pendingSets);
        pendingIncrements.clear();
        pendingSets.clear();
        return new Batch(incrementSnapshot, setSnapshot);
    }

    public long peekIncrement(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        var value = pendingIncrements.get(makeKey(uuid, gameKey, statKey));
        return value != null ? value.get() : 0L;
    }

    public Long peekSet(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        return pendingSets.get(makeKey(uuid, gameKey, statKey));
    }

    public record Batch(
            ConcurrentHashMap<String, AtomicLong> increments,
            ConcurrentHashMap<String, Long> sets
    ) {}

    private String makeKey(UUID uuid, NamespacedKey gameKey, NamespacedKey statKey) {
        return uuid + "," + gameKey.toString() + "," + statKey.toString();
    }

}
