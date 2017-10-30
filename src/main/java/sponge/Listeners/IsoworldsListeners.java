package sponge.Listeners;

import common.Msg;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.scheduler.Task;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsUtils;
import sponge.IsoworldsSponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Created by Edwin on 08/10/2017.
 */
public class IsoworldsListeners {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Listener
    public void onRespawnPlayerEvent(RespawnPlayerEvent event) {

        Player p = event.getTargetEntity();
        String worldname = (p.getUniqueId() + "-IsoWorld");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);

        Transform<World> t = new Transform<World>(event.getFromTransform().getExtent(), top.getPosition());
        event.setToTransform(t);
    }

    @Listener
    public void onPlayerChangeWorld(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player pPlayer) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_p;
        String check_w;


        String eventworld = event.getToTransform().getExtent().getName();
        Task.builder().async().delayTicks(20).execute(c -> {
            Sponge.getServer().loadWorld(event.getToTransform().getExtent().getName());
        }).submit(plugin);

        if (eventworld.contains("-IsoWorld")) {
            try {
                PreparedStatement check = plugin.database.prepare(CHECK);
                // UUID_P
                check_p = pPlayer.getUniqueId().toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = eventworld;
                check.setString(2, check_w);
                // Requête
                check.setString(3, plugin.servername);
                ResultSet rselect = check.executeQuery();
                IsoworldsUtils.cm("Monde event: " + eventworld);
                if (pPlayer.hasPermission("isoworlds.bypass.teleport")) {
                    return;
                }

                if (rselect.isBeforeFirst() ) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION).color(TextColors.AQUA))).build()));
                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION).color(TextColors.AQUA))).build()));
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.DENY_TELEPORT).color(TextColors.AQUA))).build()));
                }

            } catch (Exception se) {
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            }

        }
    }
}
