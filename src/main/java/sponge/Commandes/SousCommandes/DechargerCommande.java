package sponge.Commandes.SousCommandes;

import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsLogger;
import sponge.Utils.IsoworldsUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by Edwin on 13/02/2018.
 */
public class DechargerCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        if (!source.hasPermission("isoworlds.unload")) {
            return CommandResult.success();
        }

        int length = 0;
        String[] arg = args.split(" ");
        length = arg.length;
        String uuid = arg[0];
        String worldname = uuid + "-IsoWorld";
        World world;

        // Getting world and check if is loaded
        if (Sponge.getServer().getWorld(worldname).get().isLoaded()) {
            world = Sponge.getServer().getWorld(worldname).get();
        } else {
            return CommandResult.success();
        }

        Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(), 0, 0, 0);
        Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);


        // Process to unload
        for (Player p : world.getPlayers()) {
            p.setLocationSafely(top);
            IsoworldsLogger.warning("- Déchargement manuel, téléporation au spawn de: " + p.getName());
        }

        // Save & unload
        try {
            world.save();
            Sponge.getServer().unloadWorld(world);
        } catch (Exception e) {
            e.printStackTrace();
            return CommandResult.success();
        }

        // Getting path file isoworld
        File check = new File(ManageFiles.getPath() + world.getName());

        // Set status to 1, push
        IsoworldsUtils.setStatus(world.getName(), 1, Msg.keys.SQL);

        // Suppression ID
        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat"));
        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat_old"));
        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/session.lock"));
        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/forcedchunks.dat"));

        // Tag du dossier en push
        ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
        IsoworldsLogger.warning("- Manuel " + world.getName() + " : PUSH avec succès");

        // Suppression du monde
        try {
            if (Sponge.getServer().deleteWorld(world.getProperties()).get()) {
                IsoworldsLogger.info("- Manuel " + world.getName() + " : Isoworld supprimé avec succès !");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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