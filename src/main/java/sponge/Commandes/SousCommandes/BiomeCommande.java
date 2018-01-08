package sponge.Commandes.SousCommandes;

import common.Cooldown;
import common.Msg;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
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

        IsoworldsUtils.cm(arg[0]);

        // If got charges
        if (IsoworldsUtils.getCharge(pPlayer, Msg.keys.SQL) == 0){
            return CommandResult.success();
        }

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Check if is in isoworld
        if (!world.getName().equals(pPlayer.getUniqueId() + "-IsoWorld")) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous devez être présent dans votre monde pour changer de biome.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        if (arg[0].equals("plaines")) {
            biome = BiomeTypes.PLAINS;
        } else if (arg[0].equals("desert")) {
            biome = BiomeTypes.DESERT;
        } else if (arg[0].equals("marais")) {
            biome = BiomeTypes.SWAMPLAND;
        } else if (arg[0].equals("océan")) {
            biome = BiomeTypes.OCEAN;
        } else if (arg[0].equals("champignon")) {
            biome = BiomeTypes.MUSHROOM_ISLAND;
        } else if (arg[0].equals("jungle")) {
            biome = BiomeTypes.JUNGLE;
        } else if (arg[0].equals("enfer")) {
            biome = BiomeTypes.HELL;
        } else if (arg[0].equals("end")) {
            biome = BiomeTypes.VOID;
        } else {
            return CommandResult.success();
        }
        // Définition biome
        Location loc = pPlayer.getLocation();
        IsoworldsUtils.cm("LOCATION " + loc);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                loc.getExtent().setBiome(
                        loc.getChunkPosition().getX() * 16 + x,
                        0,
                        loc.getChunkPosition().getY() * 16 + z,
                        biome
                );
            }
        }
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9 ou F3 + G)").color(TextColors.AQUA))).build()));

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);

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