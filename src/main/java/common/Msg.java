/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
 *
 * Copyright (c) Edwin Petremann <https://github.com/Isolonice/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package common;

public class Msg {
    public interface keys {
        // ISOWORLD
        String ISOWORLD_NOT_FOUND
                = "IsoWorld introuvable, il n'est peut être pas chargé ? Tu peux le charger via le menu Maison";
        String ISOWORLD_ALREADY_EXISTS
                = "Un IsoWorld t'appartient déjà";
        String ISOWORLD_SUCCESS_CREATE
                = "IsoWorld créé avec succès !";
        String NOT_IN_A_ISOWORLD
                = "Tu dois être dans un IsoWorld pour faire ça";
        String CREATING_ISOWORLD
                = "Ton IsoWorld est en cours de création !";
        String PATERN_TYPES
                = "4 types de IsoWorld sont disponibles:";
        String PATERN_TYPES_DETAIL
                = "- Plat : Océan : Normal : Vide";
        String HEADER_ISOWORLD
                = "--------------------- [ IsoWorlds ] ---------------------";
        String WELCOME_1
                = "Bienvenue, ";
        String WELCOME_2
                = "cet IsoWorld est désormais le tiens !";
        String SPACE_LINE
                = " ";
        String CONFIRMATION
                = "Entre de nouveau la commande pour confirmer";
        String REFORGE_KICK
                = "L'IsoWorld dans lequel tu te trouvais être entrain d'être reforgé, sur demande du propriétaire";
        String FAIL_REFORGE_ISOWORLD
                = "Echec de la refonte de ton IsoWorld, retente et demande aux staff si c'est sans succès";
        String SUCCES_REFORGE
                = "Ton IsoWorld a été reforgé avec succès !";
        String PROCESSING_PULL
                = "Ton IsoWorld est en cours de transfert sur ce serveur, temps estimé: 60 secondes";
        String FAIL_PULL
                = "Ton IsoWorld n'a pas pu être transféré sur ce serveur, retente dans quelques minutes";
        String SUCCESS_PULL
                = "Ton IsoWorld a été transféré avec succès sur ce serveur, tu peux maintenant t'y rendre !";

        // TRUST
        String NOT_TRUSTED
                = "Tu n'as pas les autorisations du propriétaire de cet IsoWorld";
        String KICK_TRUST
                = "Tes droits sur cet IsoWorld viennent de t'être retirés, demande au propriétaire pour plus d'information";
        String INVALID_PLAYER
                = "Ce joueur n'existe pas ou ne s'est jamais connecté sur le serveur";
        String ALREADY_TRUSTED
                = "Ce joueur a déjà ton autorisation";
        String SUCCESS_TRUST
                = "Tu viens d'autoriser ce joueur à rejoindre ton IsoWorld, il peut maintenant intéragir avec alors attention !";
        String SUCCESS_UNTRUST
                = "Tu viens de retirer les accès de ce joueur à ton IsoWorld, il ne peut donc plus venir chez toi";
        String DENY_TELEPORT
                = "Tu n'as pas les droits pour te rendre ici";
        String DENY_SELF_REMOVE
                = "Ce qui est à toi est à toi, tu ne peux pas retirer ton propre accès !";

        // TIME
        String TIME_CHANGE_SUCCESS
                = "Le temps de cet IsoWorld vient d'être changé par: ";
        String TIME_TYPES
                = "2 types de temps sont disponibles:";
        String TIME_TYPES_DETAIL
                = "- Jour : Nuit";

        // WEATHER
        String WEATHER_CHANGE_SUCCESS
                = "Le temps de cet IsoWorld vient d'être changé par: ";
        String WEATHER_TYPES
                = "3 types de climat sont disponibles:";
        String WEATHER_TYPES_DETAIL
                = "- Soleil : Pluie : Orage";

        // CHARGE
        String CHARGE_USED
                = "Tu viens d'utiliser une charge pour cette action";
        String CHARGE_EMPTY
                = "Tu n'as plus de charge !";

        // BIOME
        String BIOME_NOT_FOUND = "Ce biome est inconnu";
        String BIOME_CHANGED = "Tu viens de changer le biome du chunk dans lequel tu te trouves avec succès !";

        // UTILS
        String UNAVAILABLE_COMMAND = "Commande inconnue";
    }
}
