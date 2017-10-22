package sponge.Commandes;

import sponge.IworldsSponge;

import sponge.Commandes.SousCommandes.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Edwin on 10/10/2017.
 */
public class IworldsCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        Player pPlayer = (Player) source;

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("/iw [creation][lister][maison][on][off][teleport]").color(TextColors.RED))).build()));
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande iWorlds, permet de créer/refondre/lister"))
                .permission("iworlds.default")
                .child(CreationCommande.getCommand(), "creation", "créer", "creer", "create", "c")
                .child(ListeCommande.getCommand(), "lister", "liste", "list", "l")
                .child(RefonteCommande.getCommand(), "refonte", "refondre", "r")
                .child(MaisonCommande.getCommand(), "maison", "m", "home", "h")
                .child(OffCommande.getCommand(), "off", "desactiver", "désactiver", "décharger", "decharger", "unload")
                .child(OnCommande.getCommand(), "on", "charger", "activer", "load")
                .child(new TeleportCommande(), "teleport", "tp")
                .child(new ConfianceCommande(), "autoriser", "trust", "allow", "accepter", "a", "confiance")
                .child(new RetirerConfianceCommande(), "retirer", "supprimer", "untrust", "remove")
                .executor(new IworldsCommande())
                .build();
    }

}
