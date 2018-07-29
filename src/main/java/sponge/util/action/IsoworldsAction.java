package sponge.util.action;

import common.ManageFiles;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.Main;
import sponge.location.Locations;

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

public class IsoworldsAction {

    private static final Map<String, Integer> lock = Main.instance.getLock();
    public static final DataQuery toId = DataQuery.of("SpongeData", "dimensionId");

    // Create Isoworld for pPlayer
    public static Boolean setIsoworld(Player pPlayer) {
        String INSERT = "INSERT INTO `isoworlds` (`uuid_p`, `uuid_w`, `date_time`, `server_id`, `status`, `dimension_id`) VALUES (?, ?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = Main.instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-Isoworld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, Main.instance.servername);
            // STATUS
            insert.setInt(5, 0);
            // DIMENSION_ID
            int id = getNextDimensionId();
            if (id == 0) {
                return false;
            }
            insert.setInt(6, id);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Create world properties Isoworlds
    public static WorldProperties setWorldProperties(String worldname, Player pPlayer) {

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            // Deal with permission of owner only

            int x;
            String username = worldname.split("-Isoworld")[0];
            Optional<User> user = StatAction.getPlayerFromUUID(UUID.fromString(username));
            if (!username.equals(pPlayer.getUniqueId().toString())) {
                // Global
                // Radius border 1000
                if (user.get().hasPermission("Isoworlds.size.1000")) {
                    x = (sponge.configuration.Configuration.getLargeRadiusSize() * 2);
                    // Radius border 750
                } else if (user.get().hasPermission("Isoworlds.size.750")) {
                    x = (sponge.configuration.Configuration.getMediumRadiusSize() * 2);
                    // Radius border 500
                } else if (user.get().hasPermission("Isoworlds.size.500")) {
                    x = (sponge.configuration.Configuration.getSmallRadiusSize() * 2);
                    // Radius border default 250
                } else {
                    x = (sponge.configuration.Configuration.getDefaultRadiusSize() * 2);
                }
            } else {
                // Global
                // Radius border 1000
                if (pPlayer.hasPermission("Isoworlds.size.1000")) {
                    x = (sponge.configuration.Configuration.getLargeRadiusSize() * 2);
                    // Radius border 750
                } else if (pPlayer.hasPermission("Isoworlds.size.750")) {
                    x = (sponge.configuration.Configuration.getMediumRadiusSize() * 2);
                    // Radius border 500
                } else if (pPlayer.hasPermission("Isoworlds.size.500")) {
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

    // Check if Isoworld exists and load it if load true
    public static Boolean isPresent(Player pPlayer, Boolean load) {

        String CHECK = "SELECT * FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // UUID _P
            check_p = StatAction.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (StatAction.PlayerToUUID(pPlayer) + "-Isoworld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, sponge.Main.instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                setWorldProperties(StatAction.PlayerToUUID(pPlayer) + "-Isoworld", pPlayer);
                if (!sponge.util.action.StorageAction.getStatus(StatAction.PlayerToUUID(pPlayer) + "-Isoworld")) {
                    if (load) {

                        // TEST
                        Path levelSponge = Paths.get(ManageFiles.getPath() + StatAction.PlayerToUUID(pPlayer) + "-Isoworld/" + "level_sponge.dat");
                        if (Files.exists(levelSponge)) {
                            DataContainer dc;
                            boolean gz = false;

                            // Find dat
                            try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(levelSponge, StandardOpenOption.READ))) {
                                dc = DataFormats.NBT.readFrom(gzip);
                                gz = true;

                                // get all id
                                ArrayList allId = IsoworldsAction.getAllDimensionId();

                                // get id
                                int dimId = IsoworldsAction.getDimensionId(pPlayer);

                                // Si non Isoworld ou non défini
                                if (dimId == 0) {
                                    for (int i = 1000; i < Integer.MAX_VALUE; i++) {
                                        if (!allId.contains(i)) {
                                            IsoworldsAction.setDimensionId(pPlayer, i);
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

                        Sponge.getServer().loadWorld(StatAction.PlayerToUUID(pPlayer) + "-Isoworld");
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

    private static OutputStream getOutput(boolean gzip, Path file) throws IOException {
        OutputStream os = Files.newOutputStream(file);
        if (gzip) {
            return new GZIPOutputStream(os, true);
        }

        return os;
    }

    // Get all Isoworlds dimension id
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

    // Get all trusted players of pPlayer's Isoworld
    public static Integer getDimensionId(Player pPlayer) {
        String CHECK = "SELECT `dimension_id` FROM `isoworlds` WHERE `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // UUID _W
            check_w = pPlayer.getUniqueId().toString() + "-Isoworld";
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
        ArrayList allId = IsoworldsAction.getAllDimensionId();

        for (int i = 1000; i < Integer.MAX_VALUE; i++) {
            if (!allId.contains(i)) {
                return i;
            }
        }
        return 0;
    }

    // set Isoworld dimension ID
    public static Boolean setDimensionId(org.spongepowered.api.entity.living.player.Player pPlayer, Integer number) {
        String CHECK = "UPDATE `isoworlds` SET `dimension_id` = ? WHERE `uuid_w` = ?";
        try {
            PreparedStatement check = sponge.Main.instance.database.prepare(CHECK);

            // Number
            check.setInt(1, number);
            // UUID_P
            check.setString(2, pPlayer.getUniqueId().toString() + "-Isoworld");
            // Requête
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
    }
}
