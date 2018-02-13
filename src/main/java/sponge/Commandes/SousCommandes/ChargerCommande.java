package sponge.Commandes.SousCommandes;

import common.Msg;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Edwin on 13/02/2018.
 */
public class ChargerCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        if (!source.hasPermission("isoworlds.load")) {
            return CommandResult.success();
        }

        int length = 0;
        String[] arg = args.split(" ");
        length = arg.length;
        String uuid = arg[0];
        String worldname = uuid + "-IsoWorld";

        // Import / Export
        if (!IsoworldsUtils.checkTagCharger(worldname)) {
            return CommandResult.success();
        }

        // SELECT WORLD (load if need)
        if (!IsoworldsUtils.isPresentCharger(uuid, Msg.keys.SQL, true)) {
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