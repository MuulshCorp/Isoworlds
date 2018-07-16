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
package sponge.util.inventory.build;

import common.Msg;
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
import sponge.util.action.IsoWorldsAction;
import sponge.util.console.Logger;
import sponge.util.inventory.MainInv;
import sponge.util.inventory.build.sub.CreateInv;

import java.util.ArrayList;
import java.util.List;

import static sponge.MainSponge.instance;

public class BuildInv {

    public static Inventory getInv(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Création")) {
                        Logger.info("[TRACKING-IW] Clic menu CREATION: " + pPlayer.getName());
                        MainInv.closeOpenMenu(pPlayer, CreateInv.getInv(pPlayer));
                    } else if (menuName.contains("Refonte")) {
                        Logger.info("[TRACKING-IW] Clic menu REFONTE: " + pPlayer.getName());
                        MainInv.commandMenu(pPlayer, "iw r");
                        MainInv.closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Construction").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);


        // Affiche la refonte si le monde est créé, sinon affiche la création
        if (IsoWorldsAction.iwExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            List<Text> list1 = new ArrayList<Text>();
            list1.add(Text.of("Réinitialiser votre IsoWorld (choix du patern)."));
            ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Refonte")
                    .color(TextColors.GOLD).build())).quantity(1).build();
            menu.query(SlotPos.of(0, 0)).set(item1);
        } else {
            List<Text> list1 = new ArrayList<Text>();
            list1.add(Text.of("Créer votre IsoWorld."));
            ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Création")
                    .color(TextColors.GOLD).build())).quantity(1).build();
            menu.query(SlotPos.of(0, 0)).set(item1);
        }

        // Bouton retour
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retour au menu principal"));
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(8, 0)).set(item2);

        return menu;
    }
}
