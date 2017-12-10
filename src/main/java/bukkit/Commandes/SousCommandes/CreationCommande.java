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
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static bukkit.Utils.IsoworldsUtils.isLocked;
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

        File deleteFile = new File(fullpath + "region");
        File sourceFile = new File(ManageFiles.getPath() + "PATERN/");
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

        // Configuration du monde
        setWorldProperties(pPlayer.getDisplayName() + "-IsoWorld", pPlayer);

        IsoworldsLocations.teleport(pPlayer, worldname);
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_CREATION_1);
        return;
    }
}
