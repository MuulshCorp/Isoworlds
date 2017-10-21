package sponge.Commandes.SousCommandes;

import sponge.IworldsSponge;
import sponge.Utils.IworldsUtils;

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
import org.spongepowered.api.world.World;

import java.util.ArrayList;


/**
 * Created by Edwin on 15/10/2017.
 */
public class OffCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        Boolean check = false;
        ArrayList<World> worlds = new ArrayList<World>();
        IworldsUtils.cm("check");

        for (World world : Sponge.getServer().getWorlds()) {
            if (world.isLoaded()) {
                if (world.getName().contains("-iWorld")) {
                    worlds.add(world);
                    IworldsUtils.cm("worlds: " + world);
                }
            }
        }

        // loop iworlds
        for (World world : worlds) {
            // si iworld existe
            if (world.getName().equals(worldname.toString())) {
                check = true;
                IworldsUtils.cm("check");
                // si iworld chargé
                if (world.isLoaded()) {
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("Sijania vient de désactiver votre iWorld.").color(TextColors.AQUA))).build()));
                    Sponge.getServer().unloadWorld(world);
                    return CommandResult.success();
                    // si iworld déjà déchargé
                } else {
                    Sponge.getServer().loadWorld(worldname);
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: Sijania indique que votre iWorld est déjà déchargé, entrez /iw maison pour y entrer.").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
                    return CommandResult.success();
                }
            }

            // si iworld n'existe pas
            if (check == false) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania ne repère aucun iWorld à votre nom dans le Royaume Isolonice. Entrez /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        }
        IworldsUtils.cm("finished");
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour désactiver son iWorld."))
                .permission("iworlds.desactiver")
                .executor(new OffCommande())
                .build();
    }
}
