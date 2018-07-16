/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
 *
 * Copyright (c) Edwin Petremann <https://github.com/Isolonice/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sponge.util.action;

import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.MainSponge;
import sponge.location.Locations;
import sponge.util.console.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IsoWorldsAction {

    public static final MainSponge plugin = MainSponge.instance;
    public static final DataQuery toId = DataQuery.of("SpongeData", "dimensionId");

    // Create IsoWorld for pPlayer
    public static Boolean setIsoWorld(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`, `STATUS`, `DIMENSION_ID`) VALUES (?, ?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, plugin.servername);
            // STATUS
            insert.setInt(5, 0);
            // DIMENSION_ID
            int id = IsoWorldsAction.getNextDimensionId();
            if (id == 0) {
                return false;
            }
            insert.setInt(6, id);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Delete IsoWorld of pPlayer
    public static Boolean deleteIsoWorld(Player pPlayer, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String DELETE_IWORLDS = "DELETE FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = plugin.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = plugin.database.prepare(DELETE_IWORLDS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, plugin.servername);

            // delete iworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, plugin.servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Create world properties IsoWorlds
    public static WorldProperties setWorldProperties(String worldname, Player pPlayer) {

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
                Logger.info("WOLRD PROPERTIES: déjà présent");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
                worldProperties.setPVPEnabled(true);
                worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                worldProperties.setWorldBorderDiameter(x);
                worldProperties.setEnabled(false);
                worldProperties.setEnabled(true);
                Sponge.getServer().saveWorldProperties(worldProperties);
                // Border
                Optional<World> world = Sponge.getServer().getWorld(worldname);
                if (world.isPresent()) {
                    world.get().getWorldBorder().setDiameter(x);
                }
                Logger.warning("Border nouveau: " + x);
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                Logger.info("WOLRD PROPERTIES: non présents, création...");
                worldProperties.setKeepSpawnLoaded(false);
                worldProperties.setLoadOnStartup(false);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
                worldProperties.setPVPEnabled(true);
                worldProperties.setWorldBorderCenter(Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getZ());
                worldProperties.setWorldBorderDiameter(x);
                Sponge.getServer().saveWorldProperties(worldProperties);
                Logger.warning("Border nouveau: " + x);
            }
            Logger.info("WorldProperties à jour");

        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return null;
        }

        return worldProperties;
    }

    // Check if pPlayer's IsoWorld is created on database
    public static Boolean isPresent(Player pPlayer, String messageErreur, Boolean load) {

        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = StatAction.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (StatAction.PlayerToUUID(pPlayer) + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                setWorldProperties(StatAction.PlayerToUUID(pPlayer) + "-IsoWorld", pPlayer);
                if (!StorageAction.getStatus(StatAction.PlayerToUUID(pPlayer) + "-IsoWorld", Msg.keys.SQL)) {
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
                                ArrayList allId = IsoWorldsAction.getAllDimensionId(Msg.keys.SQL);

                                // get id
                                int dimId = IsoWorldsAction.getDimensionId(pPlayer, Msg.keys.SQL);

                                // Si non isoworld ou non défini
                                if (dimId == 0) {
                                    for (int i = 1000; i < Integer.MAX_VALUE; i++) {
                                        if (!allId.contains(i)) {
                                            IsoWorldsAction.setDimensionId(pPlayer, i, Msg.keys.SQL);
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
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // Used for construction, check if isoworld is in database (don't care charged or not)
    public static Boolean iwExists(String uuid, String messageErreur) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check_p = uuid;
            check.setString(1, check_p);
            // UUID_W
            check_w = uuid + "-IsoWorld";
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            return false;
        }
        return false;
    }

    // Get all isoworlds dimension id
    public static ArrayList getAllDimensionId(String messageErreur) {
        String CHECK = "SELECT `DIMENSION_ID` FROM `isoworlds` WHERE `SERVEUR_ID` = ? ORDER BY `DIMENSION_ID` DESC";
        String check_w;
        ArrayList<Integer> dimList = new ArrayList<Integer>();
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                dimList.add(rselect.getInt("DIMENSION_ID"));
            }
            return dimList;
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            return dimList;
        }
    }

    // Get all trusted players of pPlayer's IsoWorld
    public static Integer getDimensionId(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `DIMENSION_ID` FROM `isoworlds` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _W
            check_w = pPlayer.getUniqueId().toString() + "-IsoWorld";
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                return rselect.getInt(1);
            }
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return 0;
        }
        return 0;
    }

    // get next dimensionID
    public static Integer getNextDimensionId() {
        // get all id
        ArrayList allId = IsoWorldsAction.getAllDimensionId(Msg.keys.SQL);

        for (int i = 1000; i < Integer.MAX_VALUE; i++) {
            if (!allId.contains(i)) {
                return i;
            }
        }
        return 0;
    }

    // set isoworld dimension ID
    public static Boolean setDimensionId(Player pPlayer, Integer number, String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `DIMENSION_ID` = ? WHERE `UUID_W` = ?";
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // Number
            check.setInt(1, number);
            // UUID_P
            check.setString(2, pPlayer.getUniqueId().toString() + "-IsoWorld");
            // Requête
            Logger.info("Debug 3: " + check.toString());
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(messageErreur);
            return false;
        }
    }

    private static OutputStream getOutput(boolean gzip, Path file) throws IOException {
        OutputStream os = Files.newOutputStream(file);
        if (gzip) {
            return new GZIPOutputStream(os, true);
        }

        return os;
    }


}
