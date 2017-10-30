package common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edwin on 24/10/2017.
 */
public class Msg {
    public static interface keys {
        public static final String CREATION_IWORLD
                = "Sijania concentre son pouvoir...";
        public static final String TITRE_BIENVENUE_1
                = "Bienvenue, ";
        public static final String TITRE_BIENVENUE_2
                = "Sijania > Cet IsoWorld est désormais votre.";
        public static final String EXISTE_IWORLD
                = "Sijania > Votre IsoWorld est déjà constitué, pourquoi en vouloir un deuxième ?";
        public static final String EXISTE_PAS_IWORLD
                = "Sijania > Je ne puis trouver votre IsoWorld.";
        public static final String EXISTE_TRUST
                = "Sijania > Votre marque de confiance est déjà présente sur ce joueur.";
        public static final String EXISTE_PAS_TRUST
                = "Sijania > Vous n'êtes pas autorisé à joindre cet IsoWorld, demandez l'autorisation.";
        public static final String EXISTE_PAS_TRUST_2
                = "Sijania > Vous n'avez posé aucune marque de confiance sur ce joueur...";
        public static final String INVALIDE_JOUEUR
                = "Sijania > Est-ce bien un joueur ?";
        public static final String INVALIDE_ISOWORLD
                = "Sijania > Je ne reconnais pas cet IsoWorld...";
        public static final String SUCCES_TELEPORTATION
                = "Sijania > Vous êtes de confiance, continuez votre chemin...";
        public static final String SUCCES_TRUST
                = "Sijania > Votre marque de confiance vient d'être posée sur ce joueur, il peut désormais fouler vos terres.";
        public static final String SUCCES_METEO
                = "L'humeur de Sijania change...";
        public static final String SUCCES_CHARGEMENT
                = "Sijania > Votre IsoWorld s'est reveillé";
        public static final String SUCCES_DECHARGEMENT
                = "Sijania > Votre IsoWorld s'endort...";
        public static final String SUCCES_REFONTE
                = "Sijania > Mon pouvoir se vide... Mais, votre IsoWorld renaît de ses cendres";
        public static final String SUCCES_RETIRER_CONFIANCE
                = "Sijania > Ce joueur ressent une brûlure... La marque de confiance vient de lui être retirée !";
        public static final String SUCCES_CREATION_1
                = "Sijania vient de terminer son oeuvre et vous offre votre IsoWorld !";
        public static final String SUCCES_CREATION_2
                = "Cet iWorld est désormais votre !";
        public static final String OFF_JOUEUR
                = "Sijania > Ce joueur n'est pas reveillé...";
        public static final String ON_JOUEUR
                = "Sijania > Ce joueur est présent.";
        public static final String CONFIRMATION
                = "Veuillez de nouveau entrer la commande pour confirmer";
        public static final String REFONTE_KICK
                = "Sijania entame une destruction entière de l'IsoWorld dans lequel vous vous trouviez sur demande de son propriétaire, vous avez été renvoyé au spawn pour votre protection";
        public static final String DENY_SELF_REMOVE
                = "Sijania > Vous ne pouvez retirer vos droit de ma création...";
        public static final String KICK_TRUST
                = "Sijania > Votre marque de confiance vient d'être retirée par le maître de l'IsoWorld dans lequel vous vous trouviez. Ce sanctuaire vous est désormais interdit.";
        public static final String DENY_TELEPORT
                = "Sijania > Vous ne pouvez pénétrer ce sanctuaire sans une marque de confiance.";
        public static final String SQL
                = "[Erreur 1]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
        public static final String FICHIERS
                = "[Erreur 2]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
        public static final String DATA
                = "[Erreur 3]: Une erreur est survenue, veuillez contacter l'équipe Isolonice (discord/ticket/forum/mp).";
    }
}
