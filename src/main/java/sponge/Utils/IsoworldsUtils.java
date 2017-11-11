package sponge.Utils;

import common.Msg;
import sponge.IsoworldsSponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by Edwin on 08/10/2017.
 */
public class IsoworldsUtils {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    public static boolean isSolid(Location blockLoc) {
        if (blockLoc.getProperty(SolidCubeProperty.class).isPresent()) {
            SolidCubeProperty property = (SolidCubeProperty) blockLoc.getProperty(SolidCubeProperty.class).get();
            return property.getValue();
        }
        return false;
    }

    // Méthode pour convertir type player à uuid
    public static UUID PlayerToUUID(Player player) {
        UUID uuid;
        uuid = player.getUniqueId();
        return (uuid);
    }

    // Envoie un message au serveur
    public static void sm(String msg) {
        Sponge.getServer().getBroadcastChannel().send(Text.of("[IsoWorlds]: " + msg));
    }

    // Envoie un message à la console
    public static void cm(String msg) {
        Sponge.getServer().getConsole().sendMessage(Text.of("[IsoWorlds]: " + msg));
    }

    // Executer une commande sur le serveur
    public void cmds(String cmd) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
    }

    // Titles
    public static Title title(String message) {
        Text text = Text.of(Text.builder(message).color(TextColors.AQUA).build());
        Title title = Title.builder().title(Text.of(text)).build();
        return title;
    }

    public static Title subtitle(String message) {
        Text text = Text.of(Text.builder(message).color(TextColors.GOLD).build());
        Title subtitle = Title.builder().subtitle(Text.of(message)).build();
        return subtitle;
    }

    public static Title titleSubtitle(String title, String subtitle) {
        Text Titre = Text.of(Text.builder(title).color(TextColors.GOLD).build());
        Text SousTitre = Text.of(Text.builder(subtitle).color(TextColors.AQUA).build());
        Title ready =(Title) Title.of(Titre, SousTitre);
        return ready;
    }

    // TPS
    private static final DecimalFormat tpsFormat = new DecimalFormat("#0.00");
    // TPS
    public static Text getTPS(double currentTps){
        TextColor colour;

        if (currentTps > 18) {
            colour = TextColors.GREEN;
        } else if (currentTps > 15) {
            colour = TextColors.YELLOW;
        } else {
            colour = TextColors.RED;
        }
        return Text.of(colour, tpsFormat.format(currentTps));
    }

    public static void coloredMessage(Player pPlayer, String message){
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(message).color(TextColors.AQUA))).build()));
    }

    public static void getHelp(Player pPlayer) {
        pPlayer.sendMessage(Text.of(Text.builder("--------------------- [ ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("IsoWorlds ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("] ---------------------").color(TextColors.GOLD)))
                .build()));

        pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

        Text cm1 = Text.of(Text.builder("- Basique:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("|| ").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("/isoworld ").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("|| ").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("/isoworlds").color(TextColors.GREEN)))
                .build());
        pPlayer.sendMessage(cm1);

        Text cm2 = Text.of(Text.builder("- Création:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("creation").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("créer").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("creer").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("create").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("c").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm2);

        Text cm3 = Text.of(Text.builder("- Refonte:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("refonte").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("refondre").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("r").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm3);

        Text cm5 = Text.of(Text.builder("- Désactivation:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("désactiver").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("off").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("décharger").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("unload").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm5);

        Text cm6 = Text.of(Text.builder("- Activation:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("activer").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("charger").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("on").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("load").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm6);

        Text cm7 = Text.of(Text.builder("- Confiance:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("confiance").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("trust").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("a").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("] <").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("pseudo").color(TextColors.GREEN)))
                .append(Text.of(Text.builder(">").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm7);

        Text cm8 = Text.of(Text.builder("- Retirer:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("retirer").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("untrust").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("supprimer").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("remove").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("] <").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("pseudo").color(TextColors.GREEN)))
                .append(Text.of(Text.builder(">").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm8);

        Text cm9 = Text.of(Text.builder("- Météo:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("météo").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("meteo").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("weather").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("m").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm9);

        Text cm10 = Text.of(Text.builder("- Maison:").color(TextColors.GOLD)
                .append(Text.of(Text.builder(" /iw ").color(TextColors.AQUA)))
                .append(Text.of(Text.builder("[").color(TextColors.GOLD)))
                .append(Text.of(Text.builder("maison").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("home").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("][").color(TextColors.GOLD)))

                .append(Text.of(Text.builder("h").color(TextColors.GREEN)))
                .append(Text.of(Text.builder("]").color(TextColors.GOLD)))
                .build());
        pPlayer.sendMessage(cm10);

        pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

    }

    // check if iworld exists
    public static Boolean iworldExists(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = IsoworldsUtils.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                Sponge.getServer().loadWorld(IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
                return true;
            }
        } catch (Exception se){
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // check if status is push or pull exists
    // true si présent, false si envoyé ou à envoyer
    public static Boolean iworldStatus(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT STATUS FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = IsoworldsUtils.PlayerToUUID(pPlayer).toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                if (rselect.getInt(1) == 0) {
                    return true;
                } else {
                    return false;
                }

            }
        } catch (Exception se){
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // Check autorisation trust
    public static Boolean trustExists(Player pPlayer, UUID uuidcible, String messageErreur) {
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check_p = uuidcible.toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId() + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return false;
    }

    // insert trust
    public static Boolean insertCreation(Player pPlayer, String messageErreur) {
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`) VALUES (?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, plugin.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // insert trust
    public static Boolean insertTrust(Player pPlayer, UUID uuidcible, String messageErreur) {
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String INSERT = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`) VALUES (?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = uuidcible.toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, plugin.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // delete iworld
    public static Boolean deleteIworld(Player pPlayer, String messageErreur) {
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String DELETE_IWORLDS = "DELETE FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = plugin.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = plugin.database.prepare(DELETE_IWORLDS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, plugin.servername);

            // delete iworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, plugin.servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }

    // Delete trust
    public static Boolean deleteTrust(Player pPlayer, UUID uuid, String messageErreur) {
        IsoworldsSponge plugin = IsoworldsSponge.instance;
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = plugin.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = uuid.toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisation
            delete_autorisations.setString(1, Iuuid_p);
            delete_autorisations.setString(2, Iuuid_w);
            delete_autorisations.setString(3, plugin.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(messageErreur).color(TextColors.AQUA))).build()));
            return false;
        }
        return true;
    }


}
