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
import org.spongepowered.api.world.gamerule.DefaultGameRules;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static bukkit.Utils.IsoworldsUtils.isSetCooldown;

public class CreationCommande {

    static IsoworldsBukkit instance;

    public static void Creation(CommandSender sender, String[] args) {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
        if (isSetCooldown(pPlayer, String.class.getName())) {
            return;
        }

        // SELECT WORLD
        if (IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        // Check si le monde existe déjà
        if (Bukkit.getServer().getWorld(worldname) != null) {
            IsoworldsUtils.cm("Le monde existe déjà");
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // INSERT
        if (!IsoworldsUtils.insertCreation(pPlayer, Msg.keys.SQL)) {
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // INSERT TRUST
        if (!IsoworldsUtils.insertTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Configuration du monde
        Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
        IsoworldsUtils.cmd("wb " + worldname + " set 250 250 0 0");
        Block y = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
        Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, y.getY(), 0);
        Bukkit.getServer().getWorld(worldname).setGameRuleValue(DefaultGameRules.MOB_GRIEFING, "false");
        IsoworldsLocations.teleport(pPlayer, worldname);
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_CREATION_1);
        instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}
