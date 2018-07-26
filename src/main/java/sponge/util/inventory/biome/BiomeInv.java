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
package sponge.util.inventory.biome;

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

import static common.Msg.msgNode;
import static sponge.Main.instance;

public class BiomeInv {

    // BIOME
    public static Inventory getInv(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains(msgNode.get("BiomePlain"))) {
                        MainInv.commandMenu(pPlayer, "iw biome plaines");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeDesert"))) {
                        MainInv.commandMenu(pPlayer, "iw biome desert");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeSwamp"))) {
                        MainInv.commandMenu(pPlayer, "iw biome marais");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeOcean"))) {
                        MainInv.commandMenu(pPlayer, "iw biome océan");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeMushroom"))) {
                        MainInv.commandMenu(pPlayer, "iw biome champignon");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeJungle"))) {
                        MainInv.commandMenu(pPlayer, "iw biome jungle");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeHell"))) {
                        MainInv.commandMenu(pPlayer, "iw biome enfer");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("BiomeEnd"))) {
                        MainInv.commandMenu(pPlayer, "iw biome end");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains(msgNode.get("MainMenu"))) {
                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("Isoworlds: Biome").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 2))
                .build(instance);

        // Plaines
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of(msgNode.get("BiomePlainLore")));
        list1.add(Text.of(msgNode.get("BiomePlainLore2")));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomePlain"))
                .color(TextColors.GREEN).build())).quantity(1).build();

        // Désert
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of(msgNode.get("BiomeDesertLore")));
        list2.add(Text.of(msgNode.get("BiomeDesertLore2")));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeDesert"))
                .color(TextColors.YELLOW).build())).quantity(1).build();

        // Marais
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of(msgNode.get("BiomeSwampLore")));
        list3.add(Text.of(msgNode.get("BiomeSwampLore2")));

        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.CLAY).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeSwamp"))
                .color(TextColors.GRAY).build())).quantity(1).build();

        // Océan
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of(msgNode.get("BiomeOceanLore")));

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.BLUE).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeOcean"))
                .color(TextColors.BLUE).build())).quantity(1).build();

        // Champignon
        List<Text> list5 = new ArrayList<Text>();
        list5.add(Text.of(msgNode.get("BiomeMushroomLore")));
        list5.add(Text.of(msgNode.get("BiomeMushroomLore2")));

        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.RED_MUSHROOM).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeMushroom"))
                .color(TextColors.RED).build())).quantity(1).build();

        // Jungle
        List<Text> list6 = new ArrayList<Text>();
        list6.add(Text.of(msgNode.get("BiomeJungleLore")));
        list6.add(Text.of(msgNode.get("BiomeJungleLore2")));

        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.SAPLING).add(Keys.ITEM_LORE, list6).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeJungle"))
                .color(TextColors.DARK_GREEN).build())).quantity(1).build();

        // Enfer
        List<Text> list7 = new ArrayList<Text>();
        list7.add(Text.of(msgNode.get("BiomeHellLore")));

        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.NETHERRACK).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeHell"))
                .color(TextColors.DARK_RED).build())).quantity(1).build();

        // End
        List<Text> list8 = new ArrayList<Text>();
        list8.add(Text.of(msgNode.get("BiomeEndLore")));

        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.END_STONE).add(Keys.ITEM_LORE, list8).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("BiomeEnd"))
                .color(TextColors.DARK_PURPLE).build())).quantity(1).build();

        // Menu principal
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of(msgNode.get("MainMenuLore")));

        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("MainMenu"))
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(4, 0)).set(item5);
        menu.query(SlotPos.of(5, 0)).set(item6);
        menu.query(SlotPos.of(6, 0)).set(item7);
        menu.query(SlotPos.of(7, 0)).set(item8);
        menu.query(SlotPos.of(8, 1)).set(item9);

        return menu;
    }
}
