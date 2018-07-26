package bukkit.util.action;

import bukkit.Main;
import bukkit.configuration.Configuration;
import bukkit.util.console.Command;
import bukkit.util.console.Logger;
import common.Manager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.zip.GZIPOutputStream;

public class IsoworldsAction {

    private static final String servername = Manager.getInstance().getServername();

    // Create IsoWorld for pPlayer
    public static Boolean setIsoWorld(Player pPlayer) {
        String INSERT = "INSERT INTO `isoworlds` (`uuid_p`, `uuid_w`, `date_time`, `server_id`, `status`) VALUES (?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = Main.instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, Main.instance.servername);
            // STATUS
            insert.setInt(5, 0);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Create world properties Isoworlds
    public static void setWorldProperties(String worldname, Player pPlayer) {

        // Properties of IsoWorld
        World world = Bukkit.getServer().getWorld(worldname);

        // ****** MODULES ******
        // Border
        if (Configuration.getBorder()) {
            // Radius border 500
            int x;
            int y;
            // Radius border 1000
            if (pPlayer.hasPermission("Isoworlds.size.1000")) {
                x = Configuration.getLargeRadiusSize();
                y = Configuration.getLargeRadiusSize();
                // Radius border 750
            } else if (pPlayer.hasPermission("Isoworlds.size.750")) {
                x = Configuration.getMediumRadiusSize();
                y = Configuration.getMediumRadiusSize();
                // Radius border 500
            } else if (pPlayer.hasPermission("Isoworlds.size.500")) {
                x = Configuration.getSmallRadiusSize();
                y = Configuration.getSmallRadiusSize();
                // Radius border default
            } else {
                x = Configuration.getDefaultRadiusSize();
                y = Configuration.getDefaultRadiusSize();
            }

            Logger.severe("Size: " + x + " " + y);
            Command.sendCmd("wb " + worldname + " set " + x + " " + y + " 0 0");
        }
        // *********************

        if (world != null) {
            Block yLoc = world.getHighestBlockAt(0, 0);
            world.setPVP(true);
            world.setSpawnLocation(0, yLoc.getY(), 0);
            world.setGameRuleValue("MobGriefing", "false");
        }
        Logger.info("WorldProperties à jour");
    }

    // Check if isoworld exists and load it if load true
    public static Boolean isPresent(Player pPlayer, Boolean load) {
        Main instance;
        instance = Main.getInstance();
        String CHECK = "SELECT * FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // Player uuuid
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // Worldname
            check_w = (check_p + "-IsoWorld");
            check.setString(2, check_w);
            // Server id
            check.setString(3, servername);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                // Load if load param true
                if (!StorageAction.getStatus(check_p + "-IsoWorld")) {
                    if (load) {
                        Bukkit.getServer().createWorld(new WorldCreator(check_p + "-IsoWorld"));
                    }
                }
                setWorldProperties(check_p + "-IsoWorld", pPlayer);
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }

    private static OutputStream getOutput(boolean gzip, Path file) throws IOException {
        OutputStream os = Files.newOutputStream(file);
        if (gzip) {
            return new GZIPOutputStream(os, true);
        }

        return os;
    }
}
