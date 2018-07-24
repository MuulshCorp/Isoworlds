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
package bukkit.util.inventory.trust.sub;

import bukkit.util.console.Logger;
import bukkit.util.inventory.MainInv;
import common.action.TrustAction;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.util.UUID;

public class TrustDeleteInv implements Listener {

    @SuppressWarnings("deprecation")
    public static MainInv getInv(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "IsoWorlds: Confiance > Retirer", 4, new MainInv.onClick() {
            @Override
            public boolean click(Player p, MainInv menu, MainInv.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw retirer " + ChatColor.stripColor(menuPlayer));
                    Logger.info("NAME REMOVE " + ChatColor.stripColor(menuPlayer));
                    p.closeInventory();
                } else if (menuName.contains("menu principal")) {
                    MainInv.MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });


        int i = 0;
        int j = 0;
        ResultSet trusts = TrustAction.getTrusts(pPlayer.getUniqueId().toString());

        try {
            while (trusts.next()) {
                // Récupération du nom du joueur
                String tmp = trusts.getString(1);
                Logger.info("name = " + tmp);
                UUID uuid = UUID.fromString(tmp);
                Logger.info("uuid = " + uuid);

                String pname;
                if (Bukkit.getServer().getPlayer(uuid) == null) {
                    pname = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                } else {
                    pname = Bukkit.getServer().getPlayer(uuid).getDisplayName();
                }

                // Dont show own access
                if (pname.equals(pPlayer.getDisplayName())) {
                    continue;
                }

                // Construction du lore
                String list1 = "Joueur";


                // Construction des skin itemstack
                ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta sm = (SkullMeta) item1.getItemMeta();
                sm.setOwner(pname);
                item1.setItemMeta(sm);

                if (i >= 8) {
                    j = j++;
                }
                menu.addButton(menu.getRow(j), i, item1, ChatColor.GREEN + pname, list1);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }
}