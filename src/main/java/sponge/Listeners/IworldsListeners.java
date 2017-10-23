package sponge.Listeners;

import org.spongepowered.api.entity.Transform;
import sponge.Locations.IworldsLocations;
import sponge.Utils.IworldsUtils;
import sponge.IworldsSponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Edwin on 08/10/2017.
 */
public class IworldsListeners {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Listener
    public void onRespawnPlayerEvent(RespawnPlayerEvent event) {

        Player p = event.getTargetEntity();
        String worldname = (p.getUniqueId() + "-iWorld");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        IworldsUtils.cm("Spawn point: " + spawn.getPosition());
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IworldsLocations.getHighestLoc(maxy).orElse(null);
        IworldsUtils.cm("Point de spawn:" + top.getPosition());

        Transform<World> t = new Transform<World>(event.getFromTransform().getExtent(), top.getPosition());
        event.setToTransform(t);
//
//        if(p.getOrNull(Keys.RESPAWN_LOCATIONS) != null) {
//            IworldsUtils.cm("PDP: 1");
//            Map<UUID, RespawnLocation> map = p.get(Keys.RESPAWN_LOCATIONS).get();
//            IworldsUtils.cm("Key: " + p.get(Keys.RESPAWN_LOCATIONS).get());
//            RespawnLocation newLoc = RespawnLocation.builder()
//                    .world(event.getFromTransform().getExtent().getUniqueId())
//                    .position(top.getPosition())
//                    .forceSpawn(false).build();
//            map.put(event.getFromTransform().getExtent().getUniqueId(), newLoc);
//            DataTransactionResult result = p.offer(Keys.RESPAWN_LOCATIONS, map);
//            IworldsUtils.cm("DataTransactionResult " + result.toString());
//       }
    }

    @Listener
    public void onPlayerChangeWorld(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player pPlayer) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        String check_p;
        String check_w;


        String eventworld = event.getToTransform().getExtent().getName();
        IworldsUtils.cm("Ce monde doit être chargé avant téléportation, préparation...");
        Sponge.getServer().loadWorld(event.getToTransform().getExtent().getName());

        if (eventworld.contains("-iWorld")) {
            try {
                PreparedStatement check = plugin.database.prepare(CHECK);

                // UUID_P
                check_p = IworldsUtils.PlayerToUUID(pPlayer).toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = eventworld;
                check.setString(2, check_w);

                IworldsUtils.cm("CHECK REQUEST: " + check);
                // Requête
                ResultSet rselect = check.executeQuery();
                IworldsUtils.cm("Monde event: " + eventworld);

                if (pPlayer.hasPermission("iworlds.bypass.teleport")) {
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("CHECK Sijania vient de vous autoriser la téléporation, car vous faites partie de l'équipe.").color(TextColors.AQUA))).build()));
                    return;
                }

                if (rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("CHECK Sijania vient de vous autoriser la téléporation.").color(TextColors.AQUA))).build()));
                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    IworldsUtils.cm("Monde joueur: " + pPlayer.getWorld().getName());
                    IworldsUtils.cm("Monde event: " + eventworld);
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("CHECK Sijania vient de vous autoriser la téléporation.").color(TextColors.AQUA))).build()));
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("CHECK Sijania vient de vous refuser la téléportation.").color(TextColors.AQUA))).build()));
                }

            } catch (Exception se) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
            }

        }
    }
}
