package sponge.Utils;

import common.Cooldown;
import common.ManageFiles;
import common.Msg;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.IsoworldsSponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edwin on 08/10/2017.
 */
public class IsoworldsUtils {

    public static final IsoworldsSponge plugin = IsoworldsSponge.instance;

    // ------------------------------------------------- USER MANIPULATION

    // Méthode pour convertir type player à uuid
    public static UUID PlayerToUUID(Player player) {
        UUID uuid;
        uuid = player.getUniqueId();
        return (uuid);
    }


    // ------------------------------------------------- MESSAGES

    // Envoie un message au serveur
    public static void sm(String msg) {
        Sponge.getServer().getBroadcastChannel().send(Text.of("[IsoWorlds]: " + msg));
    }

    // Envoie un message à la console
    public static void cm(String msg) {
        Sponge.getServer().getConsole().sendMessage(Text.of("[IsoWorlds]: " + msg));
    }

    // Executer une commande sur le serveur
    public void cmds(String cmd) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
    }

    // Titles
    public static Title title(String message) {
        Text text = Text.of(Text.builder(message).color(TextColors.AQUA).build());
        Title title = Title.builder().title(Text.of(text)).build();
        return title;
    }

    // SubTitle
    public static Title subtitle(String message) {
        Text text = Text.of(Text.builder(message).color(TextColors.GOLD).build());
        Title subtitle = Title.builder().subtitle(Text.of(message)).build();
        return subtitle;
    }

    // Tiltle with SubTitle
    public static Title titleSubtitle(String title, String subtitle) {
        Text Titre = Text.of(Text.builder(title).color(TextColors.GOLD).build());
        Text SousTitre = Text.of(Text.builder(subtitle).color(TextColors.AQUA).build());
        Title ready = (Title) Title.of(Titre, SousTitre);
        return ready;
    }

    // Send colored Message base
    public static void coloredMessage(Player pPlayer, String message) {
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(message).color(TextColors.AQUA))).build()));
    }


    // ------------------------------------------------- ISOWORLDS MANIPULATION

    // Get all IsoWorlds that trusted pPlayer
    public static ResultSet getAccess(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `UUID_W` FROM `autorisations` WHERE `UUID_P` = ? AND `SERVEUR_ID` = ?";
        String check_p;
        ResultSet result = null;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = IsoworldsUtils.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // SERVEUR_ID
            check.setString(2, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return result;
        }
        return result;
    }

    // Get all trusted players of pPlayer's IsoWorld
    public static ResultSet getTrusts(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `UUID_P` FROM `autorisations` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        ResultSet result = null;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _W
            check_w = pPlayer.getUniqueId().toString() + "-IsoWorld";
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return result;
        }
        return result;
    }

    // Check status of a IsoWorld, if is Pushed return true, else return false
    public static Boolean getStatus(String world, String messageErreur) {
        String CHECK = "SELECT STATUS FROM `isoworlds` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID_W
            check_w = world;
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            IsoworldsUtils.cm(check.toString());
            IsoworldsUtils.cm("Debug 8");
            while (rselect.next()) {
                IsoworldsUtils.cm(rselect.toString());
                IsoworldsUtils.cm("Debug 9");
                if (rselect.getInt(1) == 1) {
                    IsoworldsUtils.cm("Debug 10");
                    return true;
                } else {
                    return false;
                }

            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
        return false;
    }

    // Set global status
    public static Boolean setGlobalStatus(String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `STATUS` = 1 WHERE `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            IsoworldsUtils.cm(check.toString());
        } catch (Exception e) {
            e.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
        return true;
    }

    // Create IsoWorld for pPlayer
    public static Boolean setIsoWorld(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`, `STATUS`) VALUES (?, ?, ?, ?, ?)";
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
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Create trust for uuidcible on pPlayer IsoWorld
    public static Boolean setTrust(Player pPlayer, UUID uuidcible, String messageErreur) {
        String INSERT = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`) VALUES (?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = uuidcible.toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, plugin.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Set status of IsoWorld (1 for Pushed, 0 for Present)
    // It returns true if pushed, false si envoyé ou à envoyer
    public static void setStatus(String world, Integer status, String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `STATUS` = ? WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // STATUS
            check.setInt(1, status);
            // UUID_W
            check_w = (world);
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            IsoworldsUtils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
        }
    }

    // Create world properties IsoWorlds
    public static WorldProperties setWorldProperties(String worldname, Player pPlayer) {

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            if (wp.isPresent()) {
                worldProperties = wp.get();
                IsoworldsUtils.cm("WOLRD PROPERTIES: déjà présent");
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                IsoworldsUtils.cm("WOLRD PROPERTIES: non présents, création...");

            }

            // Deal with permission of owner only
            int x;
            String username = worldname.split("-IsoWorld")[0];
            Optional<User> user = IsoworldsUtils.getPlayerFromUUID(UUID.fromString(username));
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


            worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            worldProperties.setKeepSpawnLoaded(false);
            worldProperties.setLoadOnStartup(false);
            worldProperties.setGenerateSpawnOnLoad(false);
            worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
            worldProperties.setPVPEnabled(true);
            worldProperties.setWorldBorderCenter(0, 0);
            worldProperties.setWorldBorderDiameter(x);

            // Spawn
            //Location<World> neutral = new Location<World>(Sponge.getServer().getWorld(worldname).get(), 0, 0, 0);
            //Location<World> firstspawn = IsoworldsLocations.getHighestLoc(neutral).orElse(null);
            //worldProperties.setSpawnPosition(firstspawn.getBlockPosition()    );


            // Sauvegarde
            Sponge.getServer().saveWorldProperties(worldProperties);
            IsoworldsUtils.cm("WorldProperties à jour");

        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return null;
        }

        return worldProperties;
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
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Delete trust uuid of pPlayer's IsoWorld
    public static Boolean deleteTrust(Player pPlayer, UUID uuid, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = plugin.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = uuid.toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisation
            delete_autorisations.setString(1, Iuuid_p);
            delete_autorisations.setString(2, Iuuid_w);
            delete_autorisations.setString(3, plugin.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Check if uuid cible is trusted on pPlayer's IsoWorld
    public static Boolean isTrusted(Player pPlayer, UUID uuidcible, String messageErreur) {

        String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check_p = uuidcible.toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId() + "-IsoWorld");
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
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // Check if pPlayer's IsoWorld is created on database
    public static Boolean isPresent(Player pPlayer, String messageErreur, Boolean load) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = IsoworldsUtils.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                setWorldProperties(IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld", pPlayer);
                if (!IsoworldsUtils.getStatus(IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld", Msg.keys.SQL)) {
                    if (load) {
                        Sponge.getServer().loadWorld(IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
                    }
                }
                return true;
            }

        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // Check tag of pPlayer IsoWorld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTag(Player pPlayer, String worldname) {
        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        Integer limit = 0;
        // Vérification si monde en statut pushed
        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (IsoworldsUtils.getStatus(worldname, Msg.keys.SQL)) {
            IsoworldsUtils.cm("Debug 6");
            // Création des chemins pour vérification
            File file = new File(ManageFiles.getPath() + worldname);
            File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");


            // Suppression si doublon (généré sans autorisation), on remove le non tag
            if (file.exists() & file2.exists()) {
                IsoworldsLogger.warning(" --- Anomalie: Dossier isoworld et isoworld tag tous deux présents pour: " + worldname + " ---");
                ManageFiles.deleteDir(file);
            }

            // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue
            if (file.exists()) {
                IsoworldsUtils.cm("Debug 7");
                IsoworldsUtils.setStatus(worldname, 0, Msg.keys.SQL);
                // Si le dossier est en @PUSHED et qu'un joueur le demande alors on le passe en @PULL
                // Le script check ensutie
                return true;
            } else {
                Task task = Task.builder()
                        .execute(new IsoWorldsTasks(pPlayer, file))
                        .async()
                        .interval(1, TimeUnit.SECONDS)
                        .name("Self-Cancelling Timer Task")
                        .submit(plugin);
            }
            if (file2.exists()) {
                IsoworldsUtils.cm("TEST 0");
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                IsoworldsUtils.cm("PULL OK");
                return false;
            }
            return false;
        }
        return true;
    }

    // COPY FOR CHARGERCOMMANDE
    // Check tag of pPlayer IsoWorld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTagCharger(String worldname) {
        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        Integer limit = 0;
        // Vérification si monde en statut pushed
        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (IsoworldsUtils.getStatus(worldname, Msg.keys.SQL)) {
            // Création des chemins pour vérification
            File file = new File(ManageFiles.getPath() + worldname);
            File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");
            // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue


            // Suppression si doublon (généré sans autorisation), on remove le non tag
            if (file.exists() & file2.exists()) {
                IsoworldsLogger.severe(" --- Anomalie: Dossier isoworld et isoworld tag tous deux présents pour: " + worldname + " ---");
                ManageFiles.deleteDir(file);
            }

            if (file.exists()) {
                IsoworldsUtils.setStatus(worldname, 0, Msg.keys.SQL);
                // Si le dossier est en @PULL et qu'un joueur le demande alors on le passe en @PULL
                // Le script check ensutie
                return true;
            } else {
                IsoworldsLogger.warning("--- IMPORT MANUEL IW EN COURS POUR: " + worldname);
            }
            if (file2.exists()) {
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                IsoworldsUtils.cm("--- PULL MANUEL OK POUR: " + worldname);
                return false;
            }
            return false;
        }
        return true;
    }

    // COPY FOR CHARGERCOMMAND
    // Check if pPlayer's IsoWorld is created on database
    public static Boolean isPresentCharger(String uuid, String messageErreur, Boolean load) {
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
                // Chargement si load = true
                if (!IsoworldsUtils.getStatus(uuid + "-IsoWorld", Msg.keys.SQL)) {
                    if (load) {
                        setWorldPropertiesCharger(uuid + "-IsoWorld");
                        Sponge.getServer().loadWorld(uuid + "-IsoWorld");
                    }
                }
                return true;
            }

        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            return false;
        }
        return false;
    }

    // COPY FOR CHARGERCOMMANDE
    // Create world properties IsoWorlds
    public static WorldProperties setWorldPropertiesCharger(String worldname) {

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            if (wp.isPresent()) {
                worldProperties = wp.get();
                IsoworldsUtils.cm("WOLRD PROPERTIES: déjà présent");
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                IsoworldsUtils.cm("WOLRD PROPERTIES: non présents, création...");

            }

            // Global
            // Radius border 500
            int x = 500;
            worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            worldProperties.setKeepSpawnLoaded(false);
            worldProperties.setLoadOnStartup(false);
            worldProperties.setGenerateSpawnOnLoad(false);
            worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
            worldProperties.setPVPEnabled(true);
            worldProperties.setWorldBorderCenter(0, 0);
            worldProperties.setWorldBorderDiameter(x);

            // Sauvegarde
            Sponge.getServer().saveWorldProperties(worldProperties);
            IsoworldsUtils.cm("WorldProperties à jour");

        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            return null;
        }

        return worldProperties;
    }

    // ------------------------------------------------- LOCK AND CHECK SYSTEM

    // Vérifie si le lock est présent et renvoi vrai, sinon défini le lock et renvoi false
    public static Boolean isLocked(Player pPlayer, String className) {
        // Si le lock est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (checkLockFormat(pPlayer.getUniqueId().toString(), className)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
            return true;
        } else {
            // On set lock
            plugin.lock.put(pPlayer.getUniqueId().toString() + ";" + className, 1);
            return false;
        }
    }

    // Check if mirror iw
    //0 normal - 1 anomally
    public static int isMirrored(String worldname) {
        // Check if file exist, to detect mirrors
        File file = new File(ManageFiles.getPath() + "/" + worldname + "@PUSHED");
        File file2 = new File(ManageFiles.getPath() + "/" + worldname);
        // If exists and contains Isoworld
        if (file.exists() & file2.exists() & worldname.contains("-IsoWorld")) {
            return 1;
        }
        return 0;
    }

    // Cooldown modèle: uuid;commande
    public static Boolean checkLockFormat(String pPlayer, String command) {
        // Si le tableau est null alors lock 0 sinon lock 1
        if (plugin.lock.get(pPlayer + ";" + command) == null) {
            return false;
        } else {
            return true;
        }
    }


    // ------------------------------------------------- INFORMATION SYSTEM

    // TPS
    private static final DecimalFormat tpsFormat = new DecimalFormat("#0.00");

    // TPS
    public static Text getTPS(double currentTps) {
        TextColor colour;

        if (currentTps > 18) {
            colour = TextColors.GREEN;
        } else if (currentTps > 15) {
            colour = TextColors.YELLOW;
        } else {
            colour = TextColors.RED;
        }
        return Text.of(colour, tpsFormat.format(currentTps));
    }

    // Récupération user (off/one) depuis un uuid
    public static Optional<User> getPlayerFromUUID(UUID uuid) {
        Optional<User> user = null;
        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            user = userStorage.get(uuid);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return user;
        }
        return user;
    }

    // -------------------------------------------------  CHARGES SYSTEM

    // Get charge of a player
    public static Integer firstTime(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `UUID_P` = ?";
        ResultSet result;
        Integer number;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                IsoworldsUtils.cm(rselect.toString());
                IsoworldsUtils.cm("Debug charge 1");
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return null;
        }
        return null;
    }


    // Get charge of a player
    public static Integer getCharge(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `UUID_P` = ?";
        ResultSet result;
        Integer number;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                IsoworldsUtils.cm(rselect.toString());
                IsoworldsUtils.cm("Debug charge 1");
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return null;
        }
        initCharges(pPlayer, Msg.keys.SQL);
        return 0;
    }

    // Ajoute des charges à un joueur, succès = true
    public static Boolean updateCharge(Player pPlayer, Integer number, String messageErreur) {
        String CHECK = "UPDATE `players_info` SET `charges` = ? WHERE `UUID_P` = ?";
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // Number
            check.setInt(1, number);
            // UUID_P
            check.setString(2, pPlayer.getUniqueId().toString());
            // Requête
            IsoworldsUtils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
    }

    // Create trust for uuidcible on pPlayer IsoWorld
    public static Boolean initCharges(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `players_info` (`UUID_P`, `charges`) VALUES (?, ?)";
        Integer number;
        String Iuuid_p;

        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // Number
            number = 0;
            insert.setInt(2, number);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            return false;
        }
        return true;
    }

    // Vérifie les charges, retire si en possède sinon return false avec message
    public static Integer checkCharge(Player pPlayer, String messageErreur) {
        Integer charges = IsoworldsUtils.getCharge(pPlayer, Msg.keys.SQL);

        if (charges == null) {
            initCharges(pPlayer, Msg.keys.SQL);
            return -1;
        }
        // Permissions unlimited for player
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return -99;
        }
        if (charges == 0) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous ne possédez aucune charge !").color(TextColors.RED))).build()));
            return -1;
        } else {
            charges--;
            IsoworldsUtils.updateCharge(pPlayer, charges, Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Vous venez d'utiliser une charge, nouveau compte: ").color(TextColors.AQUA)))
                            .append(Text.of(Text.builder(charges + " charge(s)").color(TextColors.GREEN))).build()));
            return charges++;
        }
    }

}
