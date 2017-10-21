package bukkit.Commandes;

import bukkit.Commandes.SousCommandes.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IworldsCommandes implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
            } else if (arg.equals("maison") || arg.equals("m") || arg.equals("home") || arg.equals("h")) {
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
            } else if (arg.equals("autoriser") || arg.equals("trust") || arg.equals("allow") || arg.equals("accept") || arg.equals("accepter") || arg.equals("confiance")) {
                ConfianceCommande.Confiance(sender, args);
                return true;
            } else if (arg.equals("retirer") || arg.equals("interdire") || arg.equals("untrust") || arg.equals("remove") ||arg.equals("disallow") || arg.equals("deny")) {
                RetirerConfianceCommande.RetirerConfiance(sender, args);
                return true;
            } else {
                // Invalid arg given
                sendHelp(sender);
                return true;
            }
        } else {
            sendHelp(sender);
            return true;
        }
    }

    public void sendHelp(CommandSender s) {
        if (s.hasPermission("iworlds.default"))
            s.sendMessage(ChatColor.GREEN + "/iw aide" + ChatColor.BLUE + " - ");

    }
}