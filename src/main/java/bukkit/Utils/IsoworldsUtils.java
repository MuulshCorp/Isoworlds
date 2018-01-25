package bukkit.Utils;

import bukkit.IsoworldsBukkit;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
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
    public static void cm(String message) {
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
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // SERVEUR_ID
            check.setString(2, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
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
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _W
            check_w = pPlayer.getUniqueId().toString() + "-IsoWorld";
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
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
    public static void setWorldProperties(String worldname, Player pPlayer) {

        // Properties of IsoWorld
        // Radius border 500
        int x;
        int y;
        if (pPlayer.hasPermission("isoworlds.size.500")) {
            x = 500;
            y = 500;
        // Radius border 750
        } else if (pPlayer.hasPermission("isoworlds.size.750")) {
            x = 750;
            y = 750;
        // Radius border 1000
        } else if (pPlayer.hasPermission("isoworlds.size.1000")) {
            x = 1000;
            y = 1000;
        // Radius border default
        } else {
            x = 250;
            y = 250;
        }
        //Bukkit.getServer().getWorld(worldname).setKeepSpawnInMemory(true);
        Bukkit.getServer().getWorld(worldname).setPVP(true);
        IsoworldsUtils.cmd("wb " + worldname + " set " + x + " " + y + " 0 0");
        Block yLoc = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
        Bukkit.getServer().getWorld(worldname).setSpawnLocation(0, yLoc.getY(), 0);
        Bukkit.getServer().getWorld(worldname).setGameRuleValue("MobGriefing", "false");

        // Spawn
        //Location<World> neutral = new Location<World>(Sponge.getServer().getWorld(worldname).get(), 0, 0, 0);
        //Location<World> firstspawn = IsoworldsLocations.getHighestLoc(neutral).orElse(null);
        //worldProperties.setSpawnPosition(firstspawn.getBlockPosition()    );

        // Sauvegarde a faire ?

        IsoworldsUtils.cm("WorldProperties à jour");

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
    public static Boolean deleteTrust(Player pPlayer, UUID uuid, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = uuid.toString();
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
            if (rselect.isBeforeFirst()) {
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
    public static Boolean isPresent(Player pPlayer, String messageErreur, Boolean load) {
        IsoworldsBukkit instance;
        instance = IsoworldsBukkit.getInstance();
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (check_p + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête

            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                if (!IsoworldsUtils.getStatus(check_p + "-IsoWorld", Msg.keys.SQL)) {
                    if (load) {
                        Bukkit.getServer().createWorld(new WorldCreator(check_p + "-IsoWorld"));
                        setWorldProperties(check_p + "-IsoWorld", pPlayer);
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

    // -------------------------------------------------  CHARGES SYSTEM

    // Get charge of a player
    public static Integer getCharge(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `UUID_P` = ?";
        ResultSet result;
        Integer number;
        // If unlimited
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return 1;
        }
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                IsoworldsUtils.cm(rselect.toString());
                IsoworldsUtils.cm("Debug charge 1");
                number = rselect.getInt(1);
                IsoworldsUtils.cm("number: " + number);
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
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID_P
            check.setString(1, pPlayer.getUniqueId().toString());
            // NUMBER
            check.setInt(2, number);
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
            PreparedStatement insert = instance.database.prepare(INSERT);
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
    // -1 = false // true = charges // illimited = -99
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
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que vous ne possédez aucune charge !");
            return -1;
        } else {
            return charges;
        }
    }

}
