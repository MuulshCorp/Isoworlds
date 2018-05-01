package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.Msg;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 01/05/2018.
 */
public class WarpCommande {

    public static IsoworldsBukkit instance;

    public static void Warp(CommandSender sender, String[] args) {
        // Variables
        instance = IsoworldsBukkit.getInstance();
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.WARP)) {
            return;
        }

        // Vérification taille args et retour si biome non indiqué
        if (len < 1) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vous requiert un warp.");
            return;
        }
        // On boucle sur les blocks du chunk du joueur et si le biome est défini on stop, sinon on regarde
        // si le biome indiqué existe et on l'applique
        for (String s : args) {
            IsoworldsUtils.cm(s);
        }
        if (args[1].equals("exploration") || args[1].equals("minage")) {
            IsoworldsLocations.teleport(pPlayer, args[1]);
        } else {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que ce warp n'existe pas.");
            return;
        }

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);
    }
}
