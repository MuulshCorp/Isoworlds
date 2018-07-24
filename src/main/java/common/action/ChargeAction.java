package common.action;

import bukkit.Main;
import bukkit.util.message.Message;
import common.MainInterface;
import common.Manager;
import common.Msg;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChargeAction {

    private static final MainInterface instance = Manager.getInstance();


    // *** BUKKIT METHOD
    // Get charge of a player
    public static Integer getCharge(Player pPlayer) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `uuid_p` = ?";
        Integer number;
        // If unlimited
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return 1;
        }
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, pPlayer.getUniqueId().toString());
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        initCharges(pPlayer.getUniqueId().toString());
        return 0;
    }

    // *** SPONGE METHOD
    // Get charge of a player
    public static Integer getCharge(org.spongepowered.api.entity.living.player.Player pPlayer) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `uuid_p` = ?";
        Integer number;
        // If unlimited
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return 1;
        }
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, pPlayer.getUniqueId().toString());
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        initCharges(pPlayer.getUniqueId().toString());
        return 0;
    }

    // *** BUKKIT METHOD
    // Check charges, sub if owning else return false
    // -1 = false // true = charges // illimited = -99
    public static Integer checkCharge(Player pPlayer) {
        Integer charges = getCharge(pPlayer);
        if (charges == null) {
            initCharges(pPlayer.getUniqueId().toString());
            return -1;
        }
        // Permissions unlimited for player
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return -99;
        }
        if (charges <= 0) {
            pPlayer.sendMessage(Message.error(Msg.keys.CHARGE_EMPTY));
            return -1;
        }
        return charges;
    }

    // *** SPONGE METHOD
    // Check charges, sub if owning else return false
    // -1 = false // true = charges // illimited = -99
    public static Integer checkCharge(org.spongepowered.api.entity.living.player.Player pPlayer) {
        Integer charges = getCharge(pPlayer);
        if (charges == null) {
            initCharges(pPlayer.getUniqueId().toString());
            return -1;
        }
        // Permissions unlimited for player
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return -99;
        }
        if (charges <= 0) {
            pPlayer.sendMessage(sponge.util.message.Message.error(Msg.keys.CHARGE_EMPTY));
            return -1;
        }
        return charges;
    }

    // Add charge to player, success = true
    public static Boolean updateCharge(String playeruuid, Integer number) {
        String CHECK = "UPDATE `players_info` SET `charges` = ? WHERE `UUID_P` = ?";
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(2, playeruuid);
            // Amout
            check.setInt(1, number);
            // Request
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
    }

    // Init charges and playtime on first connect
    private static Boolean initCharges(String playeruuid) {
        String INSERT = "INSERT INTO `players_info` (`uuid_p`, `charges`, `playtimes`) VALUES (?, ?, ?)";
        Integer number;
        String Iuuid_p;
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            // Player uuid
            Iuuid_p = playeruuid;
            insert.setString(1, Iuuid_p);
            // Amout
            number = 0;
            insert.setInt(2, number);
            // PlayTime
            insert.setInt(3, number);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // If player's not in database
    public static Integer firstTime(String playeruuid) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `uuid_p` = ?";
        Integer number;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, playeruuid);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        return null;
    }
}