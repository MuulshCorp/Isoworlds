package bukkit.Listeners;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoWorldsInventory;
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
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        Integer maxy = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Location top = new Location(Bukkit.getServer().getWorld(worldname), 0, maxy, 0);

        p.teleport(top);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

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
        if (p.getLocation().getWorld().equals("Isolonice")) {
            event.setCancelled(true);
        }

    }

    // Used to redefine rules of the world
    public void onLoadWorld(WorldLoadEvent event) {

        if (event.getWorld().getName().contains("-IsoWorld")) {
            String worldname = event.getWorld().getName();
            Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
            IsoworldsUtils.cmd("wb " + worldname + " set 250 250 0 0");
            Block y = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
            Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, y.getY(), 0);
            Bukkit.getServer().getWorld(worldname).setGameRuleValue(DefaultGameRules.MOB_GRIEFING, "false");
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

        if (worldTo.toString().equals(worldFrom.toString())) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
        if (inventory.getName().equals(IsoWorldsInventory.mainInventory.getName())) {
            // Si inventaire biome
            if (clicked.getItemMeta().getDisplayName().equals("Biome")) {
                player.openInventory(IsoWorldsInventory.biomeInventory);
            }
            // Si inventaire confiance
            if (clicked.getItemMeta().getDisplayName().equals("Confiance")) {
                player.openInventory(IsoWorldsInventory.confianceInventory);
            }
            // Si inventaire création
            if (clicked.getItemMeta().getDisplayName().equals("Création/Refonte")) {
                player.openInventory(IsoWorldsInventory.creationInventory);
            }
            // Si inventaire Maison
            if (clicked.getItemMeta().getDisplayName().equals("Maison")) {
                player.openInventory(IsoWorldsInventory.maisonInventory);
            }
            // Si inventaire météo
            if (clicked.getItemMeta().getDisplayName().equals("Météo")) {
                player.openInventory(IsoWorldsInventory.meteoInventory);
            }
            // Si inventaire activation
            if (clicked.getItemMeta().getDisplayName().equals("Activation")) {
                player.openInventory(IsoWorldsInventory.activationInventory);
            }
            // Si inventaire téléporation
            if (clicked.getItemMeta().getDisplayName().equals("Téléportation")) {
                player.openInventory(IsoWorldsInventory.teleportationInventory);
            }
            // Si inventaire temps
            if (clicked.getItemMeta().getDisplayName().equals("Temps")) {
                player.openInventory(IsoWorldsInventory.tempsInventory);
            }
        }
    }
}