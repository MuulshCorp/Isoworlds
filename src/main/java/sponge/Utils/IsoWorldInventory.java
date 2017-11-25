package sponge.Utils;

import javafx.scene.control.cell.TextFieldListCell;
import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));

                    String menuName = String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    // MENU PRINCIPAL //
                    // BIOME
                    if (menuName.equals("Biome")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuBiome(pPlayer));
                        // CONFIANCE
                    } else if (menuName.equals("Confiance")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuConfiance(pPlayer));
                        // CONSTRUCTION
                    } else if (menuName.equals("Construction")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuConstruction(pPlayer));
                        // MAISON
                    } else if (menuName.equals("Maison")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuMaison(pPlayer));
                        // METEO
                    } else if (menuName.equals("Météo")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuMeteo(pPlayer));
                        // ACTIVATION
                    } else if (menuName.equals("Activation")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuActivation(pPlayer));
                        // TELEPORTATION
                    } else if (menuName.equals("Téléportation")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuTeleportation(pPlayer));
                    } else if (menuName.equals("Temps")) {
                        IsoworldsUtils.cm("PLAYER MENU 3");
                        closeOpenMenu(pPlayer, getMenuTemps(pPlayer));
                    }

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

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.EMERALD).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Confiance")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.BRICK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Construction")
                .color(TextColors.GRAY).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.BED).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Maison")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.DOUBLE_PLANT).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Météo")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.LEVER).add(Keys.ITEM_LORE, list6).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Activation")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.DIAMOND_BOOTS).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Téléportation")
                .color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();
        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.CLOCK).add(Keys.ITEM_LORE, list8).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Temps")
                .color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();

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
    public static Inventory getMenuBiome(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Plaines")) {
                        Sponge.getCommandManager().process(pPlayer, "iw biome plaines");
                        pPlayer.closeInventory();
                    } else if (menuName.contains("Désert")) {
                        Sponge.getCommandManager().process(pPlayer, "iw biome desert");
                        pPlayer.closeInventory();
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Biome").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // CONFIANCE
    public static Inventory getMenuConfiance(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // CONSTRUCTION
    public static Inventory getMenuConstruction(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Construction").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // MAISON
    public static Inventory getMenuMaison(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Maison").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // METEO
    public static Inventory getMenuMeteo(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Météo").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // ACTIVATION
    public static Inventory getMenuActivation(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Activation").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // TELEPORTATION
    public static Inventory getMenuTeleportation(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Téléporation").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    // TEMPS
    public static Inventory getMenuTemps(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Temps").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Gérez l'heure de votre IsoWorld"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item1);

        return menu;
    }

    private static void closeOpenMenu(Player pPlayer, Inventory inv) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                pPlayer.closeInventory();
                pPlayer.openInventory(inv);
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Ferme l'inventaire d'un joueur.").submit(instance);
    }
}
