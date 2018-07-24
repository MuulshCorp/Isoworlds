package common.action;

import bukkit.Main;
import bukkit.util.action.StorageAction;
import bukkit.util.console.Command;
import bukkit.util.console.Logger;
import common.MainInterface;
import common.Manager;
import common.Msg;
import common.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.location.Locations;
import sponge.util.action.StatAction;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class IsoWorldsAction {

    private static final Mysql database = Manager.getInstance().getMysql();
    private static final String servername = Manager.getInstance().getServername();
    private static final Map<String, Integer> lock = Manager.getInstance().getLock();

    // Create IsoWorld
    public static Boolean setIsoWorld(String playeruuid) {
        String INSERT = "INSERT INTO `isoworlds` (`uuid_p`, `uuid_w`, `date_time`, `server_id`, `status`) VALUES (?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = database.prepare(INSERT);
            // Player uuid
            Iuuid_p = playeruuid;
            insert.setString(1, Iuuid_p);
            // World name
            Iuuid_w = (playeruuid + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, servername);
            // Status of iw
            insert.setInt(5, 0);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Delete IsoWorld
    public static Boolean deleteIsoWorld(String playeruuid) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `uuid_w` = ? AND `server_id` = ?";
        String DELETE_IWORLDS = "DELETE FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        try {
            PreparedStatement delete_autorisations = database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = database.prepare(DELETE_IWORLDS);
            Iuuid_p = playeruuid;
            Iuuid_w = (playeruuid + "-IsoWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, servername);

            // delete isoworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // *** BUKKIT METHOD
    // Create world properties IsoWorlds
    public static void setWorldProperties(String worldname, Player pPlayer) {

        // Properties of IsoWorld
        World world = Bukkit.getServer().getWorld(worldname);
        // Radius border 500
        int x;
        int y;
        if (pPlayer.hasPermission("isoworlds.size.1000")) {
            x = 1000;
            y = 1000;
            // Radius border 750
        } else if (pPlayer.hasPermission("isoworlds.size.750")) {
            x = 750;
            y = 750;
            // Radius border 1000
        } else if (pPlayer.hasPermission("isoworlds.size.500")) {
            x = 500;
            y = 500;
            // Radius border default
        } else {
            x = 250;
            y = 250;
        }

        Logger.severe("Size: " + x + " " + y);
        Command.sendCmd("wb " + worldname + " set " + x + " " + y + " 0 0");

        if (world != null) {
            Block yLoc = world.getHighestBlockAt(0, 0);
            world.setPVP(true);
            world.setSpawnLocation(0, yLoc.getY(), 0);
            world.setGameRuleValue("MobGriefing", "false");
        }
        Logger.info("WorldProperties à jour");
    }

    // *** SPONGE METHOD
    // Create world properties IsoWorlds
    public static WorldProperties setWorldProperties(String worldname, org.spongepowered.api.entity.living.player.Player pPlayer) {

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            // Deal with permission of owner only
            int x;
            String username = worldname.split("-IsoWorld")[0];
            Optional<User> user = StatAction.getPlayerFromUUID(UUID.fromString(username));
            if (!username.equals(pPlayer.getUniqueId().toString())) {
                // Global
                // Radius border 500
                if (user.get().hasPermission("isoworlds.size.1000")) {
                    x = 2000;
                    // Radius border 750
                } else if (user.get().hasPermission("isoworlds.size.750")) {
                    x = 1500;
                    // Radius border 1000
                } else if (user.get().hasPermission("isoworlds.size.500")) {
                    x = 1000;
                    // Radius border default
                } else {
                    x = 500;
                }
            } else {
                // Global
                // Radius border 500
                if (pPlayer.hasPermission("isoworlds.size.1000")) {
                    x = 2000;
                    // Radius border 750
                } else if (pPlayer.hasPermission("isoworlds.size.750")) {
                    x = 1500;
                    // Radius border 1000
                } else if (pPlayer.hasPermission("isoworlds.size.500")) {
                    x = 1000;
                    // Radius border default
                } else {
                    x = 500;
                }
            }


            if (wp.isPresent()) {
                worldProperties = wp.get();
                sponge.util.console.Logger.info("WOLRD PROPERTIES: déjà présent");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(true);
                worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                worldProperties.setWorldBorderDiameter(x);
                worldProperties.setEnabled(false);
                worldProperties.setEnabled(true);
                Sponge.getServer().saveWorldProperties(worldProperties);
                // Border
                Optional<org.spongepowered.api.world.World> world = Sponge.getServer().getWorld(worldname);
                if (world.isPresent()) {
                    world.get().getWorldBorder().setDiameter(x);
                }
                sponge.util.console.Logger.warning("Border nouveau: " + x);
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                sponge.util.console.Logger.info("WOLRD PROPERTIES: non présents, création...");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(true);
                worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                worldProperties.setWorldBorderDiameter(x);
                Sponge.getServer().saveWorldProperties(worldProperties);
                sponge.util.console.Logger.warning("Border nouveau: " + x);
            }
            sponge.util.console.Logger.info("WorldProperties à jour");

        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            sponge.util.console.Logger.severe(Msg.keys.SQL);
            lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return null;
        }

        return worldProperties;
    }

    // *** SPONGE METHOD
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

    // *** BUKKIT METHOD
    // Check if isoworld exists and load it if load true
    public static Boolean isPresent(org.spongepowered.api.entity.living.player.Player pPlayer, Boolean load) {
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

    // Used for construction, check if isoworld is in database (don't care charged or not)
    public static Boolean iwExists(String playeruuid) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = database.prepare(CHECK);
            // Player uuid
            check_p = playeruuid;
            check.setString(1, check_p);
            // Worldname
            check_w = playeruuid + "-IsoWorld";
            check.setString(2, check_w);
            // Server id
            check.setString(3, servername);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }
}
