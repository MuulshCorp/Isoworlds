package bukkit.Commandes;

import bukkit.Commandes.SousCommandes.*;
import bukkit.Utils.IsoWorldsInventory;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static bukkit.IsoworldsBukkit.instance;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IsoworldsCommandes implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player pPlayer = (Player) sender;

        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("creation") || arg.equals("créer") || arg.equals("creer") || arg.equals("create") || arg.equals("c")) {
                CreationCommande.Creation(sender, args);
                return true;
            } else if (arg.equals("lister") || arg.equals("liste") || arg.equals("list") || arg.equals("l")) {
                ListeCommande.Liste(sender, args);
                return true;
            } else if (arg.equals("refonte") || arg.equals("refondre") || arg.equals("r")) {
                RefonteCommande.Refonte(sender, args);
                return true;
            } else if (arg.equals("maison") || arg.equals("home") || arg.equals("h")) {
                MaisonCommande.Maison(sender, args);
                return true;
            } else if (arg.equals("off") || arg.equals("desactiver") || arg.equals("désactiver") || arg.equals("décharger") || arg.equals("unload")) {
                OffCommande.Off(sender, args);
                return true;
            } else if (arg.equals("on") || arg.equals("charger") || arg.equals("activer") || arg.equals("load")) {
                OnCommande.On(sender, args);
                return true;
            } else if (arg.equals("teleport") || arg.equals("tp") || arg.equals("teleportation")) {
                TeleportCommande.Teleport(sender, args);
                return true;
            } else if (arg.equals("confiance") || arg.equals("trust") || arg.equals("a")) {
                ConfianceCommande.Confiance(sender, args);
                return true;
            } else if (arg.equals("retirer") || arg.equals("supprimer") || arg.equals("untrust") || arg.equals("remove")) {
                RetirerConfianceCommande.RetirerConfiance(sender, args);
                return true;
            } else if (arg.equals("météo") || arg.equals("meteo") || arg.equals("m")) {
                MeteoCommande.Meteo(sender, args);
                return true;
            } else if (arg.equals("time") || arg.equals("temps") || arg.equals("t") || arg.equals("cycle")) {
                TimeCommande.Time(sender, args);
                return true;
            } else if (arg.equals("biome") || arg.equals("biomes") || arg.equals("b")) {
                BiomeCommande.Biome(sender, args);
                return true;
            } else {
                IsoworldsUtils.getHelp(sender);
                return true;
            }
        } else {

            //MENU PRINCIPAL
            IsoWorldsInventory menuPrincipal = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
                @Override
                public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                    event.setWillClose(true);
                }
            }, instance)
                    .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks")
                    .setOption(1, new ItemStack(Material.EMERALD, 1), ChatColor.GREEN + "Confiance", "Gérez qui peut avoir accès à votre IsoWorld")
                    .setOption(2, new ItemStack(Material.BRICK, 1), ChatColor.GRAY + "Construction", "Créez ou refondez votre IsoWorld")
                    .setOption(3, new ItemStack(Material.BED, 1), ChatColor.BLUE + "Maison", "Rendez-vous sur votre IsoWorld")
                    .setOption(4, new ItemStack(Material.DOUBLE_PLANT, 1), ChatColor.YELLOW + "Météo", "Gérez la pluie et le beau temps de votre IsoWorld")
                    .setOption(5, new ItemStack(Material.LEVER, 1), ChatColor.RED + "Activation", "Chargez-Déchargez votre IsoWorld")
                    .setOption(6, new ItemStack(Material.DIAMOND_BOOTS, 1), ChatColor.LIGHT_PURPLE + "Téléportation", "Téléportez vous sur un IsoWorld [STAFF]");
            menuPrincipal.open(pPlayer);
            //IsoworldsUtils.getHelp(sender);
            return true;
        }
    }
}