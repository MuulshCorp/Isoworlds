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
package bukkit.util.inventory.weather;

import bukkit.util.inventory.MainInv;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class WeatherInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "IsoWorlds: Météo", 3, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                String mtype = "";

                if (menuName.contains("Soleil")) {
                    mtype = "soleil";
                } else if (menuName.contains("Pluie")) {
                    mtype = "pluie";
                } else if (menuName.contains("Orage")) {
                    //mtype = "storm";
                    // INDISPONIBLE
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }

                if (menuName.contains("10 minutes")) {
                    p.performCommand("iw meteo " + mtype + " 12000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                } else if (menuName.contains("30 minutes")) {
                    p.performCommand("iw meteo " + mtype + " 36000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                } else if (menuName.contains("1 heure")) {
                    p.performCommand("iw meteo " + mtype + " 72000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                }
                return true;
            }
        });

        String[] list1 = new String[]{"Le temps devient paisible et ensoleillé."};
        String[] list2 = new String[]{"Vos terres boivent l'eau de pluie."};
        String[] list3 = new String[]{"Indisponible sur la version 1.7.10, désolé ! =D"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [10 minutes]", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [30 minutes]", list1);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [1 heure]", list1);

        menu.addButton(menu.getRow(1), 0, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [10 minutes]", list2);
        menu.addButton(menu.getRow(1), 1, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [30 minutes]", list2);
        menu.addButton(menu.getRow(1), 2, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [1 heure]", list2);

        menu.addButton(menu.getRow(2), 0, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [10 minutes]", list3);
        menu.addButton(menu.getRow(2), 1, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [30 minutes]", list3);
        menu.addButton(menu.getRow(2), 2, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [1 heure]", list3);

        menu.addButton(menu.getRow(2), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }
}
