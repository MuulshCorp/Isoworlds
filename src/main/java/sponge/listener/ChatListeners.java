package sponge.listener;

import common.IsoChat;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ChatListeners {
    @Listener
    public void onIsochat(MessageChannelEvent.Chat event, @First Player sender) {
        if (IsoChat.isActivated(sender.getUniqueId())) {
            if (sender.getWorld().getName().endsWith("-Isoworld")) {
                event.setCancelled(true);
                sender.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.of(Text.builder("[Isochat] " + sender.getName() + ": " + event.getRawMessage().toPlain()).color(TextColors.BLUE).build())));
            } else {
                IsoChat.toggle(sender.getUniqueId());
            }
        }
    }
}
