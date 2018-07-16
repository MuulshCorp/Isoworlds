package bukkit.util.inventory;

import bukkit.MainBukkit;
import bukkit.util.Inventories;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin on 16/07/2018.
 */
public class BiomeInv implements Listener {

    private String name;
    private int size;
    private bukkit.util.Inventories.onClick click;
    List<String> viewing = new ArrayList<String>();
    private static final MainBukkit plugin = MainBukkit.instance;

    private ItemStack[] items;


    // MENU BIOME
    @SuppressWarnings("deprecation")
    public static bukkit.util.Inventories getMenuBiome(Player pPlayer) {
        bukkit.util.Inventories menu = new bukkit.util.Inventories(ChatColor.BLUE + "IsoWorlds: Biome", 2, new bukkit.util.Inventories.onClick() {
            @Override
            public boolean click(Player p, bukkit.util.Inventories menu, bukkit.util.Inventories.Row row, int slot, ItemStack item) {
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
}
