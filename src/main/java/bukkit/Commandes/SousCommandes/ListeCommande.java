package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ListeCommande {

    public static IsoworldsBukkit instance;

    public static void Liste(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();
        Player pPlayer = (Player) sender;
        ArrayList<World> worlds = new ArrayList<World>();
        Boolean check = false;
        for(World world : Bukkit.getServer().getWorlds()) {
            if (world.getName() != null) {
                if (world.getName().contains("-IsoWorld")) {
                    worlds.add(world);
                }
            }
        }

        if (check == true) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania ne repère aucun IsoWorld dans le Royaume Isolonice");
            return;
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "[Liste des IsoWorlds]");
        for(World w : worlds ) {
            String worldname = w.getName();
            String[] split = w.getName().split("-IsoWorld");
            UUID uuid = UUID.fromString(split[0]);
            Player player = Bukkit.getServer().getPlayer(uuid);
            String pname;
            String status;
            if (player.isOnline()) {
                pname = player.getName();
            } else {
                pname = "OFF";
            }

            if (player.isOnline()) {
                status = "ON";
            } else {
                status = "OFF";
            }

            int numOfEntities = w.getEntities().size();
            int loadedChunks = (w.getLoadedChunks().length);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + pname + " [" + status +"] | Chunks: " + loadedChunks + " | Entités: " + numOfEntities);
        }
        return;

    }

}
