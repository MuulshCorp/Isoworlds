package common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edwin on 24/10/2017.
 */
public class Messages {

    public static void erreurSQL() {

    }

    // Renvoi les messages d'erreur
    public static String getError(String type) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("SQL", "[Erreur 1: " + type + "] Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).");
        message.put("EXISTE_IWORLD", "[Erreur 2: iWorld physique existant] Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).");
        return (message.get(type));
    }

    // Renvoi les messages
    public static String getMessage(String type) {
        Map<String, String> message = new HashMap<String, String>();

        message.put("CREATION_IWORLD", "Sijania rassemble son pouvoir...");
        message.put("TITRE_BIENVENUE_1", "Bienvenue, ");
        message.put("TITRE_BIENVENUE_2", "Cet iWorld est désormais votre.");
        message.put("EXISTE_IWORLD", "Sijania vous a déjà créé un iWorld.");
        message.put("EXISTE_PAS_IWORLD", "Sijania ne trouve aucun iWorld vous appartenant.");
        message.put("EXISTE_TRUST", "Sijania vous indique que ce joueur est déjà de confiance.");
        message.put("EXISTE_PAS_TRUST", "");
        message.put("INVALIDE_JOUEUR", "Sijania ne trouve pas le joueur que indiqué.");
        message.put("INVALIDE_IWORLD", "");
        message.put("SUCCES_CREATION", "");
        message.put("SUCCES_TELEPORTATION", "");
        message.put("SUCCES_TRUST", "");
        message.put("SUCCES_METEO", "");
        message.put("SUCCES_CHARGEMENT", "");
        message.put("SUCCES_DECHARGEMENT", "");
        message.put("SUCCES_REFONTE", "");
        message.put("SUCCES_RETIRER_CONFIANCE", "");
        message.put("SUCCES_CREATION_1", "Sijania vient de terminer son oeuvre et vous offre votre iWorld !");
        message.put("SUCCES_CREATION_2", "");
        message.put("OFF_JOUEUR", "");
        message.put("ON_JOUEUR", "");
        return (message.get(type));
    }
}
