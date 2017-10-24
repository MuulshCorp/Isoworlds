package bukkit.Commandes.SousCommandes;

/**
 * Created by Edwin on 20/10/2017.
 */
import common.ManageFiles;
import bukkit.IworldsBukkit;
import bukkit.Locations.IworldsLocations;
import bukkit.Utils.IworldsUtils;
import common.Msg;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import static bukkit.Utils.IworldsUtils.cmd;

public class CreationCommande {
    static final String INSERT = "INSERT INTO `iworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String INSERT_TRUST = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";

    static IworldsBukkit instance;
    public static void Creation(CommandSender sender, String[] args) {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL);

        try {
            // SELECT WORLD
            if (IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
                return;
            }
        } catch (Exception se){
            se.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-iWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-iWorld");
        // Check si le monde existe déjà
        if (Bukkit.getServer().getWorld(worldname) != null) {
            IworldsUtils.cm("Le monde existe déjà");
            return;
        }

        File deleteFile = new File(fullpath + "region");
        File sourceFile = new File(ManageFiles.getPath() + "PATERN/");
        File destFile = new File(fullpath);

        try {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
        } catch (Exception ie) {
            ie.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
        }

        // Remove - unload - copy - load
        Bukkit.getServer().unloadWorld(worldname, true);
        ManageFiles.deleteDir(deleteFile);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            IworldsUtils.cm(Msg.keys.FICHIERS);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.FICHIERS);
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // INSERT
        try {

            // INSERT
            if (!IworldsUtils.insertCreation(pPlayer, Msg.keys.SQL)) {
                return;
            }

            // INSERT TRUST
            if (!IworldsUtils.insertTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
            return;
        }

        // Configuration du monde
        Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
        IworldsUtils.cmd("wb " + worldname + " set 250 250 0 0");
        Block y = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
        Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, y.getY(), 0);
        IworldsLocations.teleport(pPlayer, worldname);
        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_CREATION_1);
        return;
    }
}
