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
package sponge.util.inventory.trust.sub;

import common.action.TrustAction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.util.action.StatAction;
import sponge.util.console.Logger;
import sponge.util.inventory.MainInv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static sponge.Main.instance;

public class TrustAddInv {

        public static Inventory getInv(Player pPlayer) {

            Inventory menu = Inventory.builder()
                    .of(InventoryArchetypes.CHEST)
                    .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                        // Code event
                        String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                                .get(0).getOriginal().get(Keys.ITEM_LORE).get().toString());
                        String menuPlayer = String.valueOf(clickInventoryEvent.getTransactions()
                                .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                        Logger.info("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                        clickInventoryEvent.setCancelled(true);
                        // Si joueur, on ajouter le joueur
                        if (menuName.contains("Joueur")) {
                            MainInv.commandMenu(pPlayer, "iw confiance " + menuPlayer);
                            MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                        } else if (menuName.contains("Menu principal")) {
                            MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                        }
                    })
                    .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Ajouter").color(TextColors.BLUE).build())))
                    .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                    .build(instance);

            int i = 0;
            int j = 0;
            ResultSet trusts = TrustAction.getTrusts(pPlayer.getUniqueId().toString());
            List<String> players = new ArrayList<String>();

            // Récupération joueurs trust dans un tableau
            try {
                while (trusts.next()) {
                    String tmp = trusts.getString(1);
                    Logger.info("name = " + tmp);
                    UUID uuid = UUID.fromString(tmp);
                    Logger.info("uuid = " + uuid);
                    Optional<User> user = StatAction.getPlayerFromUUID(uuid);
                    players.add(user.get().getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Boucle des joueurs en ligne, si dans tableau on continue
            for (Player p : Sponge.getServer().getOnlinePlayers()) {
                if (players.contains(p.getName())) {
                    continue;
                }

                // Dont show own access
                if (p.getName().equals(pPlayer.getName())) {
                    continue;
                }

                // Construction du lore
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of("Joueur"));

                // Construction des skin itemstack
                SkullData data = Sponge.getGame().getDataManager().getManipulatorBuilder(SkullData.class).get().create();
                data.set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                ItemStack stack = Sponge.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.SKULL).itemData(data)
                        .add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(p.getName())
                                .color(TextColors.GOLD).build())).quantity(1)
                        .build();
                RepresentedPlayerData skinData = Sponge.getGame().getDataManager().getManipulatorBuilder(RepresentedPlayerData.class).get().create();
                skinData.set(Keys.REPRESENTED_PLAYER, GameProfile.of(p.getUniqueId(), p.getName()));
                stack.offer(skinData);

                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(stack);
                i++;
            }

            List<Text> list2 = new ArrayList<Text>();
            list2.add(Text.of("Menu principal"));

            ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                    .color(TextColors.RED).build())).quantity(1).build();
            menu.query(SlotPos.of(8, 3)).set(item2);

            return menu;
        }
}
