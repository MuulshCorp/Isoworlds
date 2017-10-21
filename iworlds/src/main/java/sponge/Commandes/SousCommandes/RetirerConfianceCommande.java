package sponge.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

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
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;

        UUID uuidcible;
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        int size = arg.length;

        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> player = userStorage.get(arg[0]);
            uuidcible = player.get().getUniqueId();
            if (uuidcible.toString().isEmpty() || (size > 1)) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous devez fournir un nom de joueur valide. /iw confiance nomjoueur.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            IworldsUtils.cm("Suppression du trust.");
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = plugin.database.prepare(this.CHECK);

                // UUID _P
                check_p = IworldsUtils.PlayerToUUID(pPlayer).toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
                check.setString(2, check_w);

                IworldsUtils.cm("CHECK REQUEST: " + check);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    IworldsUtils.cm("CHECK: Le joueur n'est pas présent dans le claim");
                    pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("CHECK Sijania indique que ce joueur n'est pas autorisé à rejoindre votre iWorld.").color(TextColors.AQUA))).build()));
                    return CommandResult.success();
                }

            } catch (Exception se) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("CHECK Sijania indique que votre iWorld ne semble pas exister, /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }

            // REMOVE

            if (uuidcible.toString() == pPlayer.getName()) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("REMOVE Sijania indique que vous ne pouvez vous retirer de votre iWorld.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }

            try {
                PreparedStatement insert = plugin.database.prepare(this.REMOVE);

                // UUID_P
                Iuuid_p = uuidcible.toString();
                insert.setString(1, Iuuid_p);

                // UUID_W
                Iuuid_w = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
                insert.setString(2, Iuuid_w);
                IworldsUtils.cm("REMOVE REQUEST: " + insert);

                insert.executeUpdate();
            } catch (Exception ex) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("REMOVE Sijania indique que ce joueur n'est autorisé à rejoindre votre iWorld.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return CommandResult.success();
        }


        Collection<Player> colPlayers = Sponge.getServer().getWorld(IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld").get().getPlayers();
        Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
        for (Player player : colPlayers) {
            player.setLocation(spawn);
            player.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania vient de vous retirer les droits d'accès de l'iWorld dans lequel vous vous trouviez.").color(TextColors.AQUA))).build()));
        }

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("INSERT Sijania indique que le joueur n'a désormais plus accès à votre iWorld.").color(TextColors.AQUA))).build()));
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