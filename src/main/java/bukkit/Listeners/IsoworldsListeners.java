package bukkit.Listeners;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoWorldsInventory;
import bukkit.Utils.IsoworldsLogger;
import common.ManageFiles;
import common.Msg;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.spongepowered.api.world.gamerule.DefaultGameRules;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 22/10/2017.
 */

public class IsoworldsListeners implements Listener {

    private final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();

    @EventHandler
    public static void onRespawnPlayerEvent(PlayerRespawnEvent event) {

        Player p = event.getPlayer();
        String worldname = (event.getPlayer().getWorld().getName());

        // Construction du point de respawn
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Integer secours;
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        IsoworldsLogger.severe("Y = " + top);
        try {
            if (top <= 0) {
                IsoworldsLogger.severe("1");
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                IsoworldsLogger.severe("2");
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, secours, 0);
            }

            // Téléportation du joueur
            if (p.teleport(go)) {
            }
        } catch (NullPointerException npe) {
            //
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
        }

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
        String worldname = ("Isolonice");
        Integer maxy = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Location top = new Location(Bukkit.getServer().getWorld(worldname), 0, maxy, 0);

        p.teleport(top);
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

//    // Used to redefine rules of the world
//    public void onLoadWorld(WorldLoadEvent event) {
//
//        if (event.getWorld().getName().contains("-IsoWorld")) {
//            String worldname = event.getWorld().getName();
//            Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
//            IsoworldsUtils.cmd("wb " + worldname + " set 250 250 0 0");
//            Block y = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
//            Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, y.getY(), 0);
//            Bukkit.getServer().getWorld(worldname).setGameRuleValue(DefaultGameRules.MOB_GRIEFING, "false");
//        }
//    }

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
