package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;

public record StatisticDefinition(NamespacedKey key, String display, long defaultValue) {}
