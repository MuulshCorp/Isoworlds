package bukkit.util.inventory;

import bukkit.MainBukkit;
import bukkit.location.Locations;
import common.Msg;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Edwin on 16/07/2018.
 */
public class TrustInv implements Listener {

    private String name;
    private int size;
    private bukkit.util.Inventories.onClick click;
    List<String> viewing = new ArrayList<String>();
    private static final MainBukkit plugin = MainBukkit.instance;

    private ItemStack[] items;




    // MENU CONFIANCE
    @SuppressWarnings("deprecation")
    public static bukkit.util.Inventories getMenuConfiance(Player pPlayer) {
        bukkit.util.Inventories menu = new bukkit.util.Inventories(ChatColor.BLUE + "IsoWorlds: Confiance", 2, new bukkit.util.Inventories.onClick() {
            @Override
            public boolean click(Player p, bukkit.util.Inventories menu, bukkit.util.Inventories.Row row, int slot, ItemStack item) {
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
    public static bukkit.util.Inventories getMenuConfianceAdd(Player pPlayer) {
        bukkit.util.Inventories menu = new bukkit.util.Inventories(ChatColor.BLUE + "IsoWorlds: Confiance > Ajouter", 4, new bukkit.util.Inventories.onClick() {
            @Override
            public boolean click(Player p, bukkit.util.Inventories menu, bukkit.util.Inventories.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw confiance " + ChatColor.stripColor(menuPlayer));
                    Utils.cm("Confiance:" + menuPlayer);
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
        ResultSet trusts = Utils.getTrusts(pPlayer, Msg.keys.SQL);
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
    public static bukkit.util.Inventories getMenuConfianceRemove(Player pPlayer) {
        bukkit.util.Inventories menu = new bukkit.util.Inventories(ChatColor.BLUE + "IsoWorlds: Confiance > Retirer", 4, new bukkit.util.Inventories.onClick() {
            @Override
            public boolean click(Player p, bukkit.util.Inventories menu, bukkit.util.Inventories.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajouter le joueur
                if (menuName.contains("Joueur")) {
                    p.performCommand("iw retirer " + ChatColor.stripColor(menuPlayer));
                    Utils.cm("NAME REMOVE " + ChatColor.stripColor(menuPlayer));
                    p.closeInventory();
                } else if (menuName.contains("menu principal")) {
                    MenuPrincipal(pPlayer).open(pPlayer);
                }

                return true;
            }
        });


        int i = 0;
        int j = 0;
        ResultSet trusts = Utils.getTrusts(pPlayer, Msg.keys.SQL);

        try {
            while (trusts.next()) {
                // Récupération du nom du joueur
                String tmp = trusts.getString(1);
                Utils.cm("name = " + tmp);
                UUID uuid = UUID.fromString(tmp);
                Utils.cm("uuid = " + uuid);

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
    public static bukkit.util.Inventories getMenuConfianceAccess(Player pPlayer) {
        bukkit.util.Inventories menu = new bukkit.util.Inventories(ChatColor.BLUE + "IsoWorlds: Confiance > Accès", 4, new bukkit.util.Inventories.onClick() {
            @Override
            public boolean click(Player p, bukkit.util.Inventories menu, bukkit.util.Inventories.Row row, int slot, ItemStack item) {
                String menuName = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getLore().toString());
                String menuPlayer = ChatColor.stripColor(row.getRowItem(slot).getItemMeta().getDisplayName());
                // Si joueur, on ajoute le joueur
                if (menuPlayer.contains("IsoWorld Accessible")) {
                    // Récupération UUID
                    String pname = menuName.split("-IsoWorld")[0].replace("[", "").replace("]", "");
                    String worldname = pname + "-IsoWorld";

                    // Pull du IsoWorld

                    // Si monde présent en dossier ?
                    if (Utils.checkTag(pPlayer, worldname)) {
                        // Chargement du isoworld + tp
                        Bukkit.getServer().createWorld(new WorldCreator(pname + "-IsoWorld"));
                        setWorldProperties(pname + "-IsoWorld", pPlayer);
                        Locations.teleport(pPlayer, pname + "-IsoWorld");
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
        ResultSet trusts = Utils.getAccess(pPlayer, Msg.keys.SQL);
        try {
            while (trusts.next()) {
                // Récupération uuid
                String[] tmp = trusts.getString(1).split("-IsoWorld");
                UUID uuid = UUID.fromString(tmp[0]);
                String pname;

                pname = (Bukkit.getServer().getPlayer(uuid) == null ? Bukkit.getServer().getOfflinePlayer(uuid).getName() : Bukkit.getServer().getPlayer(uuid).getDisplayName());

                Utils.cm("PNAME: " + pname);

                // Dont show own access
                if (pname.equals(pPlayer.getName()) || pname == null) {
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
