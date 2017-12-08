package bukkit.Utils;

import bukkit.IsoworldsBukkit;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IsoworldsUtils {

    private static final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();


    // ------------------------------------------------- USER MANIPULATION


    // ------------------------------------------------- MESSAGES

    // Console message
    public static void cm(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Commande console
    public static void cmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
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
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID_W
            check_w = world;
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, instance.servername);
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

    // Create IsoWorld for pPlayer
    public static Boolean setIsoWorld(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`, `STATUS`) VALUES (?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            // STATUS
            insert.setInt(5, 0);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
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
            PreparedStatement insert = instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = uuidcible.toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Set status of IsoWorld (1 for Pushed, 0 for Present)
    // It returns true if pushed, false si envoyé ou à envoyer
    public static Boolean setStatus(String world, Integer status, String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `STATUS` = ? WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // STATUS
            check.setInt(1, status);
            // UUID_W
            check_w = (world);
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            IsoworldsUtils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
        return false;
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

            // Global
            worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            worldProperties.setKeepSpawnLoaded(false);
            worldProperties.setLoadOnStartup(false);
            worldProperties.setGenerateSpawnOnLoad(false);
            worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
            worldProperties.setPVPEnabled(true);
            worldProperties.setWorldBorderCenter(0, 0);
            worldProperties.setWorldBorderDiameter(500);

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
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = instance.database.prepare(DELETE_IWORLDS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, instance.servername);

            // delete iworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Delete trust uuid of pPlayer's IsoWorld
    public static Boolean deleteTrust(Player pPlayer, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisation
            delete_autorisations.setString(1, Iuuid_p);
            delete_autorisations.setString(2, Iuuid_w);
            delete_autorisations.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
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
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check_p = uuidcible.toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId() + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.BLUE + messageErreur);
            return false;
        }
        return false;
    }

    // Check if iworld exists
    public static Boolean isPresent(String uuid, String messageErreur, Boolean load) {
        IsoworldsBukkit instance;
        instance = IsoworldsBukkit.getInstance();
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = uuid;
            check.setString(1, check_p);
            // UUID_W
            check_w = (uuid + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {

                return true;
            }

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                if (!IsoworldsUtils.getStatus(uuid + "-IsoWorld", Msg.keys.SQL)) {
                    if (load) {
                        setWorldProperties(IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld", pPlayer);
                        Bukkit.getServer().createWorld(new WorldCreator(uuid + "-IsoWorld"));
                    }
                }
                return true;
            }
        } catch (Exception se){
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            return false;
        }
        return false;
    }

    // Check tag of pPlayer IsoWorld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTag(Player pPlayer, String worldname) {
        // Vérification si monde en statut pushed
        if (IsoworldsUtils.getStatus(worldname, Msg.keys.SQL)) {
            IsoworldsUtils.cm("Debug 6");
            // Création des chemins pour vérification
            File file = new File(ManageFiles.getPath() + worldname);
            File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");
            // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue

            if (file.exists()) {
                IsoworldsUtils.cm("Debug 7");
                IsoworldsUtils.setStatus(worldname, 0, Msg.keys.SQL);
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de terminer son travail, l'IsoWorld est disponible !");
                // Si le dossier est en @PULL et qu'un joueur le demande alors on le passe en @PULL
                // Le script check ensutie
                return true;
            } else {
                // Lance la task import/export
                BukkitTask task = new IsoWorldsTasks(pPlayer, file).runTaskTimer(instance, 20, 20);
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


    // ------------------------------------------------- LOCK AND CHECK SYSTEM

    // Vérifie si le lock est présent et renvoi vrai, sinon défini le lock et renvoi false
    public static Boolean isLocked(Player pPlayer, String className) {
        // Si le lock est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (checkLockFormat(pPlayer.getUniqueId().toString(), String.class.getName())) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.");
            return true;
        } else {
            // On set lock
            instance.lock.put(pPlayer.getUniqueId().toString() + ";" + className, 1);
            return false;
        }
    }

    // Cooldown modèle: uuid;commande
    public static Boolean checkLockFormat(String pPlayer, String command) {
        // Si le tableau est null alors lock 0 sinon lock 1
        if (instance.lock.get(pPlayer + ";" + command) == null) {
            return false;
        } else {
            return true;
        }
    }

    // ------------------------------------------------- INFORMATION SYSTEM

























}
