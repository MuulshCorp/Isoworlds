package sponge.Listeners;

import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.scheduler.Task;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsLogger;
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
import java.util.concurrent.TimeUnit;

import static sponge.IsoworldsSponge.instance;


/**
 * Created by Edwin on 08/10/2017.
 */
public class IsoworldsListeners {
    private final IsoworldsSponge plugin = instance;

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

    // On téléporte tous les joueurs à la déconnexion
    @Listener
    public void onStop(GameStoppingServerEvent event) {
        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        String worldname = ("Isolonice");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);

        for (Player p : players) {
            p.setLocation(top);
            IsoworldsUtils.cm("TP DECO: " + p.getName() + " : " + top.getExtent().getName().toLowerCase());
        }
    }

    @Listener
    public void onConnect(ClientConnectionEvent.Join event) {
        // Message de bienvenue pour IsoWorlds (quelle commande), tutoriel après 5 secondes
        IsoworldsUtils.cm("TEST");
        IsoworldsUtils.cm("TEST" + event.getTargetEntity().getName());
        if (IsoworldsUtils.firstTime(event.getTargetEntity(), Msg.keys.SQL) == null) {
            IsoworldsUtils.cm("TEST2");
            Task.builder().execute(new Runnable() {
                @Override
                public void run() {
                    IsoworldsUtils.cm("TEST");
                    event.getTargetEntity().sendMessage(Text.of(Text.builder("[IsoWorlds]").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(" Sijania vous souhaite la bienvenue sur Isolonice !\n" +
                                    "Dans ce royaume, vous possédez votre propre monde nommé: IsoWorld.\n" +
                                    "Vous êtes seul maître à bord, il est à vous et vous pouvez choisir qui peut y accéder.\n" +
                                    "Essayez dès maintenant via la commande: /iw").color(TextColors.GREEN))).build()));
                }
            }).delay(5, TimeUnit.SECONDS).submit(instance);
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Login event) {
        String worldname = ("Isolonice");

        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);
        Transform<World> t = new Transform<World>(top);
        IsoworldsUtils.cm("DEBUG SPAWN 1" + t.toString());
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

    // Debug load world
    @Listener
    public void onLoadWorld(LoadWorldEvent event) {
        IsoworldsUtils.cm("LOADING " + event.getTargetWorld().getName() + " WORLD, CAUSED BY: " + event.getCause().toString());
    }

    // TP lors du unload d'un monde
    @Listener
    public void onUnloadWorld(UnloadWorldEvent event) {
        String worldname = ("Isolonice");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);

        for (Player p : event.getTargetWorld().getPlayers()) {
            p.setLocation(top);
            IsoworldsUtils.cm("TP UNLOAD: " + p.getName() + " : " + top.getExtent().getName().toLowerCase());
        }
    }

    @Listener
    public void onPlayerChangeWorld(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player pPlayer) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_p;
        String check_w;

        IsoworldsUtils.cm("[TRACKING-IW] Téléporation: " + pPlayer.getName() + " [FROM: " + event.getFromTransform().toString() + "] - [TO: " + event.getToTransform().toString() + "] - [CAUSE: "
                + event.getCause().toString() + "]");

        String eventworld = event.getToTransform().getExtent().getName();

        // Check if world folder is present
        File checkFolder = new File(ManageFiles.getPath() + eventworld);
        if (!checkFolder.exists() & eventworld.contains("IsoWorld")) {
            event.setCancelled(true);
            IsoworldsLogger.warning("Isoworld non actif, téléporation annulée !");
            return;
        }

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
        // Allow break if permission
        if (p.hasPermission("isoworlds.bypass.spawn")) {
            return;
        }
        if (p.getWorld().getName().equals("Isolonice")) {
            event.setCancelled(true);
        }

    }

}
