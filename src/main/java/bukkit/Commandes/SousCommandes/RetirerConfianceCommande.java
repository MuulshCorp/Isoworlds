package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class RetirerConfianceCommande {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String REMOVE = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    public static IworldsBukkit instance;

    public static void RetirerConfiance(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        // SQL Variables
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;

        UUID uuidcible;
        Player pPlayer = (Player) sender;
        Player cible = Bukkit.getServer().getPlayer(args[0]);

        if (!cible.isOnline()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que le joueur doit être en ligne pour l'autoriser sur votre iWorld.");
            return;
        }

        try {
            IworldsUtils.cm("Suppression du trust.");
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = instance.database.prepare(CHECK);

                // UUID _P
                check_p = cible.getUniqueId().toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
                check.setString(2, check_w);

                IworldsUtils.cm("CHECK REQUEST: " + check);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur n'est pas présent dans le claim");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que ce joueur n'est pas autorisé à rejoindre votre iWorld.");
                    return;
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
                return;
            }

            // REMOVE

            if (cible.getName() == pPlayer.getName()) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "REMOVE Sijania indique que vous ne pouvez vous retirer de votre iWorld.");
                return;
            }

            // SELECT WORLD
            try {
                PreparedStatement select = instance.database.prepare(SELECT);

                // UUID_P
                Suuid_p = pPlayer.getUniqueId().toString();
                select.setString(1, Suuid_p);
                // UUID_W
                Suuid_w = (pPlayer.getUniqueId() + "-iWorld");
                select.setString(2, Suuid_w);

                IworldsUtils.cm("SELECT REQUEST: " + select);
                // Requête
                ResultSet rselect = select.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("SELECT: Vide, l'iWorld n'existe pas");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "SELECT Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
                    return;
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "SELECT Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
                return;
            }

            try {
                PreparedStatement insert = instance.database.prepare(REMOVE);

                // UUID_P
                Iuuid_p = cible.getUniqueId().toString();
                insert.setString(1, Iuuid_p);

                // UUID_W
                Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
                insert.setString(2, Iuuid_w);
                IworldsUtils.cm("REMOVE REQUEST: " + insert);

                insert.executeUpdate();
            } catch (Exception ex) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "REMOVE Sijania indique que ce joueur n'est autorisé à rejoindre votre iWorld.");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }


        Collection<Player> colPlayers = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-iWorld").getPlayers();
        Location spawn = Bukkit.getServer().getWorld("Isolonice").getSpawnLocation();
        Player player = Bukkit.getServer().getPlayer(args[0]);
        player.teleport(spawn);
        player.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania vient de vous retirer les droits d'accès de l'iWorld dans lequel vous vous trouviez.");

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "INSERT Sijania indique que le joueur n'a désormais plus accès à votre iWorld.");
        return;

    }
}
