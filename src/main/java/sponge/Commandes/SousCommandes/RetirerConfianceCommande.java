package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import common.Msg;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

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

import java.util.*;

public class RetirerConfianceCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        UUID uuidcible;
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        int size = arg.length;
        Optional<User> player;

        if (size > 1) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            player = userStorage.get(arg[0]);
            try {
                uuidcible = player.get().getUniqueId();
            } catch (NoSuchElementException e){
                e.printStackTrace();
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }

            if (uuidcible.toString().isEmpty() || (size > 1)) {
                IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.INVALIDE_JOUEUR);
                return CommandResult.success();
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            i.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // CHECK AUTORISATIONS
        if (!IsoworldsUtils.isTrusted(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_TRUST_2).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // DELETE AUTORISATION
        if (!IsoworldsUtils.deleteTrust(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            if (player.get().isOnline()) {
                if (player.get().getPlayer().get().getWorld().getName().equals(pPlayer.getUniqueId().toString() + "-IsoWorld")) {
                    Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
                    Player playerOnline = Sponge.getServer().getPlayer(arg[0]).get();
                    playerOnline.setLocation(spawn);
                    playerOnline.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.KICK_TRUST).color(TextColors.AQUA))).build()));
                }
            }
        } catch (NoSuchElementException nse) {
            nse.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.DATA).color(TextColors.AQUA))).build()));

        }

        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_RETIRER_CONFIANCE).color(TextColors.AQUA))).build()));
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