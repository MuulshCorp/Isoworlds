package bukkit.util;

import bukkit.MainBukkit;
import common.Msg;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 16/07/2018.
 */
public class Charge {

    private static final MainBukkit instance = MainBukkit.getInstance();

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
                Utils.cm(rselect.toString());
                Utils.cm("Debug charge 1");
                number = rselect.getInt(1);
                Utils.cm("number: " + number);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
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
            check.setString(2, pPlayer.getUniqueId().toString());
            // NUMBER
            check.setInt(1, number);
            // Requête
            Utils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
            return false;
        }
    }

    // Init charges and playtime on first connect
    public static Boolean initCharges(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `players_info` (`UUID_P`, `charges`, `playtimes`) VALUES (?, ?, ?)";
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
            // PlayTime
            insert.setInt(3, number);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.cm(Msg.keys.SQL);
            return false;
        }
        return true;
    }

    // Vérifie les charges, retire si en possède sinon return false avec message
    // -1 = false // true = charges // illimited = -99
    public static Integer checkCharge(Player pPlayer, String messageErreur) {
        Integer charges = getCharge(pPlayer, Msg.keys.SQL);
        Integer newCharges;

        if (charges == null) {
            initCharges(pPlayer, Msg.keys.SQL);
            return -1;
        }
        // Permissions unlimited for player
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return -99;
        }
        if (charges <= 0) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que vous ne possédez aucune charge !");
            return -1;
        }
        return charges;
    }
}
