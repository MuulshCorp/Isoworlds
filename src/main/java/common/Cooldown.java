package common;

import org.spongepowered.api.entity.living.player.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Cooldown implements CooldownType {
    private Mysql database;
    private String servername;

    public Cooldown(Mysql database, String servername) {
        this.database = database;
        this.servername = servername;
    }

    /**
     * Sponge method
     */
    public Timestamp getPlayerLastCooldown(Player pPlayer, String type) {
        String uuid_p = sponge.Utils.IsoworldsUtils.PlayerToUUID(pPlayer).toString();

        return getPlayerLastCooldown(uuid_p, type);
    }

    /**
     * Bukkit method
     */
    public Timestamp getPlayerLastCooldown(org.bukkit.entity.Player pPlayer, String type) {
        String uuid_p = pPlayer.getUniqueId().toString();

        return getPlayerLastCooldown(uuid_p, type);
    }

    /**
     * Return all the occurences for a given player, type (ex: refonte) with date greater than now
     */
    private Timestamp getPlayerLastCooldown(String uuid_p, String type) {
        String query = "SELECT * FROM `player_cooldown` WHERE `UUID_P` = ? AND `type` = ? AND `date` > ? AND `server_id` = ?";
        try {
            PreparedStatement check = this.database.prepare(query);

            // UUID _P
            check.setString(1, uuid_p);

            //Type
            check.setString(2, type);

            //Date
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            check.setTimestamp(3, timestamp);

            // SERVEUR_ID
            check.setString(4, this.servername);

            ResultSet resultSet = check.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("date");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Sponge method
     */
    public boolean addPlayerCooldown(Player pPlayer, String type, int delay) {
        String uuid_p = sponge.Utils.IsoworldsUtils.PlayerToUUID(pPlayer).toString();

        return addPlayerCooldown(uuid_p, type, delay);
    }

    /**
     * Bukkit method
     */
    public boolean addPlayerCooldown(org.bukkit.entity.Player pPlayer, String type, int delay) {
        String uuid_p = pPlayer.getUniqueId().toString();

        return addPlayerCooldown(uuid_p, type, delay);
    }

    private boolean addPlayerCooldown(String uuid_p, String type, int delay) {
        String query = "INSERT INTO `player_cooldown` (`UUID_P`, `date`, `type`, `server_id`) VALUES (?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() + (delay * 1000));
        try {
            PreparedStatement insert = this.database.prepare(query);

            // UUID_P
            insert.setString(1, uuid_p);

            // Date
            insert.setString(2, (timestamp.toString()));

            // Type
            insert.setString(3, type);

            // SERVEUR_ID
            insert.setString(4, this.servername);

            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }


    public String getCooldownTimer(Timestamp timestamp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long cooldown = (timestamp.getTime() - now.getTime()) / 1000;
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

    private String plurializeMessage(int number, String message) {
        return number > 1 || number == 0 ? message + "s" : message;
    }
}
