package sponge.Commandes.SousCommandes;

import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
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
import org.spongepowered.api.world.World;

import java.util.ArrayList;

import static sponge.Utils.IsoworldsUtils.isLocked;


/**
 * Created by Edwin on 15/10/2017.
 */
public class OffCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        Boolean check = false;
        ArrayList<World> worlds = new ArrayList<World>();
        IsoworldsUtils.cm("check");

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        for (World world : Sponge.getServer().getWorlds()) {
            if (world.isLoaded()) {
                if (world.getName().contains("-IsoWorld")) {
                    worlds.add(world);
                }
            }
        }

        // loop iworlds
        for (World world : worlds) {
            // si iworld existe
            if (world.getName().equals(worldname.toString())) {
                check = true;
                IsoworldsUtils.cm("check");
                // si iworld chargé
                if (world.isLoaded()) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("Sijania vient de désactiver votre IsoWorld.").color(TextColors.AQUA))).build()));
                    Sponge.getServer().getWorld(worldname).get().getPlayers();
                    // Kick des joueurs présents dans le monde
                    for (Player p : Sponge.getServer().getWorld(worldname).get().getPlayers()) {
                        IsoworldsLocations.teleport(p, "Isolonice");
                    }
                    // Unload
                    Sponge.getServer().unloadWorld(world);
                    plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                    return CommandResult.success();
                    // si iworld déjà déchargé
                } else {
                    Sponge.getServer().loadWorld(worldname);
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que votre IsoWorld est déjà déchargé, entrez /iw maison pour y entrer.").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
                    plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                    return CommandResult.success();
                }
            }

            // si iworld n'existe pas
            if (check == false) {
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania ne repère aucun IsoWorld à votre nom dans le Royaume Isolonice. Entrez /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
                plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return CommandResult.success();
            }
        }
        IsoworldsUtils.cm("finished");
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour désactiver son IsoWorld."))
                .permission("isoworlds.desactiver")
                .executor(new OffCommande())
                .build();
    }
}
