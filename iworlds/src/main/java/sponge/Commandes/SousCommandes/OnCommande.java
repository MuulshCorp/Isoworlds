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
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;

/**
 * Created by Edwin on 15/10/2017.
 */
public class OnCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        Boolean check = false;
        ArrayList<String> worlds = new ArrayList<String>();
        IworldsUtils.cm("check");

        for (WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
            IworldsUtils.cm("world: " + world);
            if (world.getWorldName().contains("-iWorld")) {
                worlds.add(world.getWorldName());
                IworldsUtils.cm("worlds: " + world.getWorldName());
            }
        }

        // loop iworlds
        for (String world : worlds) {
            // si iworld existe
            if (world.equals(worldname)) {
                check = true;
                if (check == true) {
                    Sponge.getServer().loadWorld(worldname);
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("Sijania vient d'activer votre iWorld.").color(TextColors.AQUA))).build()));
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
                .description(Text.of("Commande pour activer son iWorld."))
                .permission("iworlds.activer")
                .executor(new OnCommande())
                .build();
    }
}