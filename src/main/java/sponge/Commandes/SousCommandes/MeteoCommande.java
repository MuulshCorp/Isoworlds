package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import common.Cooldown;
import common.Msg;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.weather.Weathers;
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

public class MeteoCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // SQL Variables
        Player pPlayer = (Player) source;
        String worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        String[] arg = args.split(" ");
        int size = arg.length;

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.METEO)) {
            return CommandResult.success();
        }

        // If got charges
        int charges = IsoworldsUtils.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return CommandResult.success();
        }

        // Check if world exists
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.RED))).build()));
            return CommandResult.success();
        }

        // Check if is world is loaded
        if (!Sponge.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre IsoWorld doit être chargé pour en changer la météo.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }


        if (size == 1) {
            pPlayer.sendMessage(Text.of(Text.builder("--------------------- [ ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("IsoWorlds ").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] ---------------------").color(TextColors.GOLD)))
                    .build()));

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            // Soleil
            Text meteo = Text.of(Text.builder("Sijania vous propose trois types de météo:").color(TextColors.AQUA).build());
            pPlayer.sendMessage(meteo);
            Text rain1 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Pluie").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Rain").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("10 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo pluie 1200 " + worldname)).build());
            pPlayer.sendMessage(rain1);
            Text rain2 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Pluie").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Rain").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("30 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo pluie 36000 " + worldname)).build());
            pPlayer.sendMessage(rain2);
            Text rain3 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Pluie").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Rain").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("1 Heure").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo pluie 72000 " + worldname)).build());
            pPlayer.sendMessage(rain3);
            Text rain = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Pluie").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Rain").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Eternel").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo pluie " + Integer.MAX_VALUE + " " + worldname)).build());
            pPlayer.sendMessage(rain);

            // rain
            Text sun1 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Soleil").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Sun").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("10 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo soleil 1200 " + worldname)).build());
            pPlayer.sendMessage(sun1);
            Text sun2 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Soleil").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Sun").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("30 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo soleil 36000 " + worldname)).build());
            pPlayer.sendMessage(sun2);
            Text sun3 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Soleil").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Sun").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("1 Heure").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo soleil 72000 " + worldname)).build());
            pPlayer.sendMessage(sun3);
            Text sun = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Soleil").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Sun").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Eternel").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo soleil " + Integer.MAX_VALUE + " " + worldname)).build());
            pPlayer.sendMessage(sun);

            // Storm
            Text storm1 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Déluge").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Storm").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("10 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo deluge 1200 " + worldname)).build());
            pPlayer.sendMessage(storm1);
            Text storm2 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Déluge").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Storm").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("30 Minutes").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo deluge 36000 " + worldname)).build());
            pPlayer.sendMessage(storm2);
            Text storm3 = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Déluge").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Storm").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("1 Heure").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo deluge 72000 " + worldname)).build());
            pPlayer.sendMessage(storm3);
            Text storm = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Déluge").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Storm").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] - [Durée: ").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Eternel").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                    .onClick(TextActions.runCommand("/iw meteo deluge " + Integer.MAX_VALUE + " " + worldname)).build());
            pPlayer.sendMessage(storm);

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            return CommandResult.success();

        } else if (size == 3) {
            if (arg[0].equals("soleil") || arg[0].equals("sun")) {
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.CLEAR, parseInt(arg[1]));
            } else if (arg[0].equals("pluie") || arg[0].equals("rain")) {
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.RAIN, parseInt(arg[1]));
            } else if (arg[0].equals("orage") || arg[0].equals("storm")) {
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.THUNDER_STORM, parseInt(arg[1]));
            }
            // Message pour tous les joueurs
            for (Player p : Sponge.getServer().getWorld(arg[2]).get().getPlayers()) {
                p.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que " + pPlayer.getName() + " vient de changer la météo à: " + arg[0] + " pendant: " + arg[1] + " ticks.")
                        .color(TextColors.GOLD).build()));
            }
        } else {
            return CommandResult.success();
        }

        IsoworldsUtils.updateCharge(pPlayer, charges - 1, Msg.keys.SQL);
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Vous venez d'utiliser une charge, nouveau compte: ").color(TextColors.RED)
                        .append(Text.of(Text.builder(charges - 1 + " charge(s)").color(TextColors.GREEN))))).build()));

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.METEO, Cooldown.METEO_DELAY);

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