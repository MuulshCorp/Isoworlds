package bukkit.Commandes.SousCommandes;

import common.Cooldown;
import common.ManageFiles;
import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static bukkit.Utils.IsoworldsUtils.isSetCooldown;

/**
 * Created by Edwin on 20/10/2017.
 */
public class RefonteCommande {

    public static IsoworldsBukkit instance;
    final static Map<String, Timestamp> confirm = new HashMap<String, Timestamp>();

    public static void Refonte(CommandSender sender, String[] args) {


        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        instance = IsoworldsBukkit.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //If the method return true then the command is in cooldown
        if (Cooldown.isPlayerCooldown(pPlayer, Cooldown.REFONTE, Cooldown.REFONTE_DELAY)) {
            return;
        }

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
//        if (isSetCooldown(pPlayer, String.class.getName())) {
//            return;
//        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            IsoworldsUtils.cm("DEBUG 1");
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Confirmation
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
                instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
        }

        confirm.remove(pPlayer.getUniqueId().toString());

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/IsoWorlds-REFONTE/" + worldname);
        destDir.mkdir();
        if (Bukkit.getServer().getWorld(worldname) == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }
        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location refonte = new Location(Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(refonte);
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.REFONTE_KICK);
            }
            World world = Bukkit.getServer().getWorld(worldname);
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
        }

        //iWorldsUtils.deleteDir(sourceDir);
        File remove = new File((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);

        // DELETE WORLD
        if (!IsoworldsUtils.deleteIworld(pPlayer, Msg.keys.SQL)) {
            IsoworldsUtils.cm("DEBUG 2");
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
        CommandSender newSender = pPlayer.getPlayer();
        CreationCommande.Creation(newSender, args);
        instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;

    }
}
