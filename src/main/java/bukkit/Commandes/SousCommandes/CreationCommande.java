package bukkit.Commandes.SousCommandes;

/**
 * Created by Edwin on 20/10/2017.
 */

import common.ManageFiles;
import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static bukkit.Utils.IsoworldsUtils.setWorldProperties;

public class CreationCommande {

    static IsoworldsBukkit instance;

    public static void Creation(CommandSender sender, String[] args) {

        // Variables
        instance = IsoworldsBukkit.getInstance();
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Integer len = args.length;

        // SELECT WORLD
        if (IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        // Check si le monde existe déjà
        if (Bukkit.getServer().getWorld(worldname) != null) {
            IsoworldsUtils.cm("Le monde existe déjà");
            return;
        }

        // Vérifie le nb argument
        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose 4 types de IsoWorld:");
            pPlayer.sendMessage(ChatColor.GOLD + "- FLAT/OCEAN/NORMAL/VOID: " + ChatColor.AQUA + "/iw creation " + ChatColor.GOLD + "[" + ChatColor.GREEN + "[TYPE]");
            pPlayer.sendMessage(" ");
            return;
        }

        File deleteFile = new File(fullpath + "region");
        File sourceFile;
        switch (args[1]) {
            case ("n"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-N/");
                break;
            case ("v"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-V/");
                break;
            case ("o"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-O/");
                break;
            case ("f"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-F/");
                break;
            default:
                return;
        }

        File destFile = new File(fullpath);

        try {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
        } catch (Exception ie) {
            ie.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
        }

        // Remove - unload - copy - load
        Bukkit.getServer().unloadWorld(worldname, true);
        ManageFiles.deleteDir(deleteFile);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            IsoworldsUtils.cm(Msg.keys.FICHIERS);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.FICHIERS);
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // INSERT
        if (!IsoworldsUtils.setIsoWorld(pPlayer, Msg.keys.SQL)) {
            return;
        }

        // INSERT TRUST
        if (!IsoworldsUtils.setTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
            return;
        }

        IsoworldsLocations.teleport(pPlayer, worldname);

        // Configuration du monde
        setWorldProperties(pPlayer.getDisplayName() + "-IsoWorld", pPlayer);

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_CREATION_1);
    }
}
