package sponge.Utils;

import common.Msg;
import javafx.scene.control.cell.TextFieldListCell;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
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
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds")
                        .color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
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
        list5.add(Text.of("Gérez la pluie et le beau temps"));
        list5.add(Text.of("de votre IsoWorld"));
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
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(4, 0)).set(item5);
        menu.query(SlotPos.of(5, 0)).set(item6);
        menu.query(SlotPos.of(6, 0)).set(item7);
        menu.query(SlotPos.of(7, 0)).set(item8);

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
                        //Sponge.getCommandManager().process(pPlayer, "iw biome plaines");
                        closeMenu(pPlayer);;
                    } else if (menuName.contains("Désert")) {
                        //Sponge.getCommandManager().process(pPlayer, "iw biome desert");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Biome").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Un biome relativement plat avec des collines"));
        list1.add(Text.of("vallonnées et une grande quantité de fleurs."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Un biome constitué principalement de sable"));
        list2.add(Text.of("de cactus et de canne à sucre."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Plaines")
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Désert")
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

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
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Ajouter")) {
                        closeOpenMenu(pPlayer, getMenuConfianceAdd(pPlayer));
                    } else if (menuName.contains("Retirer")) {
                        closeOpenMenu(pPlayer, getMenuConfianceAdd(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Autoriser l'accès à votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retirer l'accès à votre IsoWorld."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Ajouter")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Retirer")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

        return menu;
    }

    // CONFIANCE ADD
    public static Inventory getMenuConfianceAdd(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);
                    // Si joueur, on ajouter le joueur
                    if (menuName.contains("Joueur")) {
                        commandMenu(pPlayer, "iw confiance " + menuName);
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Ajouter").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(27, 1))
                .build(instance);




        int i = 0;
        int j = 0;
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            if (!IsoworldsUtils.trustExists(p, pPlayer.getUniqueId(), Msg.keys.EXISTE_PAS_TRUST)) {
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of("Joueur: " + p.getName()));
                ItemStack item1 = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Cliquer pour ajouter")
                        .color(TextColors.GOLD).build())).quantity(1).build();
                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(item1);
            }
        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Retirer")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(26, 3)).set(item2);

        return menu;
    }

    // CONFIANCE REMOVE
    public static Inventory getMenuConfianceRemove(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);
                    // Si joueur, on ajouter le joueur
                    if (menuName.contains("Joueur")) {
                        commandMenu(pPlayer, "iw retirer " + menuName);
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Retirer").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(27, 1))
                .build(instance);




        int i = 0;
        int j = 0;
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            if (!IsoworldsUtils.trustExists(p, pPlayer.getUniqueId(), Msg.keys.EXISTE_PAS_TRUST)) {
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of("Joueur: " + p.getName()));
                ItemStack item1 = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Cliquer pour retirer")
                        .color(TextColors.GOLD).build())).quantity(1).build();
                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(item1);
            }
        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Retirer")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(26, 3)).set(item2);

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
                    if (menuName.contains("Création")) {
                        commandMenu(pPlayer, "iw c");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Refonte")) {
                        commandMenu(pPlayer, "iw r");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Construction").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Créer votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Réinitialiser votre IsoWorld."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Création")
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Refonte")
                .color(TextColors.GOLD).build())).quantity(1).build();

        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

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

                    if (menuName.contains("Maison")) {
                        commandMenu(pPlayer, "iw h");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Maison").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Vous rendre sur votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Maison")
                .color(TextColors.GOLD).build())).quantity(1).build();

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(8, 0)).set(item2);

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
                    String mtype = "";

                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Soleil")) {
                        mtype = "soleil";
                    } else if (menuName.contains("Pluie")) {
                        mtype = "soleil";
                    } else if (menuName.contains("Orage")) {
                        mtype = "soleil";
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }

                    if (menuName.contains("10 minutes")) {
                        commandMenu(pPlayer, "iw meteo " + mtype + " 12000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("30 minutes")) {
                        commandMenu(pPlayer, "iw meteo " + mtype + " 36000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("1 heure")) {
                        commandMenu(pPlayer, "iw meteo " + mtype + " 72000 " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        closeMenu(pPlayer);
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Météo").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 3))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Le temps devient paisible et ensoleillé."));

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Vos terres boivent l'eau de pluie."));

        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("L'orage fait rage !"));

        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Retour au menu principal"));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [10 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [30 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [1 heure]")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [10 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [30 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [1 heure]")
                .color(TextColors.BLUE).build())).quantity(1).build();

        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [10 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [30 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [1 heure]")
                .color(TextColors.RED).build())).quantity(1).build();

        ItemStack item10 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(0, 1)).set(item4);
        menu.query(SlotPos.of(1, 1)).set(item5);
        menu.query(SlotPos.of(2, 1)).set(item6);
        menu.query(SlotPos.of(0, 2)).set(item7);
        menu.query(SlotPos.of(1, 2)).set(item8);
        menu.query(SlotPos.of(2, 2)).set(item9);
        menu.query(SlotPos.of(8, 2)).set(item10);


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
                    if (menuName.contains("Activer")) {
                        commandMenu(pPlayer, "iw on");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Désactiver")) {
                        commandMenu(pPlayer, "iw off");
                        closeMenu(pPlayer);
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Activation").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Charge votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Décharge votre IsoWorld."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Activer")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Désactiver")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

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
                    commandMenu(pPlayer, "iw off");
                    closeMenu(pPlayer);

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Téléporation").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(27, 1))
                .build(instance);

        int i = 0;
        int j = 0;
        for (World w : Sponge.getServer().getWorlds()) {
            if (w.getName().contains("-IsoWorld")) {
                String[] split = w.getName().split("-IsoWorld");
                UUID uuid = UUID.fromString(split[0]);
                String name = Sponge.getServer().getPlayer(uuid).get().getName();
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of(w.getName()));
                ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                        .color(TextColors.GOLD).build())).quantity(1).build();
                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(item1);
            }
        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Téléportez-vous sur un IsoWorld."));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(26, 3)).set(item2);

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
                    if (menuName.contains("Jour")) {
                        Sponge.getCommandManager().process(pPlayer, "iw temps jour " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        pPlayer.closeInventory(Cause.of(NamedCause.simulated(pPlayer)));
                    } else if (menuName.contains("Nuit")) {
                        Sponge.getCommandManager().process(pPlayer, "iw temps nuit " + pPlayer.getUniqueId().toString() + "-IsoWorld");
                        pPlayer.closeInventory(Cause.of(NamedCause.simulated(pPlayer)));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Temps").color(TextColors.GOLD).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Le jour se lève"));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("La nuit tombe"));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Jour")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Nuit")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

        return menu;
    }

    private static void closeOpenMenu(Player pPlayer, Inventory inv) {
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

    private static void closeMenu(Player pPlayer) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                pPlayer.closeInventory(Cause.of(NamedCause.simulated(pPlayer)));
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Ferme l'inventaire d'un joueur.").submit(instance);
    }

    private static void commandMenu(Player pPlayer, String cmd) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                Sponge.getCommandManager().process(pPlayer, cmd);
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Execute une commande pour le joueur.").submit(instance);
    }
}
