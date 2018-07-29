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
package sponge.util.inventory;

import common.action.ChargeAction;
import common.action.PlayTimeAction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.location.Locations;
import sponge.util.console.Logger;
import sponge.util.inventory.biome.BiomeInv;
import sponge.util.inventory.build.BuildInv;
import sponge.util.inventory.enable.EnableInv;
import sponge.util.inventory.home.HomeInv;
import sponge.util.inventory.teleport.TeleportInv;
import sponge.util.inventory.time.TimeInv;
import sponge.util.inventory.trust.TrustInv;
import sponge.util.inventory.warp.WarpInv;
import sponge.util.inventory.weather.WeatherInv;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static common.Msg.msgNode;
import static sponge.Main.instance;

public class MainInv {

    public static Inventory menuPrincipal(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    Logger.info("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    // MENU PRINCIPAL //
                    // BIOME
                    if (menuName.equals(msgNode.get("InvBiome"))) {
                        Logger.tracking("Clic menu BIOME: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, BiomeInv.getInv(pPlayer));
                        // CONFIANCE
                    } else if (menuName.equals(msgNode.get("InvTrust"))) {
                        Logger.tracking("Clic menu CONFIANCE: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, TrustInv.getInv(pPlayer));
                        // CONSTRUCTION
                    } else if (menuName.equals(msgNode.get("InvBuild"))) {
                        Logger.tracking("Clic menu CONSTRUCTION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, BuildInv.getInv(pPlayer));
                        // MAISON
                    } else if (menuName.equals(msgNode.get("InvHome"))) {
                        Logger.tracking("Clic menu MAISON: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, HomeInv.getInv(pPlayer));
                        // METEO
                    } else if (menuName.equals(msgNode.get("InvWeather"))) {
                        Logger.tracking("Clic menu METEO: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, WeatherInv.getInv(pPlayer));
                        // ACTIVATION
                    } else if (menuName.equals(msgNode.get("InvEnable"))) {
                        Logger.tracking("Clic menu ACTIVATION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, EnableInv.getInv(pPlayer));
                        // TELEPORTATION
                    } else if (menuName.equals(msgNode.get("InvTeleport"))) {
                        Logger.tracking("Clic menu TELEPORTATION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, TeleportInv.getInv(pPlayer));
                    } else if (menuName.equals(msgNode.get("InvTime"))) {
                        Logger.tracking("Clic menu TEMPS: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, TimeInv.getInv(pPlayer));
                    } else if (menuName.equals(msgNode.get("InvWarp"))) {
                        Logger.tracking("Clic menu WARP: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, WarpInv.getInv(pPlayer));
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("Isoworlds")
                        .color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        // Récupération nombre charge
        Integer charges = ChargeAction.getCharge(pPlayer);
        Integer playtime = PlayTimeAction.getPlayTime(pPlayer.getUniqueId().toString());
        String formatedPlayTime;

        if (playtime > 60) {
            formatedPlayTime = playtime / 60 + " H " + playtime % 60 + " m";
        } else {
            formatedPlayTime = playtime + " m";
        }

        // Création item
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of(msgNode.get("InvBiomeLore")));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of(msgNode.get("InvTrustLore")));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of(msgNode.get("InvBuildLore")));
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of(msgNode.get("InvHomeLore")));
        List<Text> list5 = new ArrayList<Text>();
        list5.add(Text.of(msgNode.get("InvWeatherLore")));
        list5.add(Text.of(msgNode.get("InvWeatherLore2")));
        List<Text> list6 = new ArrayList<Text>();
        list6.add(Text.of(msgNode.get("InvEnableLore")));
        List<Text> list7 = new ArrayList<Text>();
        list7.add(Text.of(msgNode.get("InvWarpLore")));
        List<Text> list8 = new ArrayList<Text>();
        list8.add(Text.of(msgNode.get("InvTimeLore")));
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of(Text.builder(msgNode.get("InvStatChargeLore")).color(TextColors.YELLOW).append(Text.of(Text.builder(charges + " disponible(s)").color(TextColors.GREEN))).build()));
        list9.add(Text.of(Text.builder(msgNode.get("InvStatPlayTimeLore")).color(TextColors.YELLOW).append(Text.of(Text.builder(formatedPlayTime).color(TextColors.GREEN))).build()));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.DIAMOND_PICKAXE).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvBuild"))
                .color(TextColors.GRAY).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.BED).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvHome"))
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.SKULL_TYPE, SkullTypes.PLAYER).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvTrust"))
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.LEAVES).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvBiome"))
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.CLOCK).add(Keys.ITEM_LORE, list8).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvTime"))
                .color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.DOUBLE_PLANT).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvWeather"))
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.COMPASS).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvWarp"))
                .color(TextColors.DARK_GREEN).build())).quantity(1).build();
        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.LEVER).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder(msgNode.get("InvStat"))
                .color(TextColors.AQUA).build())).quantity(1).build();

        //ItemStack item7 = ItemStack.builder().itemType(ItemTypes.LEVER).add(Keys.ITEM_LORE, list6).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Activation")
        //        .color(TextColors.RED).build())).quantity(1).build();
        //ItemStack item8 = ItemStack.builder().itemType(ItemTypes.DIAMOND_BOOTS).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Téléportation")
        //        .color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();


        // Placement item dans le menu
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(4, 0)).set(item5);
        menu.query(SlotPos.of(5, 0)).set(item6);
        menu.query(SlotPos.of(6, 0)).set(item7);
        menu.query(SlotPos.of(8, 0)).set(item9);

        // STAFF
        //if (pPlayer.hasPermission("isworlds.menu.activation")) {
        //    menu.query(SlotPos.of(6, 0)).set(item7);
        //}
        //if (pPlayer.hasPermission("Isoworlds.menu.teleportation")) {
        //    menu.query(SlotPos.of(7, 0)).set(item8);
        //}
        //menu.query(SlotPos.of(7, 0)).set(item8);

        return menu;
    }

    public static void closeOpenMenu(Player pPlayer, Inventory inv) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                pPlayer.closeInventory(Cause.of(NamedCause.simulated(pPlayer)));
                pPlayer.openInventory(inv, Cause.of(NamedCause.simulated(pPlayer)));
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Ferme l'inventaire d'un joueur et en ouvre un autre.").submit(instance);
    }

    public static void closeMenu(Player pPlayer) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                pPlayer.closeInventory(Cause.of(NamedCause.simulated(pPlayer)));
                pPlayer.openInventory(menuPrincipal(pPlayer), Cause.of(NamedCause.simulated(pPlayer)));
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Ferme l'inventaire d'un joueur.").submit(instance);
    }

    public static void commandMenu(Player pPlayer, String cmd) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                Sponge.getCommandManager().process(pPlayer, cmd);
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Execute une commande pour le joueur.").submit(instance);
    }

    public static void teleportMenu(Player pPlayer, String cmd) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                Logger.info("TP CMD: " + cmd);
                Locations.teleport(pPlayer, cmd);
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Téléporte le joueur dans un Isoworld.").submit(instance);
    }
}
