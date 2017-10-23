package bukkit.Listeners;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import sponge.Locations.IworldsLocations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 22/10/2017.
 */

public class IworldsListeners implements Listener {

    private final IworldsBukkit instance = IworldsBukkit.getInstance();

    @EventHandler
    public static void onRespawnPlayerEvent(PlayerRespawnEvent event) {

        Player p = event.getPlayer();
        String worldname = (p.getUniqueId() + "-iWorld");
        Integer maxy = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Location top = new Location(Bukkit.getServer().getWorld(worldname), 0, maxy, 0);

        p.teleport(top);
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

        if (eventworld == null) {
            IworldsUtils.cm("Ce monde doit être chargé avant téléportation, préparation...");
            IworldsUtils.cm("DEBUG DUPE: " + event.getTo().toString());
            Bukkit.getServer().createWorld(new WorldCreator(eventworld));

        }

        if (eventworld.contains("-iWorld")) {
            try {
                PreparedStatement check = instance.database.prepare(CHECK);

                // UUID_P
                check_p = pPlayer.getUniqueId().toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = eventworld;
                check.setString(2, check_w);

                IworldsUtils.cm("CHECK REQUEST: " + check);
                // Requête
                ResultSet rselect = check.executeQuery();
                IworldsUtils.cm("Monde event: " + eventworld);

                if (pPlayer.hasPermission("iworlds.bypass.teleport")) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "CHECK Sijania vient de vous autoriser la téléporation, car vous faites partie de l'équipe.");
                    return;
                }

                if (rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "CHECK Sijania vient de vous autoriser la téléporation.");
                    return;
                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    IworldsUtils.cm("Monde joueur: " + pPlayer.getWorld().getName());
                    IworldsUtils.cm("Monde event: " + eventworld);
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "CHECK Sijania vient de vous autoriser la téléporation.");
                    return;
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "CHECK Sijania vient de vous refuser la téléportation.");
                    return;
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
            }

        }
    }
}