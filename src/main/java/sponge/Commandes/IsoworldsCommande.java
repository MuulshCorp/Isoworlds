package sponge.Commandes;

import sponge.IsoworldsSponge;

import sponge.Commandes.SousCommandes.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import sponge.Utils.IsoWorldsInventory;
import sponge.Utils.IsoworldsUtils;

/**
 * Created by Edwin on 10/10/2017.
 */
public class IsoworldsCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        Player pPlayer = (Player) source;

        IsoworldsUtils.getHelp(pPlayer);
        // Ouverture du menu principal
        pPlayer.openInventory(IsoWorldsInventory.menuPrincipal(pPlayer));
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande IsoWorlds, permet de créer/refondre/lister"))
                .permission("isoworlds.default")
                .child(CreationCommande.getCommand(), "creation", "créer", "creer", "create", "c")
                .child(ListeCommande.getCommand(), "lister", "liste", "list", "l")
                .child(RefonteCommande.getCommand(), "refonte", "refondre", "r")
                .child(MaisonCommande.getCommand(), "maison", "home", "h")
                .child(OffCommande.getCommand(), "off", "desactiver", "désactiver", "décharger", "decharger", "unload")
                .child(OnCommande.getCommand(), "on", "charger", "activer", "load")
                .child(new BiomeCommande(), "biome", "b")
                .child(new TeleportCommande(), "teleport", "tp")
                .child(new ConfianceCommande(), "confiance", "trust", "a")
                .child(new RetirerConfianceCommande(), "retirer", "supprimer", "untrust", "remove")
                .child(new MeteoCommande(), "meteo", "weather", "m", "météo")
                .child(new TimeCommande(), "time", "temps", "t", "cycle")
                .executor(new IsoworldsCommande())
                .build();
    }

}
