package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoWorldsInventory;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.Array;

import static bukkit.IsoworldsBukkit.instance;
import static bukkit.Utils.IsoworldsUtils.isSetCooldown;

/**
 * Created by Edwin on 20/11/2017.
 */
public class BiomeCommande {

    public static IsoworldsBukkit instance;

    // BIOME
    public static IsoWorldsInventory menuBiome = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Biome"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
        @Override
        public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
            event.setWillClose(true);
        }
    }, instance)
            .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Plaines", "Un biome relativement plat avec des collines || vallonnées et une grande quantité de fleurs")
            .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.");


    public static void Biome(CommandSender sender, String[] args) {
        // Variables
        String worldname = "";
        Player pPlayer = (Player) sender;
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("check");
        Integer len = args.length;

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
        if (isSetCooldown(pPlayer, String.class.getName())) {
            return;
        }

        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Vérification taille args et retour si biome non indiqué
        if (len < 1) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vous requiert un nom de biome dans votre commande.");
            return;
        }
        // On boucle sur les blocks du chunk du joueur et si le biome est défini on stop, sinon on regarde
        // si le biome indiqué existe et on l'applique
        Chunk chunk = pPlayer.getLocation().getChunk();
        IsoworldsUtils.cm("Biomes" + Biome.values().toString());
        for (Biome b : Biome.values()) {
            if (b.name().contains(args[1])) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (chunk.getBlock(x, 0, z).getBiome() != b) {
                            final Block block = chunk.getBlock(x, 0, z);
                            block.setBiome(b);
                        }
                    }
                }
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9)");
                instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que ce biome n'existe pas.");
        instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}