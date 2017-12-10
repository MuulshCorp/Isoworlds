package bukkit.Utils;

/**
 * Created by Edwin on 23/11/2017.
 */

import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import common.Msg;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.SkullMeta;

import static bukkit.Utils.IsoworldsUtils.setWorldProperties;

public class IsoWorldsInventory implements Listener {

    private String name;
    private int size;
    private onClick click;
    List<String> viewing = new ArrayList<String>();
    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

    private ItemStack[] items;

    // MENU PRINCIPAL
    public static IsoWorldsInventory MenuPrincipal(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory("IsoWorlds", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                // MENU PRINCIPAL //
                // BIOME
                String menuName = row.getRowItem(slot).getItemMeta().getDisplayName();
                if (menuName.equals("Biome")) {
                    IsoworldsUtils.cm("PLAYER MENU 3");
                    getMenuBiome(pPlayer).open(pPlayer);
                    return true;
                    // CONFIANCE
                } else if (menuName.equals("Confiance")) {
                    IsoworldsUtils.cm("PLAYER MENU 3");
                    getMenuConfiance(pPlayer).open(pPlayer);
                    // CONSTRUCTION
                } /*else if (row.getRowItem(slot).getType().name().equals(ChatColor.GRAY + "Construction")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuConstruction().open(p);
                        // MAISON
                    } else if (row.getRowItem(slot).getType().name().equals(ChatColor.BLUE + "Maison")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuMaison().open(p);
                        // METEO
                    } else if (row.getRowItem(slot).getType().name().equals(ChatColor.YELLOW + "Météo")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuMeteo().open(p);
                        // ACTIVATION
                    } else if (row.getRowItem(slot).getType().name().equals(ChatColor.RED + "Activation")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuActivation().open(p);
                        // TELEPORTATION
                    } else if (row.getRowItem(slot).getType().name().equals(ChatColor.LIGHT_PURPLE + "Téléportation")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuTeleportation().open(p);
                    } else if (row.getRowItem(slot).getType().name().equals(ChatColor.RED + "Menu principal")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        getMenuPrincipal().open(p);
                    }*/
                Bukkit.broadcastMessage(row.getRowItem(slot).getType().name());

                return true;
            }
        });

        // Construction des skin itemstack
        ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta sm = (SkullMeta) item1.getItemMeta();
        sm.setOwner("Steve");
        item1.setItemMeta(sm);

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.DIAMOND_PICKAXE), "Construction");
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.BED), "Maison");
        menu.addButton(menu.getRow(0), 2, item1, "Confiance");
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.LEAVES), "Biome");
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.WATCH), "Temps");
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.DOUBLE_PLANT), "Météo");
        menu.addButton(menu.getRow(0), 6, new ItemStack(Material.LEVER), "Activation");
        menu.addButton(menu.getRow(0), 7, new ItemStack(Material.DIAMOND_BOOTS), "Téléportation");

        return menu;
    }

    // MENU BIOME
    public static IsoWorldsInventory getMenuBiome(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Biome", 2, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getDisplayName();
                if (menuName.contains("Plaines")) {
                    p.performCommand("iw biome plaines");
                    p.closeInventory();
                } else if (menuName.contains("Désert")) {
                    p.performCommand("iw biome desert");
                    p.closeInventory();
                } else if (menuName.contains("Marais")) {
                    p.performCommand("iw biome marais");
                    p.closeInventory();
                } else if (menuName.contains("Océan")) {
                    p.performCommand("iw biome océan");
                    p.closeInventory();
                } else if (menuName.contains("Champignon")) {
                    p.performCommand("iw biome champignon");
                    p.closeInventory();
                } else if (menuName.contains("Jungle")) {
                    p.performCommand("iw biome jungle");
                    p.closeInventory();
                } else if (menuName.contains(ChatColor.DARK_RED + "Enfer")) {
                    p.performCommand("iw biome enfer");
                    p.closeInventory();
                } else if (menuName.contains("(INDISPONIBLE) End")) {
                    p.closeInventory();
                    // INDISPONIBLE
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });
        // Plaines
        String[] list1 = new String[]{"Un biome relativement plat avec des collines", "vallonnées et une grande quantité de fleurs."};

        // Désert
        String[] list2 = new String[]{"Un biome constitué principalement de sable", "de cactus et de canne à sucre."};

        // Marais
        String[] list3 = new String[]{"Un biome avec des nombreuses étendues", "d'eau."};

        // Océan
        String[] list4 = new String[]{"Un biome avec de vastes étendues d'eau."};

        // Champignon
        String[] list5 = new String[]{"Un biome assez rare, les monstres", "n'y apparaîssent pas."};

        // Jungle
        String[] list6 = new String[]{"Un biome avec des arbres imposants", "et une grosse quantité de bois."};

        // Enfer
        String[] list7 = new String[]{"Un biome qui constitue le nether."};

        // End
        String[] list8 = new String[]{"Indisponible sur la version 1.7.10, désolé ! =D"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + "Plaines", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.DIAMOND_PICKAXE, 1), ChatColor.YELLOW + "Désert", list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.CLAY, 1), ChatColor.GRAY + "Marais", list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), ChatColor.BLUE + "Océan", list4);
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.RED_MUSHROOM, 1), ChatColor.RED + "Champignon", list5);
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.SAPLING, 1), ChatColor.DARK_GREEN + "Jungle", list6);
        menu.addButton(menu.getRow(0), 6, new ItemStack(Material.NETHERRACK, 1), ChatColor.DARK_RED + "Enfer", list7);
        menu.addButton(menu.getRow(0), 7, new ItemStack(Material.ENDER_STONE, 1), ChatColor.DARK_PURPLE + "(INDISPONIBLE) End", list8);
        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU CONFIANCE
    public static IsoWorldsInventory getMenuConfiance(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance", 2, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getDisplayName();
                if (menuName.contains("Ajouter")) {
                    getMenuConfianceAdd(pPlayer).open(pPlayer);
                } else if (menuName.contains("Retirer")) {
                    getMenuConfianceRemove(pPlayer).open(pPlayer);
                    //} else if (menuName.contains("Mes accès")) {
                    //    getMenuConfianceAccess(pPlayer).open(pPlayer);
                    //} else if (menuName.contains("Menu principal")) {
                    //    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });

        // Lore
        String[] list1 = new String[]{"Autoriser l'accès à votre IsoWorld."};
        String[] list2 = new String[]{"Retirer l'accès à votre IsoWorld."};
        String[] list3 = new String[]{"Retour au menu principal"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GREEN + "Ajouter", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Retirer", list1);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData()), ChatColor.RED + "Mes accès", list1);

        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU CONFIANCE ADD
    public static IsoWorldsInventory getMenuConfianceAdd(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Ajouter", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getLore().toString();
                String menuPlayer = row.getRowItem(slot).getItemMeta().getDisplayName();
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw confiance " + ChatColor.stripColor(menuPlayer));
                    IsoworldsUtils.cm("Confiance:" + menuPlayer);
                    p.closeInventory();
                } else if (menuName.contains("menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });

        int i = 0;
        int j = 0;
        boolean check = false;
        ResultSet trusts = IsoworldsUtils.getTrusts(pPlayer, Msg.keys.SQL);
        List<String> players = new ArrayList<String>();

        // Récupération joueurs trust dans un tableau
        try {
            while (trusts.next()) {
                String tmp = trusts.getString(1);
                UUID uuid = UUID.fromString(tmp);

                // Getting uuidowner
                String pname;
                if (Bukkit.getServer().getPlayer(uuid) == null) {
                    pname = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                } else {
                    pname = Bukkit.getServer().getPlayer(uuid).getDisplayName();
                }
                players.add(pname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Boucle des joueurs en ligne, si dans tableau on continue
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (players.contains(p.getDisplayName())) {
                continue;
            }

            // Dont show own access
            if (p.getName().equals(pPlayer.getDisplayName())) {
                continue;
            }

            // Construction du lore
            String list1 = "Joueur";

            // Construction des skin itemstack
            ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta sm = (SkullMeta) item1.getItemMeta();
            sm.setOwner(p.getDisplayName());
            item1.setItemMeta(sm);

            if (i >= 8) {
                j = j++;
            }
            menu.addButton(menu.getRow(j), i, item1, ChatColor.GREEN + p.getName(), list1);
            i++;


        }

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), "Menu principal", "Retour au menu principal");

        return menu;
    }

    // CONFIANCE REMOVE
    public static IsoWorldsInventory getMenuConfianceRemove(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Retirer", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getLore().toString();
                String menuPlayer = row.getRowItem(slot).getItemMeta().getDisplayName();
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw retirer " + ChatColor.stripColor(menuPlayer));
                    p.closeInventory();
                } else if (menuName.contains("menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });


        int i = 0;
        int j = 0;
        ResultSet trusts = IsoworldsUtils.getTrusts(pPlayer, Msg.keys.SQL);
        try {
            while (trusts.next()) {
                // Récupération du nom du joueur
                String tmp = trusts.getString(1);
                IsoworldsUtils.cm("name = " + tmp);
                UUID uuid = UUID.fromString(tmp);
                IsoworldsUtils.cm("uuid = " + uuid);

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

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), "Menu principal", "Retour au menu principal");

        return menu;
    }

    // CONFIANCE ACCESS
    public static IsoWorldsInventory getMenuConfianceAccess(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Accès", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getLore().toString();
                String menuPlayer = row.getRowItem(slot).getItemMeta().getDisplayName();
                // Si joueur, on ajoute le joueur
                if (menuPlayer.contains("IsoWorld Accessible")) {
                    // Récupération UUID
                    String[] tmp = menuName.split("-IsoWorld");
                    IsoworldsUtils.cm("NAME " + menuName);
                    String pname;
                    if (Bukkit.getServer().getPlayer(tmp[0]) == null) {
                        pname = Bukkit.getServer().getOfflinePlayer(tmp[0]).getName();
                    } else {
                        pname = Bukkit.getServer().getPlayer(tmp[0]).getDisplayName();
                    }

                    String worldname = pname + "-IsoWorld";

                    // Pull du IsoWorld

                    // Si monde présent en dossier ?
                    if (IsoworldsUtils.checkTag(pPlayer, worldname)) {
                        // Chargement du isoworld + tp
                        setWorldProperties(pname + "-IsoWorld", pPlayer);
                        Bukkit.getServer().createWorld(new WorldCreator(pname + "-IsoWorld"));
                        IsoworldsLocations.teleport(pPlayer, pname + "-IsoWorld");
                        //plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
                    }
                    p.closeInventory();

                } else if (menuName.contains("menu principal")) {
                    p.closeInventory();
                }

                return true;
            }
        });

        int i = 0;
        int j = 0;
        ResultSet trusts = IsoworldsUtils.getAccess(pPlayer, Msg.keys.SQL);
        try {
            while (trusts.next()) {
                // Récupération uuid
                String[] tmp = trusts.getString(1).split("-IsoWorld");
                UUID uuid = UUID.fromString(tmp[0]);
                String pname;
                if (Bukkit.getServer().getPlayer(tmp[0]) == null) {
                    pname = Bukkit.getServer().getOfflinePlayer(tmp[0]).getName();
                } else {
                    pname = Bukkit.getServer().getPlayer(tmp[0]).getDisplayName();
                }

                // Dont show own access
                if (pname.equals(pPlayer.getName())) {
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

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), "Menu principal", "Retour au menu principal");

        return menu;
    }


    public IsoWorldsInventory(String name, int size, onClick click) {
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

    public IsoWorldsInventory open(Player p) {
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

    public IsoWorldsInventory close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
        return this;
    }

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

    public IsoWorldsInventory addButton(Row row, int position, ItemStack item, String name, String... lore) {
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
        public abstract boolean click(Player clicker, IsoWorldsInventory menu, Row row, int slot, ItemStack item);
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



/*  public class IsoWorldsInventory implements Listener {

    private static String name;
    private static int size;
    private static OptionClickEventHandler handler;
    private static Plugin plugin;

    private static String[] optionNames;
    private static ItemStack[] optionIcons;

    public IsoWorldsInventory(String name, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public IsoWorldsInventory setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(name)) {
            IsoworldsUtils.cm("PLAYER MENU 1:" + event.getWhoClicked().getName());
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                Plugin plugin = this.plugin;
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot]);
                handler.onOptionClick(e);
                if (e.willClose()) {
                    final Player p = (Player) event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            p.closeInventory();
                            IsoworldsUtils.cm("PLAYER MENU 2: " + clicked.getItemMeta().getDisplayName());

                            // MENU PRINCIPAL //
                            // BIOME
                            if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Biome")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuBiome().open(p);
                                // CONFIANCE
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Confiance")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuConfiance().open(p);
                                // CONSTRUCTION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Construction")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuConstruction().open(p);
                                // MAISON
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Maison")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuMaison().open(p);
                                // METEO
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Météo")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuMeteo().open(p);
                                // ACTIVATION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Activation")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuActivation().open(p);
                                // TELEPORTATION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Téléportation")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuTeleportation().open(p);
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Menu principal")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuPrincipal().open(p);
                            }

                            // SOUS-MENU //
                            // BIOMES
                            else if (event.getInventory().getName().contains("Biome")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Plaines")) {
                                    p.performCommand("iw biome plaines");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Désert")) {
                                    p.performCommand("iw biome desert");
                                }
                                // CONFIANCE
                            } else if (event.getInventory().getName().contains("Confiance")) {

                                // CONSTRUCTION
                            } else if (event.getInventory().getName().contains("Construction")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Création")) {
                                    p.performCommand("iw c");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Refonte")) {
                                    p.performCommand("iw r");
                                }
                                // MAISON
                            } else if (event.getInventory().getName().contains("Maison")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Maison")) {
                                    p.performCommand("iw h");
                                }
                                // METEO
                            } else if (event.getInventory().getName().contains("Météo")) {

                                //type
                                String mtype = "";
                                if (clicked.getItemMeta().getDisplayName().contains("Soleil")) {
                                    mtype = "soleil";
                                } else if (clicked.getItemMeta().getDisplayName().contains("Pluie")) {
                                    mtype = "pluie";
                                }
                                //temps
                                // 10 MINUTES
                                if (clicked.getItemMeta().getDisplayName().contains("10 minutes")) {
                                    p.performCommand("iw meteo " + mtype + " 12000");
                                    // 30 MINUTES
                                } else if (clicked.getItemMeta().getDisplayName().contains("30 minutes")) {
                                    p.performCommand("iw meteo " + mtype + " 36000");
                                    // 1 HEURE
                                } else if (clicked.getItemMeta().getDisplayName().contains("1 heure")) {
                                    p.performCommand("iw meteo " + mtype + " 72000");
                                }
                                // ACTIVATION
                            } else if (event.getInventory().getName().contains("Activation")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Activer")) {
                                    p.performCommand("iw on");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Désactiver")) {
                                    p.performCommand("iw off");
                                }
                                // TELEPORTATION
                            } else if (event.getInventory().getName().contains("Téléportation")) {
                                if (clicked.getItemMeta().getDisplayName().contains("-IsoWorld")) {
                                    p.performCommand("iw teleport " + clicked.getItemMeta().getDisplayName());
                                }
                                // TIME
                            } else if (event.getInventory().getName().contains("Temps")) {
                                //Jour
                                if (clicked.getItemMeta().getDisplayName().contains("Jour")) {
                                    p.performCommand("iw t " + clicked.getItemMeta().getDisplayName());
                                    //Nuit
                                } else if (clicked.getItemMeta().getDisplayName().contains("Nuit")) {
                                    p.performCommand("iw t " + clicked.getItemMeta().getDisplayName());
                                }
                            }


                        }
                    }, 1);
                }
                if (e.willDestroy()) {
                    destroy();
                }
            }
        }
    }

    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event);
    }

    public class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;

        public OptionClickEvent(Player player, int position, String name) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = true;
        }

        public Player getPlayer() {
            return player;
        }

        public int getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        public boolean willClose() {
            return close;
        }

        public boolean willDestroy() {
            return destroy;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    //MENU PRINCIPAL
    public static IsoWorldsInventory getMenuPrincipal() {
        IsoWorldsInventory menuPrincipal = new IsoWorldsInventory(ChatColor.GOLD + "IsoWorlds: Menu principal", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.LEAVES, 1), ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks")
                .setOption(1, new ItemStack(Material.SKULL_ITEM, 1), ChatColor.GREEN + "Confiance", "Gérez qui peut avoir accès à votre IsoWorld")
                .setOption(2, new ItemStack(Material.DIAMOND_PICKAXE, 1), ChatColor.GRAY + "Construction", "Créez ou refondez votre IsoWorld")
                .setOption(3, new ItemStack(Material.BED, 1), ChatColor.BLUE + "Maison", "Rendez-vous sur votre IsoWorld")
                .setOption(4, new ItemStack(Material.DOUBLE_PLANT, 1), ChatColor.YELLOW + "Météo", "Gérez la pluie et le beau temps de votre IsoWorld")
                .setOption(5, new ItemStack(Material.LEVER, 1), ChatColor.RED + "Activation", "Chargez-Déchargez votre IsoWorld")
                .setOption(6, new ItemStack(Material.DIAMOND_BOOTS, 1), ChatColor.LIGHT_PURPLE + "Téléportation", "Téléportez vous sur un IsoWorld [STAFF]")
                .setOption(7, new ItemStack(Material.WATCH, 1), ChatColor.LIGHT_PURPLE + "Temps", "Gérez l'heure de votre IsoWorld");
        return menuPrincipal;
    }

    // BIOME
    public static IsoWorldsInventory getMenuBiome() {
        IsoWorldsInventory menuBiome = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Biome", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                IsoworldsUtils.cm("TEST 1");
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Plaines", "Un biome relativement plat avec des collines || vallonnées et une grande quantité de fleurs")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(2, new ItemStack(Material.CLAY, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(3, new ItemStack(Material.WOOL, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(4, new ItemStack(Material.RED_MUSHROOM, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(5, new ItemStack(Material.SAPLING, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(6, new ItemStack(Material.NETHERRACK, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")
                .setOption(7, new ItemStack(Material.ENDER_STONE, 1), ChatColor.RED + "(INDISPONIBLE) Void", "Indisponible sur la version 1.7.10, désolé ! =D")

                // RETOUR
                .setOption(8, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuBiome;
    }

    // CONFIANCE
    public static IsoWorldsInventory getMenuConfiance() {
        IsoWorldsInventory menuConfiance = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Confiance", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + "Ajouter", "Autoriser l'accès à votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.RED + "Retirer", "Retirer l'accès à votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuConfiance;
    }

    // CONSTRUCTION
    public static IsoWorldsInventory getMenuConstruction() {
        IsoWorldsInventory menuConstruction = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Construction", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Création", "Créer votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Refonte", "Réinitialiser votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuConstruction;
    }

    // MAISON
    public static IsoWorldsInventory getMenuMaison() {
        IsoWorldsInventory menuMaison = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Maison", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Maison", "Vous rendre sur votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuMaison;
    }

    // METEO
    public static IsoWorldsInventory getMenuMeteo() {
        IsoWorldsInventory menuMeteo = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Météo", 18, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [10 minutes]", "Le temps devient paisible et ensoleillé.")
                .setOption(1, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [30 minutes]", "Le temps devient paisible et ensoleillé.")
                .setOption(2, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [1 heure]", "Le temps devient paisible et ensoleillé.")

                .setOption(9, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [10 minutes]", "Vos terres boivent l'eau de pluie.")
                .setOption(10, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [30 minutes]", "Vos terres boivent l'eau de pluie.")
                .setOption(11, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [1 heure]", "Vos terres boivent l'eau de pluie.")

                // RETOUR
                .setOption(17, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuMeteo;
    }

    // ACTIVATION
    public static IsoWorldsInventory getMenuActivation() {
        IsoWorldsInventory menuActivation = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Activation", 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + "Activer", "Charge votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.RED + "Désactiver", "Décharge votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuActivation;
    }

    // TELEPORTATION
    public static IsoWorldsInventory getMenuTeleportation() {
        IsoWorldsInventory menuTeleportation = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Téléportation", 27, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance);
        // Boucle pour afficher le nom réel + nom pseudo du monde
        int i = 0;
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().contains("-IsoWorld")) {
                String[] split = w.getName().split("-IsoWorld");
                UUID uuid = UUID.fromString(split[0]);
                String name = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                menuTeleportation.setOption(i, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + w.getName(), name);
                i++;
            }
        }

        // RETOUR
        menuTeleportation.setOption(26, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menuTeleportation;
    }

    // TIME
    public static IsoWorldsInventory getMenuTime() {
        IsoWorldsInventory menuTime = new IsoWorldsInventory(ChatColor.RED + "IsoWorlds: Temps", 18, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Jour", "Le jour se lève")

                .setOption(9, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Nuit", "La nuit tombe")

                // RETOUR
                .setOption(17, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuTime;
    }

}*/
