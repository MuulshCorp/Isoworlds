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
package sponge.util.action;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.MainSponge;

public class LockAction {

    public static final MainSponge plugin = MainSponge.instance;

    // Vérifie si le lock est présent et renvoi vrai, sinon défini le lock et renvoi false
    public static Boolean isLocked(Player pPlayer, String className) {
        // Si le lock est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (checkLockFormat(pPlayer.getUniqueId().toString(), className)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
            return true;
        } else {
            // On set lock
            plugin.lock.put(pPlayer.getUniqueId().toString() + ";" + className, 1);
            return false;
        }
    }

    // Cooldown modèle: uuid;commande
    public static Boolean checkLockFormat(String pPlayer, String command) {
        // Si le tableau est null alors lock 0 sinon lock 1
        if (plugin.lock.get(pPlayer + ";" + command) == null) {
            return false;
        } else {
            return true;
        }
    }
}
