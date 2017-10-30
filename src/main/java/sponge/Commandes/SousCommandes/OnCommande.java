package sponge.Commandes.SousCommandes;

import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;

/**
 * Created by Edwin on 15/10/2017.
 */
public class OnCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        Boolean check = false;
        ArrayList<String> worlds = new ArrayList<String>();
        IsoworldsUtils.cm("check");

        for (WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
            if (world.getWorldName().contains("-IsoWorld")) {
                worlds.add(world.getWorldName());
                IsoworldsUtils.cm("worlds: " + world.getWorldName());
            }
        }

        // loop iworlds
        for (String world : worlds) {
            // si iworld existe
            if (world.equals(worldname)) {
                check = true;
                if (check == true) {
                    Sponge.getServer().loadWorld(worldname);
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("Sijania vient d'activer votre IsoWorld.").color(TextColors.AQUA))).build()));
                    return CommandResult.success();
                }
            }

            // si iworld n'existe pas
            if (check == false) {
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania ne repère aucun IsoWorld à votre nom dans le Royaume Isolonice. Entrez /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        }
        IsoworldsUtils.cm("finished");
        return CommandResult.success();
    }


    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour activer son IsoWorld."))
                .permission("isoworlds.activer")
                .executor(new OnCommande())
                .build();
    }
}