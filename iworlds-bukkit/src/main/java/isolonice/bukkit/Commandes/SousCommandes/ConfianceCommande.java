package isolonice.bukkit.Commandes.SousCommandes;

import isolonice.bukkit.IworldsBukkit;
import isolonice.bukkit.Utils.IworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ConfianceCommande {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String INSERT = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    static IworldsBukkit plugin;

    public ConfianceCommande(IworldsBukkit instance) {
        this.plugin = instance;
    }

    public static void Confiance(CommandSender sender, String[] args) {
        // SQL Variables
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UUID uuidcible;
        Player pPlayer = (Player) sender;

        try {
            uuidcible = Bukkit.getOfflinePlayer(pPlayer.getName()).getUniqueId();
            if (uuidcible.toString().isEmpty()) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.");
                return;
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.");
            return;
        }

        try {
            IworldsUtils.cm("Ajout d'un trust.");
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = plugin.database.prepare(CHECK);

                // UUID _P
                check_p = uuidcible.toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = (pPlayer.getUniqueId() + "-iWorld");
                check.setString(2, check_w);

                IworldsUtils.cm("CHECK REQUEST: " + check);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur existe déjà");
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que ce joueur est déjà autorisé à rejoindre votre iWorld.");
                    return;
                }

            } catch (Exception se) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.");
                return;
            }

            // SELECT WORLD
            try {
                PreparedStatement select = plugin.database.prepare(SELECT);

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

            // INSERT
            try {
                PreparedStatement insert = plugin.database.prepare(INSERT);

                // UUID_P
                Iuuid_p = uuidcible.toString();
                insert.setString(1, Iuuid_p);

                // UUID_W
                Iuuid_w = ((pPlayer.getUniqueId()) + "-iWorld");
                insert.setString(2, Iuuid_w);
                IworldsUtils.cm("INSERT REQUEST: " + insert);

                // Date
                insert.setString(3, (timestamp.toString()));

                insert.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "INSERT Sijania que le joueur a désormais accès à votre iWorld.");
        return;
    }
}
