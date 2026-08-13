package pro.fazeclan.river.jarona.queue;

import lombok.Getter;
import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.map.GameMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueuedGame {

    @Getter
    private final Game game;
    @Getter
    private int secondsRemain;
    @Getter
    private GameMap highestVoted;
    @Getter
    private final Map<UUID, GameMap> votedMaps;

    public QueuedGame(Game game, int secondsRemain) {
        this.game = game;
        this.secondsRemain = secondsRemain;
        this.highestVoted = null;
        this.votedMaps = new HashMap<>();
    }

    public void vote(UUID uuid, GameMap map) {
        votedMaps.put(uuid, map);
        var mostVoted = votedMaps.values()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
        mostVoted.ifPresent(gameMap -> this.highestVoted = gameMap);
    }

    public void vote(Player player, GameMap map) {
        vote(player.getUniqueId(), map);
    }

    public void decrementSeconds() {
        secondsRemain--;
    }

    public long getMapVotes(GameMap map) {
        Long votes = votedMaps.values()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .get(map);
        if (votes == null) return 0;
        else return votes;
    }

}
