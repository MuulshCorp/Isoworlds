package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
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

    @SuppressWarnings("deprecation")
    public static void Maison(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();

        // Variables
        String worldname = "";
        Player pPlayer;
        String uuid;
        pPlayer = (Player) sender;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.MAISON)) {
            return;
        }

        worldname = (pPlayer.getUniqueId() + "-IsoWorld");

        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
        if (isLocked(pPlayer, "checkTag")) {
            return;
        }

        // Import / Export
        if (!IsoworldsUtils.checkTag(pPlayer, worldname)) {
            return;
        }

        // Supprime le lock
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");

        // SELECT WORLD
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, true)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
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
        }

        // Téléportation du joueur
        if (pPlayer.teleport(go)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
            instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
        }
        return;

    }
}
