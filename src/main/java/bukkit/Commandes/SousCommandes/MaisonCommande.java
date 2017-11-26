package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
public class MaisonCommande {

    public static IsoworldsBukkit instance;

    public static void Maison(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();

        // Variables
        String worldname = "";
        Player pPlayer;
        String uuid;
        pPlayer = (Player) sender;

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        // Check if home of trusted isoworld
        IsoworldsUtils.cm("DEBUG MAISON: " + args.length);
        // Si joueur précisé
        if (args.length == 2) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
            if (owner == null) {
                instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
            uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
            worldname = uuid + "-IsoWorld";
            IsoworldsUtils.cm("DEBUG OFF MAISON: " + worldname);
            // Si aucun joueur précisé
        } else {
            uuid = pPlayer.getUniqueId().toString();
            worldname = (pPlayer.getUniqueId() + "-IsoWorld");
            IsoworldsUtils.cm("DEBUG ON MAISON: " + worldname);
        }

        // Import / Export
        if (!IsoworldsUtils.ieWorld(pPlayer, worldname)) {
            // Suppression lock
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(uuid, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }


        // Construction du point de respawn
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Integer secours;
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        try {
            if (top == null) {
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, secours, 0);
            }
        } catch (NullPointerException npe) {
            //
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        }

        // Téléportation du joueur
        if (pPlayer.teleport(go)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
        }
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;

    }
}
