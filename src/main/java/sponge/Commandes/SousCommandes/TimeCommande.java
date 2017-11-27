package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 09/11/2017.
 */

import common.Msg;
import org.spongepowered.api.text.action.TextActions;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static sponge.Utils.IsoworldsUtils.isLocked;

public class TimeCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // SQL Variables
        Player pPlayer = (Player) source;
        String worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        String[] arg = args.split(" ");
        int size = arg.length;

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
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
        if (!Sponge.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").get().isLoaded()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre IsoWorld doit être chargé pour en changer le temps.").color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        if (size == 1) {
            pPlayer.sendMessage(Text.of(Text.builder("--------------------- [ ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("IsoWorlds ").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] ---------------------").color(TextColors.GOLD)))
                    .build()));

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            // Soleil
            Text meteo = Text.of(Text.builder("Sijania vous propose deux temps:").color(TextColors.AQUA).build());
            pPlayer.sendMessage(meteo);

            // Jour
            Text day = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Jour").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Day").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]")))
                    .onClick(TextActions.runCommand("/iw time jour " + worldname)).build());
            pPlayer.sendMessage(day);

            // Nuit
            Text night = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Nuit").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Night").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]")))
                    .onClick(TextActions.runCommand("/iw time nuit " + worldname)).build());
            pPlayer.sendMessage(night);

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();

        } else if (size == 2) {
            if (arg[0].equals("jour") || arg[0].equals("day")) {
                Sponge.getServer().getWorld(arg[1]).get().getProperties().setWorldTime(0);
            } else if (arg[0].equals("nuit") || arg[0].equals("night")) {
                Sponge.getServer().getWorld(arg[1]).get().getProperties().setWorldTime(12000);
            }
        } else {
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
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