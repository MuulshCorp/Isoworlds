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
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                // MENU PRINCIPAL //
                // BIOME
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.equals("Biome")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu BIOME: " + p.getName());
                    getMenuBiome(pPlayer).open(pPlayer);
                    // CONFIANCE
                } else if (menuName.equals("Confiance")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu CONFIANCE: " + p.getName());
                    getMenuConfiance(pPlayer).open(pPlayer);
                    // CONSTRUCTION
                } else if (menuName.equals("Construction")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu CONSTRUCTION: " + p.getName());
                    getMenuConstruction(pPlayer).open(pPlayer);
                    // MAISON
                } else if (menuName.equals("Maison")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu MAISON: " + p.getName());
                    getMenuMaison(pPlayer).open(pPlayer);
                    // METEO
                } else if (menuName.equals("Météo")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu METEO: " + p.getName());
                    getMenuMeteo(pPlayer).open(pPlayer);
                    // ACTIVATION
                } else if (menuName.equals("Activation")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu ACTIVATION: " + p.getName());
                    getMenuActivation(pPlayer).open(pPlayer);
                    // TELEPORTATION
                } else if (menuName.equals("Téléportation")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu TELEPORTATION: " + p.getName());
                    getMenuTeleportation(pPlayer).open(pPlayer);
                } else if (menuName.equals("Temps")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu TEMPS: " + p.getName());
                    getMenuTemps(pPlayer).open(pPlayer);
                } else if (menuName.equals("Menu principal")) {
                    IsoworldsUtils.cm("[TRACKING-IW] Clic menu PRINCIPAL: " + p.getName());
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });

        // Récupération nombre charge
        Integer charges = IsoworldsUtils.getCharge(pPlayer, Msg.keys.SQL);

        // Création item
        String[] list1 = new String[]{"Créez ou refondez votre IsoWorld"};
        String[] list2 = new String[]{"Rendez-vous sur votre IsoWorld"};
        String[] list3 = new String[]{"Gérez qui peut avoir accès à votre IsoWorld"};
        String[] list4 = new String[]{"Gérez le biome de vos chunks"};
        String[] list5 = new String[]{"Gérez l'heure de votre IsoWorld"};
        String[] list6 = new String[]{"Gérez la pluie et le beau temps", "de votre IsoWorld"};
        String[] list7 = new String[]{"[" + charges + "]" + " charge(s) disponible(s)"};
        //String[] list7 = new String[]{"Chargez-Déchargez votre IsoWorld"};
        //String[] list8 = new String[]{"Téléportez vous sur un IsoWorld [STAFF]"};

        // Construction des skin itemstack
        ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta sm = (SkullMeta) item1.getItemMeta();
        sm.setOwner("Steve");
        item1.setItemMeta(sm);

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.DIAMOND_PICKAXE), ChatColor.GRAY + "Construction", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.BED), ChatColor.BLUE + "Maison", list2);
        menu.addButton(menu.getRow(0), 2, item1, ChatColor.GREEN + "Confiance", list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.LEAVES), ChatColor.GOLD + "Biome", list4);
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.WATCH), ChatColor.LIGHT_PURPLE + "Temps", list5);
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.DOUBLE_PLANT), ChatColor.YELLOW + "Météo", list6);
        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.LEVER), ChatColor.AQUA + "Charges", list7);
        //menu.addButton(menu.getRow(0), 6, new ItemStack(Material.LEVER), ChatColor.RED + "Activation", list7);
        //menu.addButton(menu.getRow(0), 7, new ItemStack(Material.DIAMOND_BOOTS), ChatColor.LIGHT_PURPLE + "Téléportation", list8);

        return menu;
    }

    // MENU BIOME
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuBiome(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Biome", 2, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
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
                } else if (menuName.contains("Enfer")) {
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
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.SAND, 1), ChatColor.YELLOW + "Désert", list2);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.CLAY, 1), ChatColor.GRAY + "Marais", list3);
        menu.addButton(menu.getRow(0), 3, new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), ChatColor.BLUE + "Océan", list4);
        menu.addButton(menu.getRow(0), 4, new ItemStack(Material.RED_MUSHROOM, 1), ChatColor.RED + "Champignon", list5);
        menu.addButton(menu.getRow(0), 5, new ItemStack(Material.SAPLING, 1), ChatColor.DARK_GREEN + "Jungle", list6);
        menu.addButton(menu.getRow(0), 6, new ItemStack(Material.NETHERRACK, 1), ChatColor.DARK_RED + "Enfer", list7);
        menu.addButton(menu.getRow(0), 7, new ItemStack(Material.ENDER_STONE, 1), ChatColor.DARK_PURPLE + "(INDISPONIBLE) End", list8);
        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU CONFIANCE
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuConfiance(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance", 2, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = row.getRowItem(slot).getItemMeta().getDisplayName();
                if (menuName.contains("Ajouter")) {
                    getMenuConfianceAdd(pPlayer).open(pPlayer);
                } else if (menuName.contains("Retirer")) {
                    getMenuConfianceRemove(pPlayer).open(pPlayer);
                } else if (menuName.contains("Mes accès")) {
                    getMenuConfianceAccess(pPlayer).open(pPlayer);
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
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

        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU CONFIANCE ADD
    public static IsoWorldsInventory getMenuConfianceAdd(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Ajouter", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
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

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // CONFIANCE REMOVE
    public static IsoWorldsInventory getMenuConfianceRemove(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Retirer", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw retirer " + ChatColor.stripColor(menuPlayer));
                    IsoworldsUtils.cm("NAME REMOVE " + ChatColor.stripColor(menuPlayer));
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

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // CONFIANCE ACCESS
    public static IsoWorldsInventory getMenuConfianceAccess(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Confiance > Accès", 4, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajoute le joueur
                if (menuPlayer.contains("IsoWorld Accessible")) {
                    // Récupération UUID
                    String pname = menuName.split("-IsoWorld")[0].replace("[", "").replace("]", "");
                    String worldname = pname + "-IsoWorld";

                    // Pull du IsoWorld

                    // Si monde présent en dossier ?
                    if (IsoworldsUtils.checkTag(pPlayer, worldname)) {
                        // Chargement du isoworld + tp
                        Bukkit.getServer().createWorld(new WorldCreator(pname + "-IsoWorld"));
                        setWorldProperties(pname + "-IsoWorld", pPlayer);
                        IsoworldsLocations.teleport(pPlayer, pname + "-IsoWorld");
                        //plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
                    }
                    p.closeInventory();
                } else if (menuName.contains("menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
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
                if (Bukkit.getServer().getPlayer(uuid) == null) {
                    pname = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                } else {
                    pname = Bukkit.getServer().getPlayer(uuid).getDisplayName();
                }

                IsoworldsUtils.cm("PNAME: " + pname);

                // Dont show own access
                if (pname.equals(pPlayer.getName())) {
                    continue;
                }

                // Construction du lore
                String list1 = uuid.toString();

                // Construction des skin itemstack
                ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta sm = (SkullMeta) item1.getItemMeta();
                sm.setOwner(pname);
                item1.setItemMeta(sm);

                if (i >= 8) {
                    j = j++;
                }
                menu.addButton(menu.getRow(j), i, item1, ChatColor.GOLD + "IsoWorld Accessible: " + pname, list1);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU CONSTRUCTION
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuConstruction(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Construction", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("Création")) {
                    p.performCommand("iw c");
                    p.closeInventory();
                } else if (menuName.contains("Refonte")) {
                    p.performCommand("iw r");
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        // Affiche la refonte si le monde est créé, sinon affiche la création
        if (IsoworldsUtils.iwExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            String[] list1 = new String[]{"Réinitialiser votre IsoWorld."};
            menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.GOLD + "Refonte", list1);
        } else {
            String[] list1 = new String[]{"Créer votre IsoWorld."};
            menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GOLD + "Création", list1);
        }

        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU MAISON
    public static IsoWorldsInventory getMenuMaison(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Maison", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("Maison")) {
                    p.performCommand("iw h");
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        String[] list1 = new String[]{"Vous rendre sur votre IsoWorld"};
        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.BED, 1), ChatColor.GOLD + "Maison", list1);
        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU METEO
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuMeteo(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Météo", 3, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                String mtype = "";

                if (menuName.contains("Soleil")) {
                    mtype = "soleil";
                } else if (menuName.contains("Pluie")) {
                    mtype = "pluie";
                } else if (menuName.contains("Orage")) {
                    //mtype = "storm";
                    // INDISPONIBLE
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                if (menuName.contains("10 minutes")) {
                    p.performCommand("iw meteo " + mtype + " 12000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                } else if (menuName.contains("30 minutes")) {
                    p.performCommand("iw meteo " + mtype + " 36000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                } else if (menuName.contains("1 heure")) {
                    p.performCommand("iw meteo " + mtype + " 72000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                    p.closeInventory();
                }

                return true;
            }
        });

        String[] list1 = new String[]{"Le temps devient paisible et ensoleillé."};
        String[] list2 = new String[]{"Vos terres boivent l'eau de pluie."};
        String[] list3 = new String[]{"Indisponible sur la version 1.7.10, désolé ! =D"};

        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [10 minutes]", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [30 minutes]", list1);
        menu.addButton(menu.getRow(0), 2, new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + "Soleil [1 heure]", list1);

        menu.addButton(menu.getRow(1), 0, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [10 minutes]", list2);
        menu.addButton(menu.getRow(1), 1, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [30 minutes]", list2);
        menu.addButton(menu.getRow(1), 2, new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + "Pluie [1 heure]", list2);

        menu.addButton(menu.getRow(2), 0, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [10 minutes]", list3);
        menu.addButton(menu.getRow(2), 1, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [30 minutes]", list3);
        menu.addButton(menu.getRow(2), 2, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + "Orage [1 heure]", list3);

        menu.addButton(menu.getRow(2), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU ACTIVATION
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuActivation(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Activation", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("Activer")) {
                    p.performCommand("iw on");
                    p.closeInventory();
                } else if (menuName.contains("Désactiver")) {
                    p.performCommand("iw off");
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        String[] list1 = new String[]{"Charge votre IsoWorld."};
        String[] list2 = new String[]{"Décharge votre IsoWorld."};
        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GREEN + "Activer", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.GREEN + "Désactiver", list2);
        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU TELEPORTATION
    public static IsoWorldsInventory getMenuTeleportation(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Téléportation", 3, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("IsoWorld")) {
                    IsoworldsUtils.cm("NOM: " + menuName);
                    IsoworldsLocations.teleport(pPlayer, menuName);
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        int i = 0;
        int j = 0;
        for (World w : Bukkit.getServer().getWorlds()) {
            if (w.getName().contains("-IsoWorld") & w != null) {
                String[] split = w.getName().split("-IsoWorld");
                UUID uuid = UUID.fromString(split[0]);
                String name = Bukkit.getServer().getPlayer(uuid).getDisplayName();
                String[] list1 = new String[]{w.getName()};
                // A FAIRE ID
                if (i >= 8) {
                    j = j++;
                }
                //menu.addButton(menu.getRow(j), i, item1, ChatColor.GOLD + "IsoWorld Accessible: " + pname, list1);
            }
        }

        menu.addButton(menu.getRow(3), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menu;
    }

    // MENU ACTIVATION
    @SuppressWarnings("deprecation")
    public static IsoWorldsInventory getMenuTemps(Player pPlayer) {
        IsoWorldsInventory menu = new IsoWorldsInventory(ChatColor.BLUE + "IsoWorlds: Temps", 1, new onClick() {
            @Override
            public boolean click(Player p, IsoWorldsInventory menu, Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                if (menuName.contains("Jour")) {
                    p.performCommand("iw temps jour 0");
                    p.closeInventory();
                } else if (menuName.contains("Nuit")) {
                    p.performCommand("iw temps nuit 12000");
                    p.closeInventory();
                } else if (menuName.contains("Menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }
                return true;
            }
        });

        String[] list1 = new String[]{"Le jour se lève"};
        String[] list2 = new String[]{"La nuit tombe"};
        menu.addButton(menu.getRow(0), 0, new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData()), ChatColor.YELLOW + "Jour", list1);
        menu.addButton(menu.getRow(0), 1, new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData()), ChatColor.BLUE + "Nuit", list2);
        menu.addButton(menu.getRow(0), 8, new ItemStack(Material.GOLD_BLOCK), ChatColor.RED + "Menu principal", "Retour au menu principal");

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
