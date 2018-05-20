package bukkit.Listeners;

import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import bukkit.Utils.IsoworldsLogger;
import common.ManageFiles;
import common.Msg;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 22/10/2017.
 */

public class IsoworldsListeners implements Listener {

    private final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();

    @EventHandler
    public void onRespawnPlayerEvent(PlayerRespawnEvent event) {

        Player p = event.getPlayer();
        String worldname = p.getUniqueId() + "-IsoWorld";

        new BukkitRunnable() {

            @Override
            public void run() {
                if (Bukkit.getServer().getWorld(worldname) != null) {
                    IsoworldsLocations.teleport(p, worldname);
                } else {
                    IsoworldsLogger.info("SPAWN ISOLONICE");
                    IsoworldsLocations.teleport(p, "Isolonice");
                }
            }

        }.runTaskLater(this.instance, 1);
    }

    // Set autosave for a new loaded world to avoid crash ?
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                event.getWorld().setAutoSave(true);
                IsoworldsLogger.info("- Isoworld setAutoSave to TRUE");
            }
        }.runTaskLater(this.instance, 20);


    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Message de bienvenue pour IsoWorlds (quelle commande), tutoriel après 5 secondes
        if (IsoworldsUtils.getCharge(event.getPlayer(), Msg.keys.SQL) == null) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.GREEN + "Sijania vous souhaite la bienvenue !");
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.GREEN + "Sur Isolonice, vous possédez votre propre monde nommé: IsoWorld");
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.GREEN + "Vous êtes seul maître à bord, il est à vous !");
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.GREEN + "Pour commencer l'aventure entrez la commande: /iw");
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.GREEN + "Puis sélectionnez le premier menu (Construction)");
                    event.getPlayer().performCommand("iw");
                }

            }.runTaskLater(this.instance, 100);
        }

        String world = event.getPlayer().getWorld().getName();
        IsoworldsUtils.cm("DEBUG1: " + world);
    }

    // Logout event, tp spawn
    @EventHandler
    public void onLogout(PlayerQuitEvent event) {

        Player p = event.getPlayer();

        IsoworldsLocations.teleport(p, "Isolonice");
        IsoworldsUtils.cm("Joueur téléporté au spawn");
    }

    @EventHandler
    // Anti grief spawn
    public void onInteractSpawn(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("isoworlds.bypass.spawn")) {
            return;
        }
        if (p.getLocation().getWorld().equals("Isolonice")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    // Anti grief spawn
    public void onDestructSpawn(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("isoworlds.bypass.spawn")) {
            return;
        }

        // If break in chunk of spawn layer 60, remove drop
        Location eventLocation = new Location(Bukkit.getServer().getWorld(event.getBlock().getWorld().getName()), 0, 60, 0);

        // Don't break plateforme of nether/end spawn
        if (event.getBlock().getWorld().getName().equals("DIM1") || event.getBlock().getWorld().getName().equals("DIM-1")) {
            if (event.getBlock().getY() == 60 || event.getBlock().getY() == 61 & event.getBlock().getLocation().distance(eventLocation) <= 3.0) {
                event.setCancelled(true);
            }
        }

        // Don't drop on isoworld plateform break
        if (event.getBlock().getY() == 60 & event.getBlock().getLocation().distance(eventLocation) <= 2.0) {
            if (event.getBlock().getType().name().equals("DIRT")) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        String check_p;
        String check_w;
        Location worldTo = event.getTo();
        Location worldFrom = event.getFrom();
        Player pPlayer = event.getPlayer();

        IsoworldsUtils.cm("[TRACKING-IW] Téléporation: " + pPlayer.getName() + " [FROM: " + worldFrom.toString() + "] - [TO: " + worldTo.toString() + "] - [CAUSE: "
                + event.getCause().toString() + "]");
        if (worldTo.toString().equals(worldFrom.toString())) {
            return;
        }

        // Deny teleport to unloaded world with /back that load world DIM number instead
        File checkFolder = new File(ManageFiles.getPath() + worldTo.getWorld().getName());
        if (!checkFolder.exists() & worldTo.getWorld().getName().contains("IsoWorld")) {
            event.setCancelled(true);
            IsoworldsLogger.warning("Isoworld non actif, téléporation annulée !");
            return;
        }

        String eventworld = worldTo.getWorld().getName();

        if (eventworld.contains("-IsoWorld")) {
            try {
                PreparedStatement check = instance.database.prepare(CHECK);
                // UUID_P
                check_p = pPlayer.getUniqueId().toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = eventworld;
                check.setString(2, check_w);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (pPlayer.hasPermission("isoworlds.bypass.teleport")) {
                    return;
                }

                if (rselect.isBeforeFirst()) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
                    return;
                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
                    return;
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.DENY_TELEPORT);
                    return;
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            }

        }
    }


}
