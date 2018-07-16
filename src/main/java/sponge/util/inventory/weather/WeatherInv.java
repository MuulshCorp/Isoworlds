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
package sponge.util.inventory.weather;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
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

public class WeatherInv {

    public static Inventory getInv(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    String mtype = "";

                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Soleil")) {
                        mtype = "soleil";
                    } else if (menuName.contains("Pluie")) {
                        mtype = "pluie";
                    } else if (menuName.contains("Orage")) {
                        mtype = "storm";
                    } else if (menuName.contains("Menu principal")) {
                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                    }

                    if (menuName.contains("10 minutes")) {
                        // Check if charge higher than 0
                        MainInv.commandMenu(pPlayer, "iw meteo " + mtype + " 12000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("30 minutes")) {
                        MainInv.commandMenu(pPlayer, "iw meteo " + mtype + " 36000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("1 heure")) {
                        MainInv.commandMenu(pPlayer, "iw meteo " + mtype + " 72000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        MainInv.closeMenu(pPlayer);
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Météo").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 3))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Le temps devient paisible et ensoleillé."));

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Vos terres boivent l'eau de pluie."));

        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("L'orage fait rage !"));

        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Retour au menu principal"));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [10 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [30 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [1 heure]")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [10 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [30 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [1 heure]")
                .color(TextColors.BLUE).build())).quantity(1).build();

        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [10 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [30 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [1 heure]")
                .color(TextColors.RED).build())).quantity(1).build();

        ItemStack item10 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(0, 1)).set(item4);
        menu.query(SlotPos.of(1, 1)).set(item5);
        menu.query(SlotPos.of(2, 1)).set(item6);
        menu.query(SlotPos.of(0, 2)).set(item7);
        menu.query(SlotPos.of(1, 2)).set(item8);
        menu.query(SlotPos.of(2, 2)).set(item9);
        menu.query(SlotPos.of(8, 2)).set(item10);

        return menu;
    }
}
