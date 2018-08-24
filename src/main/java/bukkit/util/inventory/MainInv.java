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
package bukkit.util.inventory;

import bukkit.util.console.Logger;
import bukkit.util.inventory.biome.BiomeInv;
import bukkit.util.inventory.build.BuildInv;
import bukkit.util.inventory.enable.EnableInv;
import bukkit.util.inventory.home.HomeInv;
import bukkit.util.inventory.teleport.TeleportInv;
import bukkit.util.inventory.time.TimeInv;
import bukkit.util.inventory.trust.TrustInv;
import bukkit.util.inventory.warp.WarpInv;
import bukkit.util.inventory.weather.WeatherInv;
import common.IsoChat;
import common.action.ChargeAction;
import common.action.PlayTimeAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static common.Msg.msgNode;

public class MainInv implements Listener {

    private String name;
    private int size;
    private MainInv.onClick click;
    private ItemStack[] items;
    List<String> viewing = new ArrayList<String>();

    public static MainInv MenuPrincipal(Player pPlayer) {
        MainInv menu = new MainInv(ChatColor.BLUE + "Isoworlds", 1, new onClick() {
            @Override
            public boolean click(Player p, MainInv menu, Row row, int slot, ItemStack item) {
                // MENU PRINCIPAL //
                // BIOME
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.equals(msgNode.get("InvBiome"))) {
                    Logger.tracking("Clic menu BIOME: " + p.getName());
                    BiomeInv.getInv(pPlayer).open(pPlayer);
                    // CONFIANCE
                } else if (menuName.equals(msgNode.get("InvTrust"))) {
                    Logger.tracking("Clic menu CONFIANCE: " + p.getName());
                    TrustInv.getInv(pPlayer).open(pPlayer);
                    // CONSTRUCTION
                } else if (menuName.equals(msgNode.get("InvBuild"))) {
                    Logger.tracking("Clic menu CONSTRUCTION: " + p.getName());
                    BuildInv.getInv(pPlayer).open(pPlayer);
                    // MAISON
                } else if (menuName.equals(msgNode.get("InvHome"))) {
                    Logger.tracking("Clic menu MAISON: " + p.getName());
                    HomeInv.getInv(pPlayer).open(pPlayer);
                    // METEO
                } else if (menuName.equals(msgNode.get("InvWeather"))) {
                    Logger.tracking("Clic menu METEO: " + p.getName());
                    WeatherInv.getInv(pPlayer).open(pPlayer);
                    // ACTIVATION
                } else if (menuName.equals(msgNode.get("InvEnabe"))) {
                    Logger.tracking("Clic menu ACTIVATION: " + p.getName());
                    EnableInv.getInv(pPlayer).open(pPlayer);
                    // TELEPORTATION
                } else if (menuName.equals(msgNode.get("InvTeleport"))) {
                    Logger.tracking("Clic menu TELEPORTATION: " + p.getName());
                    TeleportInv.getInv(pPlayer).open(pPlayer);
                } else if (menuName.equals(msgNode.get("InvTime"))) {
                    Logger.tracking("Clic menu TEMPS: " + p.getName());
                    TimeInv.getInv(pPlayer).open(pPlayer);
                } else if (menuName.equals(msgNode.get("InvWarp"))) {
                    Logger.tracking("Clic menu WARP: " + p.getName());
                    WarpInv.getInv(pPlayer).open(pPlayer);
                } else if (menuName.equals("Toogle IsoChat")) {
                    IsoChat.toggle(pPlayer.getUniqueId());
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });

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
        String[] list1 = new String[]{msgNode.get("InvBuildLore")};
        String[] list2 = new String[]{msgNode.get("InvHomeLore")};
        String[] list3 = new String[]{msgNode.get("InvTrustLore")};
        String[] list4 = new String[]{msgNode.get("InvBiomeLore")};
        String[] list5 = new String[]{msgNode.get("InvTimeLore")};
        String[] list6 = new String[]{msgNode.get("InvWeatherLore"), msgNode.get("InvWeatherLore2")};
        String[] list7 = new String[]{ChatColor.YELLOW + msgNode.get("InvStatChargeLore") + ChatColor.GREEN + charges, ChatColor.YELLOW + msgNode.get("InvStatPlayTimeLore") + ChatColor.GREEN + formatedPlayTime};
        String[] list8 = new String[]{msgNode.get("InvWarpLore")};
        String[] list9 = new String[]{IsoChat.isActivated(pPlayer.getUniqueId()) ? ChatColor.GREEN + msgNode.get("InvIsochatEnabled") : ChatColor.RED + msgNode.get("InvIsochatDisabled")};
        //String[] list7 = new String[]{"Chargez-Déchargez votre Isoworld"};
        //String[] list8 = new String[]{"Téléportez vous sur un Isoworld [STAFF]"};

        // Construction des skin itemstack
        ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta sm = (SkullMeta) item1.getItemMeta();
        sm.setOwner("Steve");
        item1.setItemMeta(sm);

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.DIAMOND_PICKAXE), ChatColor.GRAY + msgNode.get("InvBuild"), list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.BED), ChatColor.BLUE + msgNode.get("InvHome"), list2);
        menu.addButton(menu.getRow(0), 2, item1, ChatColor.GREEN + msgNode.get("InvTrust"), list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.LEAVES), ChatColor.GOLD + msgNode.get("InvBiome"), list4);
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.WATCH), ChatColor.LIGHT_PURPLE + msgNode.get("InvTime"), list5);
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.DOUBLE_PLANT), ChatColor.YELLOW + msgNode.get("InvWeather"), list6);
        menu.addButton(menu.getRow(0), 6, new ItemStack(Material.COMPASS), ChatColor.DARK_GREEN + msgNode.get("InvWarp"), list8);
        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.LEVER), ChatColor.AQUA + msgNode.get("InvStat"), list7);
        menu.addButton(menu.getRow(1), 0, new ItemStack(Material.SIGN), ChatColor.WHITE + msgNode.get("InvIsochat"), list9);
        //menu.addButton(menu.getRow(0), 6, new ItemStack(Material.LEVER), ChatColor.RED + "Activation", list7);
        //menu.addButton(menu.getRow(0), 7, new ItemStack(Material.DIAMOND_BOOTS), ChatColor.LIGHT_PURPLE + "Téléportation", list8);

        return menu;
    }

    public MainInv(String name, int size, onClick click) {
        this.name = name;
        this.size = size * 9;
        items = new ItemStack[this.size];
        this.click = click;
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : this.getViewers())
            close(p);
    }

    public MainInv open(Player p) {
        p.openInventory(getInventory(p));
        viewing.add(p.getName());
        return this;
    }

    private Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, size, name);
        for (int i = 0; i < items.length; i++)
            if (items[i] != null)
                inv.setItem(i, items[i]);
        return inv;
    }

    public MainInv close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
        return this;
    }

    @SuppressWarnings("deprecation")
    public List<Player> getViewers() {
        List<Player> viewers = new ArrayList<Player>();
        for (String s : viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewing.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            Row row = getRowFromSlot(event.getSlot());
            if (!click.click(p, this, row, event.getSlot() - row.getRow() * 9, event.getCurrentItem()))
                close(p);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getName()))
            viewing.remove(event.getPlayer().getName());
    }

    public MainInv addButton(Row row, int position, ItemStack item, String name, String... lore) {
        items[row.getRow() * 9 + position] = getItem(item, name, lore);
        return this;
    }

    public Row getRowFromSlot(int slot) {
        return new Row(slot / 9, items);
    }

    public Row getRow(int row) {
        return new Row(row, items);
    }

    public interface onClick {
        public abstract boolean click(Player clicker, MainInv menu, Row row, int slot, ItemStack item);
    }

    public class Row {
        private ItemStack[] rowItems = new ItemStack[9];
        int row;

        public Row(int row, ItemStack[] items) {
            this.row = row;
            int j = 0;
            for (int i = (row * 9); i < (row * 9) + 9; i++) {
                rowItems[j] = items[i];
                j++;
            }
        }

        public ItemStack[] getRowItems() {
            return rowItems;
        }

        public ItemStack getRowItem(int item) {
            return rowItems[item] == null ? new ItemStack(Material.AIR) : rowItems[item];
        }

        public int getRow() {
            return row;
        }
    }

    private ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
}