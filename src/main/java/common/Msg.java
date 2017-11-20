package common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edwin on 24/10/2017.
 */
public class Msg {
    public static interface keys {
        String CREATION_IWORLD
                = "Sijania concentre son pouvoir...";
        String TITRE_BIENVENUE_1
                = "Bienvenue, ";
        String TITRE_BIENVENUE_2
                = "Sijania > Cet IsoWorld est désormais votre.";
        String EXISTE_IWORLD
                = "Sijania > Votre IsoWorld est déjà constitué, pourquoi en vouloir un deuxième ?";
        String EXISTE_PAS_IWORLD
                = "Sijania > Je ne puis trouver votre IsoWorld.";
        String EXISTE_TRUST
                = "Sijania > Votre marque de confiance est déjà présente sur ce joueur.";
        String EXISTE_PAS_TRUST
                = "Sijania > Vous n'êtes pas autorisé à joindre cet IsoWorld, demandez l'autorisation.";
        String EXISTE_PAS_TRUST_2
                = "Sijania > Vous n'avez posé aucune marque de confiance sur ce joueur...";
        String INVALIDE_JOUEUR
                = "Sijania > Est-ce bien un joueur ?";
        String INVALIDE_ISOWORLD
                = "Sijania > Je ne reconnais pas cet IsoWorld...";
        String SUCCES_TELEPORTATION
                = "Sijania > Vous êtes de confiance, continuez votre chemin...";
        String SUCCES_TRUST
                = "Sijania > Votre marque de confiance vient d'être posée sur ce joueur, il peut désormais fouler vos terres.";
        String SUCCES_METEO
                = "L'humeur de Sijania change...";
        String SUCCES_CHARGEMENT
                = "Sijania > Votre IsoWorld s'est reveillé";
        String SUCCES_DECHARGEMENT
                = "Sijania > Votre IsoWorld s'endort...";
        String SUCCES_REFONTE
                = "Sijania > Mon pouvoir se vide... Mais, votre IsoWorld renaît de ses cendres";
        String SUCCES_RETIRER_CONFIANCE
                = "Sijania > Ce joueur ressent une brûlure... La marque de confiance vient de lui être retirée !";
        String SUCCES_CREATION_1
                = "Sijania vient de terminer son oeuvre et vous offre votre IsoWorld !";
        String SUCCES_CREATION_2
                = "Cet iWorld est désormais votre !";
        String OFF_JOUEUR
                = "Sijania > Ce joueur n'est pas reveillé...";
        String ON_JOUEUR
                = "Sijania > Ce joueur est présent.";
        String CONFIRMATION
                = "Veuillez de nouveau entrer la commande pour confirmer";
        String REFONTE_KICK
                = "Sijania entame une destruction entière de l'IsoWorld dans lequel vous vous trouviez sur demande de son propriétaire, vous avez été renvoyé au spawn pour votre protection";
        String DENY_SELF_REMOVE
                = "Sijania > Vous ne pouvez retirer vos droit de ma création...";
        String KICK_TRUST
                = "Sijania > Votre marque de confiance vient d'être retirée par le maître de l'IsoWorld dans lequel vous vous trouviez. Ce sanctuaire vous est désormais interdit.";
        String DENY_TELEPORT
                = "Sijania > Vous ne pouvez pénétrer ce sanctuaire sans une marque de confiance.";
        String SQL
                = "[Erreur 1]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
        String FICHIERS
                = "[Erreur 2]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
        String DATA
                = "[Erreur 3]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
    }
}
