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
package sponge.util.inventory.warp;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.util.inventory.MainInv;

import java.util.ArrayList;
import java.util.List;

import static sponge.MainSponge.instance;

public class WarpInv {
    public static Inventory getInv(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Exploration")) {
                        MainInv.commandMenu(pPlayer, "iw warp exploration");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("Minage")) {
                        MainInv.commandMenu(pPlayer, "iw warp minage");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("End")) {
                        MainInv.commandMenu(pPlayer, "iw warp end");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("Nether")) {
                        MainInv.commandMenu(pPlayer, "iw warp nether");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Warp").color(TextColors.DARK_GREEN).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 2))
                .build(instance);

        // Minage
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Exploitez les ressources (quarry...)"));
        list1.add(Text.of("Réinitialisé tous 1er du mois à 19h"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.STONE_PICKAXE).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Minage")
                .color(TextColors.GREEN).build())).quantity(1).build();

        // Exploration
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Explorez, combattez, enrichissez vous !"));
        list2.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.FILLED_MAP).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Exploration")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        // End
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Un grondement sourd se fait entendre..."));
        list3.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.ENDER_PEARL).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("End")
                .color(TextColors.DARK_GRAY).build())).quantity(1).build();

        // Nether
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Lieu très hostile !"));
        list4.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.NETHER_STAR).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Nether")
                .color(TextColors.DARK_RED).build())).quantity(1).build();

        // Menu principal
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of("Retour au menu principal"));

        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(8, 1)).set(item9);

        return menu;
    }
}
