package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import common.Msg;
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
import java.util.*;

public class RetirerConfianceCommande implements CommandCallable {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String REMOVE = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // SQL Variables
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;

        UUID uuidcible;
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        int size = arg.length;

        // SELECT WORLD
        if (!IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> player = userStorage.get(arg[0]);
            uuidcible = player.get().getUniqueId();
            if (uuidcible.toString().isEmpty() || (size > 1)) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            i.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // CHECK AUTORISATIONS
        if (!IworldsUtils.trustExists(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_TRUST).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // DELETE AUTORISATION
        if (!IworldsUtils.deleteTrust(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        Collection<Player> colPlayers = Sponge.getServer().getWorld(IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld").get().getPlayers();
        Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
        Player player = Sponge.getServer().getPlayer(arg[0]).get();
        player.setLocation(spawn);
        player.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
            .append(Text.of(Text.builder(Msg.keys.KICK_TRUST).color(TextColors.AQUA))).build()));

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
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