package sponge.Listeners;

import common.ManageFiles;
import common.Msg;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
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

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


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

    // Logout event, tp spawn
    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event) {
        String worldname = ("Isolonice");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);
        event.getTargetEntity().setLocationSafely(top);
        IsoworldsUtils.cm("Joueur téléporté au spawn");
    }

    @Listener
    public void onLoadWorld(LoadWorldEvent event) {
        if (IsoworldsUtils.iworldPushed(event.getTargetWorld().getName(), Msg.keys.SQL)) {
            IsoworldsUtils.cm("Debug 4");
            // Prepair for pushing to backup server
            File check = new File(ManageFiles.getPath() + event.getTargetWorld().getName() + "PULL");
            File check2 = new File(ManageFiles.getPath() + event.getTargetWorld().getName());
            if (check2.exists()) {
                IsoworldsUtils.cm("Debug 5");
                IsoworldsUtils.iworldSetStatus(event.getTargetWorld().getName(), 0, Msg.keys.SQL);
            } else if (check.exists()) {
                ManageFiles.rename(ManageFiles.getPath() + event.getTargetWorld().getName() + "@PUSH", "@PULL");
                IsoworldsUtils.cm("PULL OK");
                event.setCancelled(true);
                return;
            }
        }

        if (event.getTargetWorld().getName().contains("-IsoWorld")) {
            Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(event.getTargetWorld().getName());
            Sponge.getServer().getWorld(event.getTargetWorld().getName()).get().getProperties().setKeepSpawnLoaded(false);
            IsoworldsUtils.cm("KeepSpawnLoaded: " + optionalWorld.get().doesKeepSpawnLoaded());

            Sponge.getServer().getWorld(event.getTargetWorld().getName()).get().getProperties().setLoadOnStartup(false);
            IsoworldsUtils.cm("LoadOnStartup: " + optionalWorld.get().loadOnStartup());

            Sponge.getServer().getWorld(event.getTargetWorld().getName()).get().getProperties().setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
            IsoworldsUtils.cm("Mob Griefing: " + optionalWorld.get().getGameRules());

            Sponge.getServer().getWorld(event.getTargetWorld().getName()).get().getProperties().setGenerateSpawnOnLoad(false);
            IsoworldsUtils.cm("KeepSpawnLoaded: " + optionalWorld.get().doesKeepSpawnLoaded());

            Sponge.getServer().saveWorldProperties(event.getTargetWorld().getProperties());
        }
    }

    @Listener
    public void onPlayerChangeWorld(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player pPlayer) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_p;
        String check_w;


        String eventworld = event.getToTransform().getExtent().getName();
        // Check if world status is 0

        Sponge.getServer().loadWorld(event.getToTransform().getExtent().getName());

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

                if (rselect.isBeforeFirst()) {
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

    @Listener
    public void onInteractSpawn(HandInteractEvent event, @First Player p) {
        if (!event.getInteractionPoint().isPresent()) {
            return;
        }
        if (p.getWorld().getName().equals("Isolonice")) {
            event.setCancelled(true);
        }

    }
}
