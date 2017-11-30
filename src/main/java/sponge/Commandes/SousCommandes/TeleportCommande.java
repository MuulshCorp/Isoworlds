package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import common.Msg;
import org.spongepowered.api.text.format.TextColors;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static sponge.Utils.IsoworldsUtils.isLocked;

public class TeleportCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        Player pPlayer = (Player) source;
        int length = 0;
        String[] arg = args.split(" ");
        length = arg.length;

        // Check if world exists
        if (!IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.RED))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Check if world exists
        if (!IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.RED))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Check if is world is loaded
        if (!(Sponge.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").get().isLoaded())) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre IsoWorld doit être chargé pour en changer le temps.").color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        IsoworldsUtils.cm("Total arguments" + length);
        IsoworldsUtils.cm("Total arguments2" + args);
        IsoworldsUtils.cm("Total arguments3" + arg);
        if (length != 2) {
            Text message = Text.of(Msg.keys.INVALIDE_JOUEUR);
            pPlayer.sendMessage(message);
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        Optional<Player> target = Sponge.getServer().getPlayer(arg[0]);
        Player player;
        if (!target.isPresent()) {
            Text message = Text.of("Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            pPlayer.sendMessage(message);
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        } else {
            player = target.get().getPlayer().get();
            IsoworldsLocations.teleport(player, arg[1]);
        }
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}