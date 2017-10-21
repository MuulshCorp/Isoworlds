package sponge.Commandes.SousCommandes;

import sponge.IworldsSponge;
import sponge.Locations.IworldsLocations;
import sponge.Utils.IworldsUtils;
import common.ManageFiles;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Created by Edwin on 05/10/2017.
 */

public class CreationCommande implements CommandExecutor {
    static final String INSERT = "INSERT INTO `iworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String INSERT_TRUST = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;
        final String check_w;
        final String check_p;
        final String Iuuid_p;
        final String Iuuid_w;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

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
            if (rselect.isBeforeFirst() ) {
                IworldsUtils.cm("CHECK: Le joueur existe déjà");
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("CHECK Sijania indique que votre iWorld est déjà créé.").color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        } catch (Exception se){
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("CHECK Sijania indique que votre iWorld est déjà créé.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Sijania entame la construction de votre iWorld...").color(TextColors.AQUA))).build()));
        fullpath = (ManageFiles.getPath() + IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        IworldsUtils.cm("fullpath: " + fullpath);
        IworldsUtils.cm("worldname: " + worldname);

        // Check si le monde existe déjà
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            throw new CommandException(Text.of(TextColors.DARK_GRAY, worldname, " already exists"), false);
        }

        File sourceFile = new File(ManageFiles.getPath() + "PATERN");
        File destFile = new File(fullpath);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne parvient pas à créer votre iWorld, veuillez contacter un membre de l'équipe.").color(TextColors.RED))).build()));
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Dans le cas ou vous possédez déjà un iWorld et que vous souhaitez le réinitialiser: /iw refonte.").color(TextColors.GREEN))).build()));
            return CommandResult.success();
        }

        try {
            //WorldArchetype worldArchetype = WorldArchetype.builder().dimension(DimensionTypes.OVERWORLD).reset().build(worldname, worldname);
            WorldProperties worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            Sponge.getServer().getWorldProperties(worldname).get().setLoadOnStartup(false);
            Sponge.getServer().getWorldProperties(worldname).get().getDimensionType().getId();
            Sponge.getServer().saveWorldProperties(worldProperties);
            Sponge.getGame().getServer().loadWorld(worldname);

        } catch (IOException ie) {
            ie.printStackTrace();
        }

        // INSERT
        try {
            PreparedStatement insert = plugin.database.prepare(this.INSERT);
            PreparedStatement insert_trust = plugin.database.prepare(this.INSERT_TRUST);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            insert_trust.setString(1, Iuuid_p);

            // UUID_W
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            insert.setString(2, Iuuid_w);
            insert_trust.setString(2, Iuuid_w);
            IworldsUtils.cm("INSERT REQUEST: " + insert);

            // Date
            insert.setString(3, (timestamp.toString()));
            insert_trust.setString(3, (timestamp.toString()));

            insert.executeUpdate();
            insert_trust.executeUpdate();
        } catch (Exception ex) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("INSERT Sijania indique que votre iWorld est déjà créé.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        IworldsUtils.cm("INSERT REQUEST: OK");

        // Configuration du monde
        Sponge.getServer().getWorld(worldname).get().setKeepSpawnLoaded(true);
        IworldsUtils.cm("iWorld " + worldname + ": KeepSpawnLoaded activé.");
        Sponge.getServer().getWorld(worldname).get().getWorldBorder().setCenter(0, 0);
        IworldsUtils.cm("iWorld " + worldname + ": Centre du WorldBorder défini en x:0, y:0.");
        Sponge.getServer().getWorld(worldname).get().getWorldBorder().setDiameter(500);
        IworldsUtils.cm("iWorld " + worldname + ": Diamètre du WorldBorder défini à 500");

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Sijania vient de terminer son oeuvre, voici votre iWorld !").color(TextColors.AQUA))).build()));

        IworldsLocations.teleport(pPlayer, worldname);

        pPlayer.sendTitle(IworldsUtils.titleSubtitle("Bienvenue, " + pPlayer.getName(), "Cet iWorld, vous appartient désormais !" ));
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commandes de création des iWorlds"))
                .permission("iworlds.creation")
                .executor(new CreationCommande())
                .build();
    }
}

