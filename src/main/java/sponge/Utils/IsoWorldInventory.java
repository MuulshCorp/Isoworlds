package sponge.Utils;

import javafx.scene.control.cell.TextFieldListCell;
import org.spongepowered.api.data.key.Keys;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sponge.IsoworldsSponge.instance;

/**
 * Created by Edwin on 25/11/2017.
 */
public class IsoWorldInventory {

    // MENU PRINCIPAL
    public static Inventory menuPrincipal(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("IsoWorlds")))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        // Création item
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez le biome des vos chunks"));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Gérez qui peut avoir accès à votre IsoWorld"));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Créez ou refondez votre IsoWorld"));
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Rendez-vous sur votre IsoWorld"));
        List<Text> list5 = new ArrayList<Text>();
        list5.add(Text.of("Gérez la pluie et le beau temps de votre IsoWorld"));
        List<Text> list6 = new ArrayList<Text>();
        list6.add(Text.of("Chargez-Déchargez votre IsoWorld"));
        List<Text> list7 = new ArrayList<Text>();
        list7.add(Text.of("Téléportez vous sur un IsoWorld [STAFF]"));
        List<Text> list8 = new ArrayList<Text>();
        list8.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome").color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.EMERALD).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Confiance").color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.BRICK).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Construction").color(TextColors.GRAY).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.BED).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Maison").color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.DOUBLE_PLANT).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Météo").color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.LEVER).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Activation").color(TextColors.RED).build())).quantity(1).build();
        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.DIAMOND_BOOTS).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Téléportation").color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();
        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.CLOCK).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Temps").color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();

        // Placement item dans le menu
        menu.query(SlotPos.of(0,0)).set(item1);
        menu.query(SlotPos.of(1,0)).set(item2);
        menu.query(SlotPos.of(2,0)).set(item3);
        menu.query(SlotPos.of(3,0)).set(item4);
        menu.query(SlotPos.of(4,0)).set(item5);
        menu.query(SlotPos.of(5,0)).set(item6);
        menu.query(SlotPos.of(6,0)).set(item7);
        menu.query(SlotPos.of(7,0)).set(item8);

        return menu;
    }

    // BIOME
    public static Inventory menuBiome(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    // MENU PRINCIPAL //
                    // BIOME

                    // FAIRE ICI
                    if (clickInventoryEvent. .getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Biome")) {
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
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Menu principal").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        ItemStack item = ItemStack.builder().itemType(ItemTypes.COAL).add(Keys.DISPLAY_NAME, Text.of("Test")).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item);

        return menu;
    }

    // CONFIANCE
    public static Inventory menuConfiance(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        ItemStack item = ItemStack.builder().itemType(ItemTypes.COAL).add(Keys.DISPLAY_NAME, Text.of("Test")).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item);

        return menu;
    }
}
