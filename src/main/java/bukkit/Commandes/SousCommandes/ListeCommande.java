package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
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

    public static IworldsBukkit instance;

    public static void Liste(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        Player pPlayer = (Player) sender;
        ArrayList<World> worlds = new ArrayList<World>();
        Boolean check = false;
        for(World world : Bukkit.getServer().getWorlds()) {
            if (world.getName() != null) {
                if (world.getName().contains("-iWorld")) {
                    worlds.add(world);
                }
            }
        }

        if (check == true) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "Sijania ne repère aucun iWorld dans le Royaume Isolonice");
            return;
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "[Liste des iWorlds]");
        for(World w : worlds ) {
            String worldname = w.getName();
            String[] split = w.getName().split("-iWorld");
            UUID uuid = UUID.fromString(split[0]);
            Player player = Bukkit.getServer().getPlayer(uuid);
            String pname;
            String status;
            if (!player.isOnline()) {
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

            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + pname + " [" + status +"] | Chunks: " + loadedChunks + " | Entités: " + numOfEntities);
        }
        return;

    }

}
