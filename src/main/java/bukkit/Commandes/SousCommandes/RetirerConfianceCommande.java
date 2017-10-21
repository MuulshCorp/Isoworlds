package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
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

    static IworldsBukkit plugin;

    public RetirerConfianceCommande(IworldsBukkit instance) {
        this.plugin = instance;
    }

    public static void RetirerConfiance(CommandSender sender, String[] args) {

        // SQL Variables
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;

        UUID uuidcible;
        Player pPlayer = (Player) sender;

        try {
            uuidcible = pPlayer.getUniqueId();
            if (uuidcible.toString().isEmpty()) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.");
                return;
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.");
            return;
        }

        try {
            IworldsUtils.cm("Suppression du trust.");
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = plugin.database.prepare(CHECK);

                // UUID _P
                check_p = pPlayer.getUniqueId().toString();
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

            if (uuidcible.toString().equals(pPlayer.getName())) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "REMOVE Sijania indique que vous ne pouvez vous retirer de votre iWorld.");
                return;
            }

            try {
                PreparedStatement insert = plugin.database.prepare(REMOVE);

                // UUID_P
                Iuuid_p = uuidcible.toString();
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
        for (Player player : colPlayers) {
            player.teleport(spawn);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania vient de vous retirer les droits d'accès de l'iWorld dans lequel vous vous trouviez.");
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "INSERT Sijania indique que le joueur n'a désormais plus accès à votre iWorld.");
        return;

    }
}
