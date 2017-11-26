package common;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Cooldown implements CooldownType {
    private static final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();


    /**
     * Sponge method
     */
    public static Timestamp getPlayerLastCooldown(Player pPlayer, String type) {
        String uuid_p = sponge.Utils.IsoworldsUtils.PlayerToUUID(pPlayer).toString();

        return getPlayerLastCooldown(uuid_p, type);
    }

    /**
     * Bukkit method
     */
    public static Timestamp getPlayerLastCooldown(org.bukkit.entity.Player pPlayer, String type) {
        String uuid_p = pPlayer.getUniqueId().toString();

        return getPlayerLastCooldown(uuid_p, type);
    }

    /**
     * Return all the occurences for a given player, type (ex: refonte) with date greater than now
     */
    private static Timestamp getPlayerLastCooldown(String uuid_p, String type) {
        String query = "SELECT * FROM `player_cooldown` WHERE `UUID_P` = ? AND `type` = ? AND `date` > ? AND `server_id` = ?";
        try {
            PreparedStatement check = instance.database.prepare(query);

            // UUID _P
            check.setString(1, uuid_p);

            //Type
            check.setString(2, type);

            //Date
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            check.setTimestamp(3, timestamp);

            // SERVEUR_ID
            check.setString(4, instance.servername);

            ResultSet resultSet = check.executeQuery();
            IsoworldsUtils.cm(check.toString());
            if (resultSet.next()) {
                return resultSet.getTimestamp("date");
            }
        } catch (Exception e) {
            e.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
        }

        return null;
    }


    /**
     * Sponge method
     */
    public static boolean addPlayerCooldown(Player pPlayer, String type, int delay) {
        String uuid_p = sponge.Utils.IsoworldsUtils.PlayerToUUID(pPlayer).toString();

        return addPlayerCooldown(uuid_p, type, delay);
    }

    /**
     * Bukkit method
     */
    public static boolean addPlayerCooldown(org.bukkit.entity.Player pPlayer, String type, int delay) {
        String uuid_p = pPlayer.getUniqueId().toString();

        return addPlayerCooldown(uuid_p, type, delay);
    }

    private static boolean addPlayerCooldown(String uuid_p, String type, int delay) {
        String query = "INSERT INTO `player_cooldown` (`UUID_P`, `date`, `type`, `server_id`) VALUES (?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() + (delay * 1000));
        try {
            PreparedStatement insert = instance.database.prepare(query);

            // UUID_P
            insert.setString(1, uuid_p);

            // Date
            insert.setString(2, (timestamp.toString()));

            // Type
            insert.setString(3, type);

            // SERVEUR_ID
            insert.setString(4, instance.servername);

            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);

            return false;
        }

        return true;
    }


    public static String getCooldownTimer(Timestamp timestamp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long cooldown = (timestamp.getTime() - now.getTime()) / 1000;
        IsoworldsUtils.cm(Long.toString(cooldown));
        String timer = "";
        int hours = (int) (cooldown / 3600);
        if (hours > 0) {
            timer += " " + hours + plurializeMessage(hours, " heure");
        }

        int remainder = (int) (cooldown - hours * 3600);
        int mins = remainder / 60;
        if (mins > 0) {
            timer += " " + mins + plurializeMessage(mins, " minute");
        }

        remainder = remainder - mins * 60;
        int secs = remainder;
        if (secs > 0) {
            timer += " " + secs + plurializeMessage(secs, " seconde");
        }

        return timer;
    }

    private static String plurializeMessage(int number, String message) {
        return number > 1 || number == 0 ? message + "s" : message;
    }
}
