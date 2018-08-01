package sponge.listener;

import common.IsoChat;
import org.bukkit.entity.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;

public class ChatListeners {
    @Listener
    public void onChatInIso(MessageChannelEvent.Chat event, @First Player sender) {
        if (IsoChat.isActivated(sender.getUniqueId())) {
            if (sender.getWorld().getName().endsWith("-Isoworld")) {
                event.setCancelled(true);
                sender.getWorld().getPlayers().forEach(p -> p.sendMessage(event.getMessage().toPlain()));
            } else {
                IsoChat.toggle(sender.getUniqueId());
                sender.sendMessage("You are not in a isoworld, isochat deactivated");
            }
        }
    }
}
