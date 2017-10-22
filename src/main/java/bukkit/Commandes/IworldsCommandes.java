package bukkit.Commandes;

import bukkit.Commandes.SousCommandes.*;
import bukkit.IworldsBukkit;
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
            } else if (arg.equals("autoriser") || arg.equals("trust") || arg.equals("allow") || arg.equals("accepter") || arg.equals("a") || arg.equals("confiance")) {
                ConfianceCommande.Confiance(sender, args);
                return true;
            } else if (arg.equals("retirer") || arg.equals("supprimer") || arg.equals("untrust") || arg.equals("remove")) {
                RetirerConfianceCommande.RetirerConfiance(sender, args);
                return true;
            } else {
                help(sender);
                return true;
            }
        } else {
            help(sender);
            return true;
        }
    }

    public void help(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "[iWorlds] Commandes disponibles");
        s.sendMessage(ChatColor.BLUE + "- Basique: /iw || /iworld || /iworlds");
        s.sendMessage(ChatColor.BLUE + "- Création: /iw [création][créer][creer][create][c]");
        s.sendMessage(ChatColor.BLUE + "- Refonte: /iw [refonte][refondre][r]");
        s.sendMessage(ChatColor.BLUE + "- Maison: /iw [maison][home][h][m]");
        s.sendMessage(ChatColor.BLUE + "- Désactivation: /iw [désactiver][off][décharger][unload]");
        s.sendMessage(ChatColor.BLUE + "- Activation: /iw [activer][charger][on][load]");
        s.sendMessage(ChatColor.BLUE + "- Confiance: /iw [confiance][trust][allow][accepter][a][autoriser] <nom>");
        s.sendMessage(ChatColor.BLUE + "- Retirer Confiance: /iw [retirer][untrust][supprimer][remove] <nom>");
    }
}