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
package bukkit.util.inventory.build;

import bukkit.util.inventory.MainInv;
import bukkit.util.inventory.build.sub.CreateInv;
import common.action.IsoworldsAction;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import bukkit.util.console.Logger;

import static common.Msg.msgNode;

public class BuildInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "Isoworlds: " + msgNode.get("InvBuild"), 1, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains(msgNode.get("BuildCreate"))) {
                    Logger.tracking("Clic menu CREATION: " + p.getName());
                    CreateInv.getInv(pPlayer).open(pPlayer);
                } else if (menuName.contains(msgNode.get("BuildReforge"))) {
                    Logger.tracking("Clic menu REFONTE: " + p.getName());
                    p.performCommand("iw r");
                    p.closeInventory();
                } else if (menuName.contains(msgNode.get("MainMenu"))) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        // Affiche la refonte si le monde est créé, sinon affiche la création
        if (IsoworldsAction.iwExists(pPlayer.getUniqueId().toString())) {
            String[] list1 = new String[]{msgNode.get("BuildReforgeLore")};
            menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.GOLD + msgNode.get("BuildReforge"), list1);
        } else {
            String[] list1 = new String[]{msgNode.get("BuildCreateLore")};
            menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GOLD + msgNode.get("BuildCreate"), list1);
        }

        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + msgNode.get("MainMenu"), msgNode.get("MainMenuLore"));

        return menu;
    }
}