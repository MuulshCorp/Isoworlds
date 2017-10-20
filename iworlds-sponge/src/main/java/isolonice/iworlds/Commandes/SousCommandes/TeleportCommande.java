package isolonice.iworlds.Commandes.SousCommandes;

/**
 * Created by Edwin on 14/10/2017.
 */

import isolonice.iworlds.IworldsSponge;
import isolonice.iworlds.Locations.IworldsLocations;
import isolonice.iworlds.Utils.IworldsUtils;

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

public class TeleportCommande implements CommandCallable {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        Player pPlayer = (Player) source;
        int lenght = 0;
        String[] arg = args.split(" ");
        for (int i = 0; i < args.length(); i++) {
            lenght = lenght + 1;
            IworldsUtils.cm("arg: " + lenght);
        }

        IworldsUtils.cm("Total arguments" + lenght);
        if (lenght < 1 || lenght < 2) {
            Text message = Text.of("Veuillez indiquer le joueur cible et le monde cible.");
            pPlayer.sendMessage(message);
            return CommandResult.success();
        }

        Optional<Player> target = Sponge.getServer().getPlayer(arg[0]);
        Player player;
        if (!target.isPresent()) {
            Text message = Text.of("Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            pPlayer.sendMessage(message);
            return CommandResult.success();
        } else {
            player = target.get().getPlayer().get();
            IworldsLocations.teleport(player, arg[1]);
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