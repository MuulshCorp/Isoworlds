package sponge.Commandes.SousCommandes;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static sponge.IsoworldsSponge.instance;
import static sponge.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/11/2017.
 */

public class BiomeCommande implements CommandCallable {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {
        Player pPlayer = (Player) source;
        World world = pPlayer.getWorld();
        String[] arg = args.split(" ");
        int size = arg.length;
        BiomeType biome;

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        if (arg[0] == "plaines") {
            biome = BiomeTypes.PLAINS;
        } else if (arg[0] == "desert") {
            biome = BiomeTypes.DESERT;
        } else {
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }
        //Get chunk
        int ChunkX = pPlayer.getLocation().getBlockPosition().getX();
        int ChunkY = pPlayer.getLocation().getBlockPosition().getY();
        // Définition biome
        Optional<Chunk> chunk = world.getChunk(ChunkX, 0, ChunkY);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.get().setBiome(x, 0, z, biome);
            }
        }
        //pPlayer.sendMessage("[IsoWorlds]: " + "Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9)");
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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