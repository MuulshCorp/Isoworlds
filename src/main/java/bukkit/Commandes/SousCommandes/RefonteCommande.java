package bukkit.Commandes.SousCommandes;

import common.ManageFiles;
import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edwin on 20/10/2017.
 */
public class RefonteCommande {

    public static IworldsBukkit instance;
    final static Map<String, Timestamp> confirm = new HashMap<String, Timestamp>();

    public static void Refonte(CommandSender sender, String[] args) {



        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        instance = IworldsBukkit.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // SELECT WORLD
        if (!IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        // Confirmation
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
                return;
            }
        }

        confirm.remove(pPlayer.getUniqueId().toString());

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-iWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-iWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/iWORLDS-REFONTE/" + worldname);
        destDir.mkdir();
        if (Bukkit.getServer().getWorld(worldname) == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }
        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location refonte = new Location(Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(refonte);
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.REFONTE_KICK);
            }
            World world = Bukkit.getServer().getWorld(worldname);
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
        }

        //iWorldsUtils.deleteDir(sourceDir);
        File remove = new File((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);

        // DELETE WORLD
        if (!IworldsUtils.deleteIworld(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        // DELETE AUTORISATIONS
        if (!IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
        CommandSender newSender = pPlayer.getPlayer();
        CreationCommande.Creation(newSender, args);
        return;

    }
}
