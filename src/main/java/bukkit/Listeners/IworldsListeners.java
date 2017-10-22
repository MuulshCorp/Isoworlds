package bukkit.Listeners;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
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
        Location spawn = Bukkit.getServer().getWorld(worldname).getSpawnLocation();
        Integer maxy = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Location top = new Location(Bukkit.getServer().getWorld(worldname), 0, maxy, 0);

        p.teleport(top);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        String check_p;
        String check_w;

        Player pPlayer = event.getPlayer();
        //String eventworld = event.getFromTransform().getExtent().getName();
        String eventworld = event.getTo().toString();
        //String worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        //IworldsUtils.cm("To: " + eventworld);
        //IworldsUtils.cm("World: " + worldname);*

        if (Bukkit.getServer().getWorld(eventworld) == null) {
            IworldsUtils.cm("Ce monde doit être chargé avant téléportation, préparation...");
            Bukkit.getServer().createWorld(new WorldCreator(event.getTo().toString()));
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
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania vient de vous autoriser la téléporation, car vous faites partie de l'équipe.");
                    return;
                }

                if (rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania vient de vous autoriser la téléporation.");

                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    IworldsUtils.cm("Monde joueur: " + pPlayer.getWorld().getName());
                    IworldsUtils.cm("Monde event: " + eventworld);
                    IworldsUtils.cm("CHECK: Le joueur est autorisé");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania vient de vous autoriser la téléporation.");
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania vient de vous refuser la téléportation.");
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
            }

        }
    }
}