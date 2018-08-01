package bukkit.listener;

import common.IsoChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListeners implements Listener {
    @EventHandler
    public void onChatInIso(PlayerChatEvent event) {
        Player sender = event.getPlayer();
        if (IsoChat.isActivated(sender.getUniqueId())) {
            if (sender.getWorld().getName().endsWith("-Isoworld")) {
                event.setCancelled(true);
                sender.getWorld().getPlayers().forEach(p -> p.sendMessage(event.getMessage()));
            } else {
                IsoChat.toggle(sender.getUniqueId());
                sender.sendMessage("You are not in a isoworld, isochat deactivated");
            }
        }
    }
}
