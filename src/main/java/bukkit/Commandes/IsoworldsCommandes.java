package bukkit.Commandes;

import bukkit.Commandes.SousCommandes.*;
import bukkit.Utils.IsoWorldsInventory;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
                return true;
            }
        } else {
            IsoWorldsInventory.getMenuPrincipal().open(pPlayer);
            //IsoworldsUtils.getHelp(sender);
            return true;
        }
    }
}