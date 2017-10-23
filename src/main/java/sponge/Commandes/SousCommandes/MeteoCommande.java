package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.weather.Weathers;
import sponge.IworldsSponge;
import sponge.Utils.IworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.Integer.parseInt;
import static sponge.Utils.IworldsUtils.iworldExists;

public class MeteoCommande implements CommandCallable {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // SQL Variables
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Player pPlayer = (Player) source;
        String worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        String[] arg = args.split(" ");
        int size = arg.length;

        if (iworldExists(pPlayer) == false) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous ne possédez aucun iWorld.").color(TextColors.RED))).build()));
            return CommandResult.success();
        }

        if (size < 1) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("METEO Sijania indique que vous devez entrer un temps [soleil][pluie]votre iWorld est déjà créé.").color(TextColors.AQUA))).build()));
            Text meteo = Text.of(Text.builder("[iWorlds] Choisir votre météo").color(TextColors.GREEN).build());
            pPlayer.sendMessage(meteo);
            Text sun1 = Text.of(Text.builder("- [Temps: Pluie/Rain] - [Durée: 10 minutes]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 10 " + worldname)).build());
            pPlayer.sendMessage(sun1);
            Text sun2 = Text.of(Text.builder("- [Temps: Pluie/Rain] - [Durée: 30 minutes]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 30 " + worldname)).build());
            pPlayer.sendMessage(sun2);
            Text sun3 = Text.of(Text.builder("- [Temps: Pluie/Rain] - [Durée: 1 heure]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 60 " + worldname)).build());
            pPlayer.sendMessage(sun3);
            Text sun = Text.of(Text.builder("- [Temps: Pluie/Rain] - [Durée: Eternel]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil " + Integer.MAX_VALUE + " " + worldname)).build());
            pPlayer.sendMessage(sun);
            Text rain1 = Text.of(Text.builder("- [Temps: Soleil/Sun] - [Durée: 10 minutes]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 10 " + worldname)).build());
            pPlayer.sendMessage(rain1);
            Text rain2 = Text.of(Text.builder("- [Temps: Soleil/Sun] - [Durée: 30 minutes]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 30 " + worldname)).build());
            pPlayer.sendMessage(rain2);
            Text rain3 = Text.of(Text.builder("- [Temps: Soleil/Sun] - [Durée: 60 minutes]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil 60 " + worldname)).build());
            pPlayer.sendMessage(rain3);
            Text rain = Text.of(Text.builder("- [Temps: Soleil/Sun] - [Durée: Eternel]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/iw meteo soleil" + worldname)).build());
            pPlayer.sendMessage(rain);
            return CommandResult.success();
        } else if (size == 3) {
            if (arg[0].equals("soleil") || arg[0].equals("sun")) {
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.CLEAR, parseInt(arg[1]));
            } else if (arg[0].equals("pluie") || arg[0].equals("rain")){
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.CLEAR, parseInt(arg[1]));
            } else if (arg[0].equals("eternel") || arg[0].equals("eternal")) {
                Sponge.getServer().getWorld(arg[2]).get().setWeather(Weathers.CLEAR, parseInt(arg[1]));
            }
        } else {
            return CommandResult.success();
        }
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