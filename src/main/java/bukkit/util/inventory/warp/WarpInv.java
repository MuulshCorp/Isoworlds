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
package bukkit.util.inventory.warp;

import bukkit.util.inventory.MainInv;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class WarpInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.DARK_GREEN + "IsoWorlds: Warp", 2, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("Minage")) {
                    p.performCommand("iw warp minage");
                    p.closeInventory();
                } else if (menuName.contains("Exploration")) {
                    p.performCommand("iw warp exploration");
                    p.closeInventory();
                } else if (menuName.contains("End")) {
                    p.performCommand("iw warp end");
                    p.closeInventory();
                } else if (menuName.contains("Nether")) {
                    p.performCommand("iw warp nether");
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });
        // Minage
        String[] list1 = new String[]{"Exploitez les ressources (quarry...)", "Réinitialisé tous 1er du mois à 19h"};

        // Exploration
        String[] list2 = new String[]{"Explorez, combattez, enrichissez vous !", "Réinitialisé tous les vendredi à 19h"};

        // End
        String[] list3 = new String[]{"Un grondement sourd se fait entendre...", "Réinitialisé tous les vendredi à 19h"};

        // Nether
        String[] list4 = new String[]{"Lieu très hostile !", "Réinitialisé tous les vendredi à 19h"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.STONE_PICKAXE, 1), ChatColor.GREEN + "Minage", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.MAP, 1), ChatColor.YELLOW + "Exploration", list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.ENDER_PEARL, 1), ChatColor.DARK_GRAY + "End", list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.NETHER_STAR, 1), ChatColor.DARK_RED + "Nether", list4);

        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }
}
