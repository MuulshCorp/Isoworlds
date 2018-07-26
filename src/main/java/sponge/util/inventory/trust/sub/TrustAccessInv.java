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
package sponge.util.inventory.trust.sub;

import common.Cooldown;
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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.Main;
import sponge.location.Locations;
import sponge.util.action.StatAction;
import sponge.util.action.StorageAction;
import sponge.util.console.Logger;
import sponge.util.inventory.MainInv;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static common.Msg.msgNode;
import static sponge.Main.instance;

public class TrustAccessInv {

    private static final Main plugin = Main.instance;

    public static Inventory getInv(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.ITEM_LORE).get().get(0).toPlain());
                    String menuPlayer = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    Logger.info("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);

                    // Si joueur, on ajoute le joueur
                    if (menuPlayer.contains(msgNode.get("TrustAccessLore2"))) {
                        // Récupération UUID
                        String[] tmp = menuName.split("-IsoWorld");
                        Logger.info("NAME " + menuName);
                        Optional<User> user = StatAction.getPlayerFromUUID(UUID.fromString(tmp[0]));
                        String worldname = user.get().getUniqueId().toString() + "-IsoWorld";

                        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
                        if (StorageAction.iwInProcess(pPlayer, worldname)) {
                            return;
                        }

                        // Pull du IsoWorld
                        Task.builder().execute(new Runnable() {
                            @Override
                            public void run() {
                                // Si monde présent en dossier ?
                                // Removing iwInProcess in task
                                if (StorageAction.checkTag(pPlayer, worldname)) {
                                    // Chargement du isoworld + tp
                                    sponge.util.action.IsoworldsAction.setWorldProperties(worldname, pPlayer);
                                    Sponge.getServer().loadWorld(worldname);
                                    Locations.teleport(pPlayer, worldname);
                                    plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
                                }

                                // Supprime le lock (worldname, worldname uniquement pour les access confiance)
                                plugin.lock.remove(worldname + ";" + worldname);

                            }
                        })
                                .delay(1, TimeUnit.SECONDS)
                                .name("Pull du IsoWorld.").submit(instance);

                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));

                    } else if (menuName.contains(msgNode.get("MainMenu"))) {
                        MainInv.closeOpenMenu(pPlayer, MainInv.menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("Isoworlds: " + msgNode.get("InvTrust") + " > " + msgNode.get("TrustAccess")).color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                .build(instance);

        int i = 0;
        int j = 0;
        ResultSet trusts = TrustAction.getAccess(pPlayer.getUniqueId().toString());
        try {
            while (trusts.next()) {
                // Récupération uuid
                String[] tmp = trusts.getString(1).split("-IsoWorld");
                UUID uuid = UUID.fromString(tmp[0]);
                Optional<User> user = StatAction.getPlayerFromUUID(uuid);

                // Dont show own access
                if (user.get().getName().equals(pPlayer.getName())) {
                    continue;
                }

                // Construction du lore
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of(user.get().getUniqueId().toString()));
                Logger.info("record: " + trusts.getString(1));

                // Construction des skin itemstack
                SkullData data = Sponge.getGame().getDataManager().getManipulatorBuilder(SkullData.class).get().create();
                data.set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                ItemStack stack = Sponge.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.SKULL).itemData(data)
                        .add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("TrustAccessLore") + ": " + user.get().getName())
                                .color(TextColors.GOLD).build())).quantity(1)
                        .build();
                RepresentedPlayerData skinData = Sponge.getGame().getDataManager().getManipulatorBuilder(RepresentedPlayerData.class).get().create();
                Logger.info("USER: " + user.get().getUniqueId().toString() + user.get().getName());
                skinData.set(Keys.REPRESENTED_PLAYER, GameProfile.of(user.get().getUniqueId(), user.get().getName()));
                stack.offer(skinData);

                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(stack);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of(msgNode.get("MainMenuLore")));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("MainMenu"))
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(8, 3)).set(item2);

        return menu;
    }
}
