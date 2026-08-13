package pro.fazeclan.river.jarona.screen;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.queue.QueuedPlayer;

import java.util.List;

public class MapVotingScreen {

    public static void handleScreen(QueuedPlayer qp, @Nullable String text) {
        var jarona = Jarona.getInstance();
        var manager = jarona.getMapManager();
        var queue = jarona.getQueueManager();
        var maps = manager.getAllMapsSupporting(qp.getGameKey());
        var mm = MiniMessage.miniMessage();

        var buttons = maps.stream()
                .map(map -> ActionButton
                        .builder(mm.deserialize(map.name()))
                        .action(DialogAction.customClick((response, audience) -> {
                            queue.setPlayerVote(qp.getPlayer(), qp.getGameKey(), map);
                            handleScreen(qp, "<green>You've voted for " + map.name() + "!</green>");
                        }, ClickCallback.Options.builder().build()))
                        .build()
                )
                .toList();

        String message;
        if (text != null) {
            message = text;
        } else {
            message = "<green>Please select a map that you would like to see in the next game!</green>";
        }

        qp.getPlayer().showDialog(Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(mm.deserialize("<yellow>Map Voting!</yellow>"))
                                .body(List.of(
                                        DialogBody.plainMessage(mm.deserialize(message))
                                ))
                                .build()
                )
                .type(
                        DialogType
                                .multiAction(buttons)
                                .columns(3)
                                .build()
                )
        ));
    }

}
