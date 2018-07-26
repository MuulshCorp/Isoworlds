/*
 * This file is part of Isoworlds, licensed under the MIT License (MIT).
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
package bukkit.util.inventory.biome;

import bukkit.util.inventory.MainInv;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static common.Msg.msgNode;

public class BiomeInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "Isoworlds: Biome", 2, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains(msgNode.get("BiomePlain"))) {
                    p.performCommand("iw biome plaines");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeDesert"))) {
                    p.performCommand("iw biome desert");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeSwamp"))) {
                    p.performCommand("iw biome marais");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeOcean"))) {
                    p.performCommand("iw biome océan");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeMushroom"))) {
                    p.performCommand("iw biome champignon");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeJungle"))) {
                    p.performCommand("iw biome jungle");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeHell"))) {
                    p.performCommand("iw biome enfer");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("BiomeEndNotAvailable"))) {
                    p.closeInventory();
                    // INDISPONIBLE
                } else if (menuName.contains(msgNode.get("MainMenu"))) {
                    MainInv.MenuPrincipal(p).open(p);
                }
                return true;
            }
        });
        // Plaines
        String[] list1 = new String[]{msgNode.get("BiomePlainLore"), msgNode.get("BiomePlainLore2")};

        // Désert
        String[] list2 = new String[]{msgNode.get("BiomeDesertLore"), msgNode.get("BiomeDesertLore2")};

        // Marais
        String[] list3 = new String[]{msgNode.get("BiomeSwampLore"), msgNode.get("BiomeSwampLore2")};

        // Océan
        String[] list4 = new String[]{msgNode.get("BiomeOceanLore")};

        // Champignon
        String[] list5 = new String[]{msgNode.get("BiomeMushroomLore"), msgNode.get("BiomeMushroomLore2")};

        // Jungle
        String[] list6 = new String[]{msgNode.get("BiomeJungleLore"), msgNode.get("BiomeJungleLore2")};

        // Enfer
        String[] list7 = new String[]{msgNode.get("BiomeHellLore")};

        // End
        String[] list8 = new String[]{msgNode.get("BiomeEndNotAvailable")};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + msgNode.get("BiomePlain"), list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.SAND, 1), ChatColor.YELLOW + msgNode.get("BiomeDesert"), list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.CLAY, 1), ChatColor.GRAY + msgNode.get("BiomeSwamp"), list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), ChatColor.BLUE + msgNode.get("BiomeOcean"), list4);
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.RED_MUSHROOM, 1), ChatColor.RED + msgNode.get("BiomeMushroom"), list5);
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.SAPLING, 1), ChatColor.DARK_GREEN + msgNode.get("BiomeJungle"), list6);
        menu.addButton(menu.getRow(0), 6, new ItemStack(Material.NETHERRACK, 1), ChatColor.DARK_RED + msgNode.get("BiomeHell"), list7);
        menu.addButton(menu.getRow(0), 7, new ItemStack(Material.ENDER_STONE, 1), ChatColor.DARK_PURPLE + msgNode.get("BiomeEnd"), list8);
        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + msgNode.get("MainMenu"), msgNode.get("MainMenuLore"));

        return menu;
    }
}
