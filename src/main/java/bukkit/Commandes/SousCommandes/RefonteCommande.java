package bukkit.Commandes.SousCommandes;

import common.ManageFiles;
import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
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

        instance = IworldsBukkit.getInstance();

        final String Iuuid_p;
        final String Iuuid_w;
        final String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ?";
        final String DELETE_IWORLDS = "DELETE FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;

        // Confirm
        IworldsUtils.cm("Timestamp: " + timestamp);

        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            IworldsUtils.cm("Value3: ");
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CONFIRM: Sijania vous indique de rentrer la commande de nouveau pour confirmer la refonte.");
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            IworldsUtils.cm("Timestamp: " + millis);
            IworldsUtils.cm("Timestamp: " + minutes);
            if (minutes >= 1) {
                IworldsUtils.cm("Timestamp: supérieur à 1 minute, suppression");
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CONFIRM: Sijania vous indique de rentrer la commande de nouveau pour confirmer la refonte.");
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
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que vous ne possédez aucun iWorld, elle vous recommande d'entrez la commande: /iw creation.");
            return;
        }
        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location refonte = new Location (Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(refonte);
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania entame une destruction entière de l'iWorld dans lequel vous vous trouviez sur demande de son propriétaire, vous avez été renvoyé au spawn pour votre protection.");
            }
            World world = Bukkit.getServer().getWorld(worldname);
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
            IworldsUtils.cm("L'iWorld :" + worldname + " a bien été déchargé");
        }

        //iWorldsUtils.deleteDir(sourceDir);
        IworldsUtils.cm("Le monde :" + worldname + " a bien été déplacé dans le dossier de refonte");
        File remove = new File ((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);

        IworldsUtils.cm("Le monde a bien été supprimé");
        //move(sourceDir, destDir);
        IworldsUtils.cm("sourceDir: " + sourceDir);
        IworldsUtils.cm("destDir: " + destDir);

        // DELETE
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = instance.database.prepare(DELETE_IWORLDS);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            delete_iworlds.setString(1, Iuuid_p);

            // UUID_W
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            delete_autorisations.setString(1, Iuuid_w);
            delete_iworlds.setString(2, Iuuid_w);

            IworldsUtils.cm("DELETE_AUTORISATIONS: " + delete_autorisations);
            IworldsUtils.cm("DELETE_IWORLDS" + delete_iworlds);

            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "INSERT Sijania n'est pas parvenue à refondre votre iWorld, veuillez contacter l'équipe Isolonice.");
            return;
        }

        IworldsUtils.cm("Fin de la procédure de refonte");
        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania vient de terminer son travail, vous pouvez lui demander un nouveau iWorld en entrant la commande: /iw creation.");
        return;

    }
}
