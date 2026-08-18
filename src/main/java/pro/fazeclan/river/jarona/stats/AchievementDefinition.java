package pro.fazeclan.river.jarona.stats;

import org.bukkit.NamespacedKey;

public record AchievementDefinition(NamespacedKey key, String display, long min, long max) {}
