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
package bukkit.util.inventory.warp;

import bukkit.util.inventory.MainInv;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static common.Msg.msgNode;

public class WarpInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.DARK_GREEN + "Isoworlds: " + msgNode.get("InvWarp"), 2, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains(msgNode.get("WarpMining"))) {
                    p.performCommand("iw warp minage");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("WarpExploration"))) {
                    p.performCommand("iw warp exploration");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("WarpEnd"))) {
                    p.performCommand("iw warp end");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("WarpNether"))) {
                    p.performCommand("iw warp nether");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("MainMenu"))) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });
        // Minage
        String[] list1 = new String[]{msgNode.get("WarpMiningLore"), msgNode.get("WarpMiningLore2")};

        // Exploration
        String[] list2 = new String[]{msgNode.get("WarpExplorationLore"), msgNode.get("WarpExplorationLore2")};

        // End
        String[] list3 = new String[]{msgNode.get("WarpEndLore"), msgNode.get("WarpEndLore2")};

        // Nether
        String[] list4 = new String[]{msgNode.get("WarpNetherLore"), msgNode.get("WarpNetherLore2")};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.STONE_PICKAXE, 1), ChatColor.GREEN + msgNode.get("WarpMining"), list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.MAP, 1), ChatColor.YELLOW + msgNode.get("WarpExploration"), list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.ENDER_PEARL, 1), ChatColor.DARK_GRAY + msgNode.get("WarpEnd"), list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.NETHER_STAR, 1), ChatColor.DARK_RED + msgNode.get("WarpNether"), list4);

        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + msgNode.get("MainMenu"), msgNode.get("MainMenuLore"));

        return menu;
    }
}
