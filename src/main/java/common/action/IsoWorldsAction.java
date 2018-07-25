package common.action;

import bukkit.Main;
import bukkit.configuration.Configuration;
import bukkit.util.action.StorageAction;
import bukkit.util.console.Command;
import bukkit.util.console.Logger;
import common.ManageFiles;
import common.Manager;
import common.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.location.Locations;
import sponge.util.action.StatAction;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IsoWorldsAction {

    private static final Mysql database = Manager.getInstance().getMysql();
    private static final String servername = Manager.getInstance().getServername();
    private static final Map<String, Integer> lock = Manager.getInstance().getLock();
    public static final DataQuery toId = DataQuery.of("SpongeData", "dimensionId");

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

        // ****** MODULES ******
        // Border
        if (Configuration.getBorder()) {
            // Radius border 500
            int x;
            int y;
            // Radius border 1000
            if (pPlayer.hasPermission("isoworlds.size.1000")) {
                x = Configuration.getLargeRadiusSize();
                y = Configuration.getLargeRadiusSize();
                // Radius border 750
            } else if (pPlayer.hasPermission("isoworlds.size.750")) {
                x = Configuration.getMediumRadiusSize();
                y = Configuration.getMediumRadiusSize();
                // Radius border 500
            } else if (pPlayer.hasPermission("isoworlds.size.500")) {
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
                // Radius border 1000
                if (user.get().hasPermission("isoworlds.size.1000")) {
                    x = (sponge.configuration.Configuration.getLargeRadiusSize() * 2);
                    // Radius border 750
                } else if (user.get().hasPermission("isoworlds.size.750")) {
                    x = (sponge.configuration.Configuration.getMediumRadiusSize() * 2);
                    // Radius border 500
                } else if (user.get().hasPermission("isoworlds.size.500")) {
                    x = (sponge.configuration.Configuration.getSmallRadiusSize() * 2);
                    // Radius border default 250
                } else {
                    x = (sponge.configuration.Configuration.getDefaultRadiusSize() * 2);
                }
            } else {
                // Global
                // Radius border 1000
                if (pPlayer.hasPermission("isoworlds.size.1000")) {
                    x = (sponge.configuration.Configuration.getLargeRadiusSize() * 2);
                    // Radius border 750
                } else if (pPlayer.hasPermission("isoworlds.size.750")) {
                    x = (sponge.configuration.Configuration.getMediumRadiusSize() * 2);
                    // Radius border 500
                } else if (pPlayer.hasPermission("isoworlds.size.500")) {
                    x = (sponge.configuration.Configuration.getSmallRadiusSize() * 2);
                    // Radius border default
                } else {
                    x = (sponge.configuration.Configuration.getDefaultRadiusSize() * 2);
                }
            }


            if (wp.isPresent()) {
                worldProperties = wp.get();
                sponge.util.console.Logger.info("WOLRD PROPERTIES: déjà présent");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(true);
                // ****** MODULES ******
                // Border
                if (sponge.configuration.Configuration.getBorder()) {
                    worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                    worldProperties.setWorldBorderDiameter(x);
                }
                // *********************
                worldProperties.setEnabled(false);
                worldProperties.setEnabled(true);
                Sponge.getServer().saveWorldProperties(worldProperties);
                // ****** MODULES ******
                // Border
                if (sponge.configuration.Configuration.getBorder()) {
                    Optional<org.spongepowered.api.world.World> world = Sponge.getServer().getWorld(worldname);
                    if (world.isPresent()) {
                        world.get().getWorldBorder().setDiameter(x);
                    }
                    sponge.util.console.Logger.warning("Border nouveau: " + x);
                }
                // *********************
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                sponge.util.console.Logger.info("WOLRD PROPERTIES: non présents, création...");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(true);
                // ****** MODULES ******
                // Border
                if (sponge.configuration.Configuration.getBorder()) {
                    worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                    worldProperties.setWorldBorderDiameter(x);
                    Sponge.getServer().saveWorldProperties(worldProperties);
                    sponge.util.console.Logger.warning("Border nouveau: " + x);
                }
                // *********************
            }
            sponge.util.console.Logger.info("WorldProperties à jour");

        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return null;
        }

        return worldProperties;
    }

    // *** SPONGE METHOD
    // Check if isoworld exists and load it if load true
    public static Boolean isPresent(org.spongepowered.api.entity.living.player.Player pPlayer, Boolean load) {

        String CHECK = "SELECT * FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // UUID _P
            check_p = StatAction.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (StatAction.PlayerToUUID(pPlayer) + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, sponge.Main.instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                setWorldProperties(StatAction.PlayerToUUID(pPlayer) + "-IsoWorld", pPlayer);
                if (!sponge.util.action.StorageAction.getStatus(StatAction.PlayerToUUID(pPlayer) + "-IsoWorld")) {
                    if (load) {

                        // TEST
                        Path levelSponge = Paths.get(ManageFiles.getPath() + StatAction.PlayerToUUID(pPlayer) + "-IsoWorld/" + "level_sponge.dat");
                        if (Files.exists(levelSponge)) {
                            DataContainer dc;
                            boolean gz = false;

                            // Find dat
                            try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(levelSponge, StandardOpenOption.READ))) {
                                dc = DataFormats.NBT.readFrom(gzip);
                                gz = true;

                                // get all id
                                ArrayList allId = IsoWorldsAction.getAllDimensionId();

                                // get id
                                int dimId = IsoWorldsAction.getDimensionId(pPlayer);

                                // Si non isoworld ou non défini
                                if (dimId == 0) {
                                    for (int i = 1000; i < Integer.MAX_VALUE; i++) {
                                        if (!allId.contains(i)) {
                                            IsoWorldsAction.setDimensionId(pPlayer, i);
                                            dimId = i;
                                            break;
                                        }
                                    }
                                }

                                dc.set(toId, dimId);

                                // define dat
                                try (OutputStream os = getOutput(gz, levelSponge)) {
                                    DataFormats.NBT.writeTo(os, dc);
                                    os.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Sponge.getServer().loadWorld(StatAction.PlayerToUUID(pPlayer) + "-IsoWorld");
                    }
                }
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

    // *** BUKKIT METHOD
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

    // Get all isoworlds dimension id
    public static ArrayList getAllDimensionId() {
        String CHECK = "SELECT `dimension_id` FROM `isoworlds` WHERE `server_id` = ? ORDER BY `dimension_id` DESC";
        String check_w;
        ArrayList<Integer> dimList = new ArrayList<Integer>();
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, sponge.Main.instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                dimList.add(rselect.getInt("dimension_id"));
            }
            return dimList;
        } catch (Exception se) {
            se.printStackTrace();
            return dimList;
        }
    }

    // Get all trusted players of pPlayer's IsoWorld
    public static Integer getDimensionId(org.spongepowered.api.entity.living.player.Player pPlayer) {
        String CHECK = "SELECT `dimension_id` FROM `isoworlds` WHERE `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // UUID _W
            check_w = pPlayer.getUniqueId().toString() + "-IsoWorld";
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, sponge.Main.instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                return rselect.getInt(1);
            }
        } catch (Exception se) {
            se.printStackTrace();
            return 0;
        }
        return 0;
    }

    // get next dimensionID
    public static Integer getNextDimensionId() {
        // get all id
        ArrayList allId = IsoWorldsAction.getAllDimensionId();

        for (int i = 1000; i < Integer.MAX_VALUE; i++) {
            if (!allId.contains(i)) {
                return i;
            }
        }
        return 0;
    }

    // set isoworld dimension ID
    public static Boolean setDimensionId(org.spongepowered.api.entity.living.player.Player pPlayer, Integer number) {
        String CHECK = "UPDATE `isoworlds` SET `dimension_id` = ? WHERE `uuid_w` = ?";
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // Number
            check.setInt(1, number);
            // UUID_P
            check.setString(2, pPlayer.getUniqueId().toString() + "-IsoWorld");
            // Requête
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
    }
}
