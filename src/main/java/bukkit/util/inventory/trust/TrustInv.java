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
package bukkit.util.inventory.trust;

import bukkit.util.inventory.MainInv;
import bukkit.util.inventory.trust.sub.TrustAccessInv;
import bukkit.util.inventory.trust.sub.TrustAddInv;
import bukkit.util.inventory.trust.sub.TrustDeleteInv;
import common.action.IsoWorldsAction;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TrustInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "IsoWorlds: Confiance", 2, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getDisplayName();
                if (menuName.contains("Ajouter")) {
                    // Check if player has iw
                    if (IsoWorldsAction.iwExists(pPlayer.getUniqueId().toString())) {
                        TrustAddInv.getInv(pPlayer).open(pPlayer);
                    }
                } else if (menuName.contains("Retirer")) {
                    if (IsoWorldsAction.iwExists(pPlayer.getUniqueId().toString())) {
                        TrustDeleteInv.getInv(pPlayer).open(pPlayer);
                    }
                } else if (menuName.contains("Mes accès")) {
                    TrustAccessInv.getInv(pPlayer).open(pPlayer);
                } else if (menuName.contains("Menu principal")) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        // Lore
        String[] list1 = new String[]{"Autoriser l'accès à votre IsoWorld."};
        String[] list2 = new String[]{"Retirer l'accès à votre IsoWorld."};
        String[] list3 = new String[]{"Vois les IsoWorlds accessibles"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GREEN + "Ajouter", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Retirer", list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData()), ChatColor.RED + "Mes accès", list3);

        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }
}
