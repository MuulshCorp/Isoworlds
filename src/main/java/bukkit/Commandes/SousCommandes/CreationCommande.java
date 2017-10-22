package bukkit.Commandes.SousCommandes;

/**
 * Created by Edwin on 20/10/2017.
 */
import common.ManageFiles;
import bukkit.IworldsBukkit;
import bukkit.Locations.IworldsLocations;
import bukkit.Utils.IworldsUtils;
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
    static final String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";


    static IworldsBukkit instance;
    public static void Creation(CommandSender sender, String[] args) {
        instance = IworldsBukkit.getInstance();

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        final String check_w;
        final String check_p;
        final String Iuuid_p;
        final String Iuuid_w;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            IworldsUtils.cm("test2: " + check);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            check.setString(2, check_w);

            IworldsUtils.cm("CHECK REQUEST: " + check);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                IworldsUtils.cm("CHECK: Le joueur existe déjà");
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK 1 Sijania indique que votre iWorld est déjà créé.");
                return;
            }
        } catch (Exception se){
            se.printStackTrace();
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK 2 Sijania indique que votre iWorld est déjà créé.");
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania entame la construction de votre iWorld...");
        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-iWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-iWorld");
        IworldsUtils.cm("fullpath: " + fullpath);
        IworldsUtils.cm("worldname: " + worldname);

        // Check si le monde existe déjà
        if (Bukkit.getServer().getWorld(worldname) != null) {
            IworldsUtils.cm("Le monde existe déjà");
            return;
        }

        File deleteFile = new File(fullpath + "region");
        File sourceFile = new File(ManageFiles.getPath() + "PATERN/");
        File destFile = new File(fullpath);

//        Integer t = 0;
//        if (t==0){
//            return;
//        }

        try {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
        } catch (Exception ie) {
            ie.printStackTrace();
        }

        // Remove - unload - copy - load

        Bukkit.getServer().unloadWorld(worldname, true);

        ManageFiles.deleteDir(deleteFile);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania ne parvient pas à créer votre iWorld, veuillez contacter un membre de l'équipe.");
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Dans le cas ou vous possédez déjà un iWorld et que vous souhaitez le réinitialiser: /iw refonte.");
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // INSERT
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            PreparedStatement insert_trust = instance.database.prepare(INSERT_TRUST);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            insert_trust.setString(1, Iuuid_p);

            // UUID_W
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            insert.setString(2, Iuuid_w);
            insert_trust.setString(2, Iuuid_w);
            IworldsUtils.cm("INSERT REQUEST: " + insert);

            // Date
            insert.setString(3, (timestamp.toString()));
            insert_trust.setString(3, (timestamp.toString()));

            insert.executeUpdate();
            insert_trust.executeUpdate();
        } catch (Exception ex) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "INSERT Sijania indique que votre iWorld est déjà créé.");
            return;
        }

        IworldsUtils.cm("INSERT REQUEST: OK");

        // Configuration du monde
        Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
        IworldsUtils.cm("iWorld " + worldname + ": KeepSpawnLoaded activé.");
        cmd("wb " + worldname + "set 250 250 0 0");
        IworldsUtils.cm("iWorld " + worldname + ": Centre du WorldBorder défini en x:0, y:0.");
        IworldsUtils.cm("iWorld " + worldname + ": Diamètre du WorldBorder défini à 500");
        Block y = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
        Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, y.getY(), 0);
        IworldsUtils.cm("Point de spawn défini");

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania vient de terminer son oeuvre, voici votre iWorld !");

        IworldsLocations.teleport(pPlayer, worldname);

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Cet iWorld, vous appartient désormais !");
        return;
    }
}
