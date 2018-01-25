package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.Msg;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/11/2017.
 */

public class BiomeCommande {

    public static IsoworldsBukkit instance;

    public static void Biome(CommandSender sender, String[] args) {
        // Variables
        instance = IsoworldsBukkit.getInstance();
        String worldname = "";
        Player pPlayer = (Player) sender;
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("check");
        Integer len = args.length;
        Biome biome;

        // If got charges
        int charges = IsoworldsUtils.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return;
        }

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return;
        }

        // SELECT WORLD
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        // Vérification taille args et retour si biome non indiqué
        if (len < 1) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vous requiert un nom de biome dans votre commande.");
            return;
        }
        // On boucle sur les blocks du chunk du joueur et si le biome est défini on stop, sinon on regarde
        // si le biome indiqué existe et on l'applique
        for (String s : args) {
            IsoworldsUtils.cm(s);
        }
        if (args[1].equals("plaines")) {
            biome = Biome.PLAINS;
        } else if (args[1].equals("desert")) {
            biome = Biome.DESERT;
        } else if (args[1].equals("marais")) {
            biome = Biome.SWAMPLAND;
        } else if (args[1].equals("océan")) {
            biome = Biome.OCEAN;
        } else if (args[1].equals("champignon")) {
            biome = Biome.MUSHROOM_ISLAND;
        } else if (args[1].equals("jungle")) {
            biome = Biome.JUNGLE;
        } else if (args[1].equals("enfer")) {
            biome = Biome.HELL;
            // Biome VOID inexistant 1.7.10
        } else {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que ce biome n'existe pas.");
            return;
        }

        Chunk chunk = pPlayer.getLocation().getChunk();
        IsoworldsUtils.cm("Biomes" + Biome.values().toString());
        IsoworldsUtils.cm("COORDINATES: X: " + chunk.getX() + " Z: " + chunk.getZ());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final Block block = chunk.getBlock(x, 0, z);
                block.setBiome(biome);
            }
        }

        // Update charges if not unlimited & positive
        if (charges > 0) {
            IsoworldsUtils.updateCharge(pPlayer, -1, Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Vous venez d'utiliser une charge, nouveau compte: " + ChatColor.GREEN + charges + " charge(s)");
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9)");

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);
    }
}