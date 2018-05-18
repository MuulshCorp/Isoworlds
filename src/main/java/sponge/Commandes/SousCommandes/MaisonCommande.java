package sponge.Commandes.SousCommandes;

import common.Cooldown;
import common.Msg;
import org.spongepowered.api.Sponge;
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

import java.util.Optional;

import static sponge.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 10/10/2017.
 */
public class MaisonCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String cmdname = "maison";
        String worldname = "";
        Optional<World> world = Sponge.getServer().getWorld(worldname);
        Location<World> spawn;
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.MAISON)) {
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
        if (isLocked(pPlayer, "checkTag")) {
            return CommandResult.success();
        }

        // Import / Export
        if (!IsoworldsUtils.checkTag(pPlayer, worldname)) {
            return CommandResult.success();
        }

        // Supprime le lock
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");

        // SELECT WORLD (load if need)
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, true)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Construction du point de respawn

        // Téléportation du joueur
        if (IsoworldsLocations.teleport(pPlayer, worldname)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION + pPlayer.getName()).color(TextColors.AQUA))).build()));
        } else {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.").color(TextColors.AQUA))).build()));
        }

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.MAISON, Cooldown.MAISON_DELAY);


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