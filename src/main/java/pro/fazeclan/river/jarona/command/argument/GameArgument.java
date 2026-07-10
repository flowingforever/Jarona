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
import org.jspecify.annotations.NullMarked;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.Game;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class GameArgument implements CustomArgumentType<Game, String> {

    private static final SimpleCommandExceptionType ERROR_REGISTRY_EMPTY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game type registry is empty!"))
    );

    private static final SimpleCommandExceptionType ERROR_INVALID_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("This game type key is not structured correctly!"))
    );

    private static final SimpleCommandExceptionType ERROR_REGISTRY_NO_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game type registry does not have this game!"))
    );

    @Override
    public Game parse(StringReader reader) {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> Game parse(StringReader reader, S source) throws CommandSyntaxException {
        var manager = Jarona.getInstance().getGameManager();
        var registry = manager.getRegistry();

        if (registry.isEmpty()) {
            throw ERROR_REGISTRY_EMPTY.create();
        }

        final NamespacedKey key = NamespacedKey.fromString(getNativeType().parse(reader));
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
        var manager = Jarona.getInstance().getGameManager();
        manager.getRegistry().keySet().stream()
                .map(NamespacedKey::asString)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
