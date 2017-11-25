package common;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Cooldown implements CooldownType {
    private static final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();

    public static boolean isPlayerCooldown(Player pPlayer, String type, int delay) {
        String query = "SELECT * FROM `player_cooldown` WHERE `UUID_P` = ? AND WHERE date > ? AND WHERE type = ?";

        try {
            PreparedStatement check = instance.database.prepare(query);

            // UUID _P
            String check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);

            //Date
            Timestamp timestamp = new Timestamp(System.currentTimeMillis() - delay);
            check.setTimestamp(2, timestamp);

            //Type
            check.setString(3, type);

            ResultSet resultSet = check.executeQuery();

            return !resultSet.isBeforeFirst();
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);

            return false;
        }
    }

    public static boolean addPlayerCooldown(Player pPlayer, String type) {
        String query = "INSERT INTO `player_cooldown` (`UUID_P`, `date`, `type`) VALUES (?, ?, ?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = instance.database.prepare(query);

            // UUID_P
            String Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);

            // Date
            insert.setString(2, (timestamp.toString()));

            // Type
            insert.setString(3, type);

            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);

            return false;
        }

        return true;

    }
}
