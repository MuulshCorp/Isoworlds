package sponge.Commandes.SousCommandes;

import common.Msg;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.*;

import javax.print.attribute.standard.MediaSize;
import java.util.NoSuchElementException;

/**
 * Created by Edwin on 10/10/2017.
 */
public class MaisonCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Location<World> spawn;
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Import / Export
        if (!IsoworldsUtils.ieWorld(pPlayer, worldname)) {
            return CommandResult.success();
        }

        // Construction du point de respawn
        try {
            spawn = plugin.getGame().getServer().getWorld(worldname).get().getSpawnLocation();
        } catch (NullPointerException | NoSuchElementException e) {
            e.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.DATA).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);
        // Téléportation du joueur
        if (pPlayer.setLocationSafely(top)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION + pPlayer.getName()).color(TextColors.AQUA))).build()));
        } else {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.").color(TextColors.AQUA))).build()));
        }

        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour retourner dans son iWorld"))
                .permission("isoworlds.maison")
                .executor(new MaisonCommande())
                .build();
    }
}