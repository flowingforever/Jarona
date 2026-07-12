package pro.fazeclan.river.jarona.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.map.GameMap;

import java.util.concurrent.CompletableFuture;

public class GameMapArgument implements CustomArgumentType<GameMap, String> {

    private static final SimpleCommandExceptionType ERROR_REGISTRY_EMPTY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game map registry is empty!"))
    );

    private static final SimpleCommandExceptionType ERROR_INVALID_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("This game map key is not structured correctly!"))
    );

    private static final SimpleCommandExceptionType ERROR_REGISTRY_NO_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game map registry does not have this map!"))
    );

    @Override
    public GameMap parse(StringReader reader) throws CommandSyntaxException {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> GameMap parse(StringReader reader, S source) throws CommandSyntaxException {
        var manager = Jarona.getInstance().getMapManager();
        var registry = manager.getRegistry();

        if (registry.isEmpty()) {
            throw ERROR_REGISTRY_EMPTY.create();
        }

        final String key = getNativeType().parse(reader);
        if (key == null) {
            throw ERROR_INVALID_KEY.create();
        }

        if (!registry.containsKey(key)) {
            throw ERROR_REGISTRY_NO_KEY.create();
        }

        return registry.get(key);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var manager = Jarona.getInstance().getMapManager();
        manager.getRegistry().keySet()
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
