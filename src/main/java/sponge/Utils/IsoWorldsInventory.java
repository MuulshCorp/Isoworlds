package sponge.Utils;

import common.Cooldown;
import common.Msg;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
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
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static sponge.IsoworldsSponge.instance;
import static sponge.Utils.IsoworldsUtils.iwInProcess;
import static sponge.Utils.IsoworldsUtils.setWorldProperties;

/**
 * Created by Edwin on 25/11/2017.
 */
public class IsoWorldsInventory {

    // MENU PRINCIPAL
    private static final IsoworldsSponge plugin = IsoworldsSponge.instance;

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
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu BIOME: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuBiome(pPlayer));
                        // CONFIANCE
                    } else if (menuName.equals("Confiance")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu CONFIANCE: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuConfiance(pPlayer));
                        // CONSTRUCTION
                    } else if (menuName.equals("Construction")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu CONSTRUCTION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuConstruction(pPlayer));
                        // MAISON
                    } else if (menuName.equals("Maison")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu MAISON: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuMaison(pPlayer));
                        // METEO
                    } else if (menuName.equals("Météo")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu METEO: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuMeteo(pPlayer));
                        // ACTIVATION
                    } else if (menuName.equals("Activation")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu ACTIVATION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuActivation(pPlayer));
                        // TELEPORTATION
                    } else if (menuName.equals("Téléportation")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu TELEPORTATION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuTeleportation(pPlayer));
                    } else if (menuName.equals("Temps")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu TEMPS: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuTemps(pPlayer));
                    } else if (menuName.equals("Warp")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu WARP: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuWarp(pPlayer));
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds")
                        .color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        // Récupération nombre charge
        Integer charges = IsoworldsUtils.getCharge(pPlayer, Msg.keys.SQL);
        Integer playtime = IsoworldsUtils.getPlayTime(pPlayer, Msg.keys.SQL);
        String formatedPlayTime;

        if (playtime > 60) {
            formatedPlayTime = playtime / 60 + " Heure(s) et " + playtime % 60 + " minute(s)";
        } else {
            formatedPlayTime = playtime + " minute(s)";
        }

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
        list7.add(Text.of("Rendez-vous sur les dimensions publiques"));
        List<Text> list8 = new ArrayList<Text>();
        list8.add(Text.of("Gérez l'heure de votre IsoWorld"));
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of(Text.builder("Charges: ").color(TextColors.YELLOW).append(Text.of(Text.builder(charges + " disponible(s)").color(TextColors.GREEN))).build()));
        list9.add(Text.of(Text.builder("Temps de jeu: ").color(TextColors.YELLOW).append(Text.of(Text.builder(formatedPlayTime).color(TextColors.GREEN))).build()));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.DIAMOND_PICKAXE).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Construction")
                .color(TextColors.GRAY).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.BED).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Maison")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.SKULL_TYPE, SkullTypes.PLAYER).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Confiance")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.LEAVES).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Biome")
                .color(TextColors.GOLD).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.CLOCK).add(Keys.ITEM_LORE, list8).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Temps")
                .color(TextColors.LIGHT_PURPLE).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.DOUBLE_PLANT).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Météo")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.COMPASS).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Warp")
                .color(TextColors.DARK_GREEN).build())).quantity(1).build();
        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.LEVER).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Statistiques")
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
        //if (pPlayer.hasPermission("isoworlds.menu.teleportation")) {
        //    menu.query(SlotPos.of(7, 0)).set(item8);
        //}
        //menu.query(SlotPos.of(7, 0)).set(item8);

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
                        commandMenu(pPlayer, "iw biome plaines");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Désert")) {
                        commandMenu(pPlayer, "iw biome desert");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Marais")) {
                        commandMenu(pPlayer, "iw biome marais");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Océan")) {
                        commandMenu(pPlayer, "iw biome océan");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Champignon")) {
                        commandMenu(pPlayer, "iw biome champignon");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Jungle")) {
                        commandMenu(pPlayer, "iw biome jungle");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Enfer")) {
                        commandMenu(pPlayer, "iw biome enfer");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("End")) {
                        commandMenu(pPlayer, "iw biome end");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Biome").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 2))
                .build(instance);

        // Plaines
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Un biome relativement plat avec des collines"));
        list1.add(Text.of("vallonnées et une grande quantité de fleurs."));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Plaines")
                .color(TextColors.GREEN).build())).quantity(1).build();

        // Désert
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Un biome constitué principalement de sable"));
        list2.add(Text.of("de cactus et de canne à sucre."));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.SAND).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Désert")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        // Marais
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Un biome avec des nombreuses étendues"));
        list3.add(Text.of("d'eau."));

        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.CLAY).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Marais")
                .color(TextColors.GRAY).build())).quantity(1).build();

        // Océan
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Un biome avec de vastes étendues d'eau."));

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.BLUE).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Océan")
                .color(TextColors.BLUE).build())).quantity(1).build();

        // Champignon
        List<Text> list5 = new ArrayList<Text>();
        list5.add(Text.of("Un biome assez rare, les monstres"));
        list5.add(Text.of("n'y apparaîssent pas."));

        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.RED_MUSHROOM).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Champignon")
                .color(TextColors.RED).build())).quantity(1).build();

        // Jungle
        List<Text> list6 = new ArrayList<Text>();
        list6.add(Text.of("Un biome avec des arbres imposants"));
        list6.add(Text.of("et une grosse quantité de bois."));

        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.SAPLING).add(Keys.ITEM_LORE, list6).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Jungle")
                .color(TextColors.DARK_GREEN).build())).quantity(1).build();

        // Enfer
        List<Text> list7 = new ArrayList<Text>();
        list7.add(Text.of("Un biome qui constitue le nether."));

        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.NETHERRACK).add(Keys.ITEM_LORE, list7).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Enfer")
                .color(TextColors.DARK_RED).build())).quantity(1).build();

        // End
        List<Text> list8 = new ArrayList<Text>();
        list8.add(Text.of("Un biome qui constitue l'end."));

        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.END_STONE).add(Keys.ITEM_LORE, list8).add(Keys.DISPLAY_NAME, Text.of(Text.builder("End")
                .color(TextColors.DARK_PURPLE).build())).quantity(1).build();

        // Menu principal
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of("Retour au menu principal"));

        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(4, 0)).set(item5);
        menu.query(SlotPos.of(5, 0)).set(item6);
        menu.query(SlotPos.of(6, 0)).set(item7);
        menu.query(SlotPos.of(7, 0)).set(item8);
        menu.query(SlotPos.of(8, 1)).set(item9);

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
                        closeOpenMenu(pPlayer, getMenuConfianceRemove(pPlayer));
                    } else if (menuName.contains("Mes accès")) {
                        closeOpenMenu(pPlayer, getMenuConfianceAccess(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Autoriser l'accès à votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retirer l'accès à votre IsoWorld."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Ajouter")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Retirer")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.ORANGE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Mes accès")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(8, 0)).set(item4);

        return menu;
    }

    // CONFIANCE ADD
    public static Inventory getMenuConfianceAdd(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.ITEM_LORE).get().toString());
                    String menuPlayer = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);
                    // Si joueur, on ajouter le joueur
                    if (menuName.contains("Joueur")) {
                        commandMenu(pPlayer, "iw confiance " + menuPlayer);
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Ajouter").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                .build(instance);


        int i = 0;
        int j = 0;
        ResultSet trusts = IsoworldsUtils.getTrusts(pPlayer, Msg.keys.SQL);
        List<String> players = new ArrayList<String>();

        // Récupération joueurs trust dans un tableau
        try {
            while (trusts.next()) {
                String tmp = trusts.getString(1);
                IsoworldsUtils.cm("name = " + tmp);
                UUID uuid = UUID.fromString(tmp);
                IsoworldsUtils.cm("uuid = " + uuid);
                Optional<User> user = IsoworldsUtils.getPlayerFromUUID(uuid);
                players.add(user.get().getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Boucle des joueurs en ligne, si dans tableau on continue
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            if (players.contains(p.getName())) {
                continue;
            }

            // Dont show own access
            if (p.getName().equals(pPlayer.getName())) {
                continue;
            }

            // Construction du lore
            List<Text> list1 = new ArrayList<Text>();
            list1.add(Text.of("Joueur"));

            // Construction des skin itemstack
            SkullData data = Sponge.getGame().getDataManager().getManipulatorBuilder(SkullData.class).get().create();
            data.set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
            ItemStack stack = Sponge.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.SKULL).itemData(data)
                    .add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(p.getName())
                            .color(TextColors.GOLD).build())).quantity(1)
                    .build();
            RepresentedPlayerData skinData = Sponge.getGame().getDataManager().getManipulatorBuilder(RepresentedPlayerData.class).get().create();
            skinData.set(Keys.REPRESENTED_PLAYER, GameProfile.of(p.getUniqueId(), p.getName()));
            stack.offer(skinData);

            if (i >= 8) {
                j = j++;
            }
            menu.query(SlotPos.of(i, j)).set(stack);
            i++;


        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(8, 3)).set(item2);

        return menu;
    }

    // CONFIANCE REMOVE
    public static Inventory getMenuConfianceRemove(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.ITEM_LORE).get().toString());
                    String menuPlayer = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);
                    // Si joueur, on ajouter le joueur
                    if (menuName.contains("Joueur")) {
                        commandMenu(pPlayer, "iw retirer " + menuPlayer);
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Retirer").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                .build(instance);

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
                Optional<User> user = IsoworldsUtils.getPlayerFromUUID(uuid);

                // Dont show own access
                if (user.get().getName().equals(pPlayer.getName())) {
                    continue;
                }

                // Construction du lore
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of("Joueur"));

                // Construction des skin itemstack
                SkullData data = Sponge.getGame().getDataManager().getManipulatorBuilder(SkullData.class).get().create();
                data.set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                ItemStack stack = Sponge.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.SKULL).itemData(data)
                        .add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder(user.get().getName())
                                .color(TextColors.GOLD).build())).quantity(1)
                        .build();
                RepresentedPlayerData skinData = Sponge.getGame().getDataManager().getManipulatorBuilder(RepresentedPlayerData.class).get().create();
                IsoworldsUtils.cm("USER: " + user.get().getUniqueId().toString() + user.get().getName());
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
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(8, 3)).set(item2);

        return menu;
    }

    // CONFIANCE ACCESS
    private static Inventory getMenuConfianceAccess(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.ITEM_LORE).get().get(0).toPlain());
                    String menuPlayer = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    IsoworldsUtils.cm("CURSOR 2 " + String.valueOf(clickInventoryEvent.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain()));
                    clickInventoryEvent.setCancelled(true);

                    // Si joueur, on ajoute le joueur
                    if (menuPlayer.contains("IsoWorld Accessible")) {
                        // Récupération UUID
                        String[] tmp = menuName.split("-IsoWorld");
                        IsoworldsUtils.cm("NAME " + menuName);
                        Optional<User> user = IsoworldsUtils.getPlayerFromUUID(UUID.fromString(tmp[0]));
                        String worldname = user.get().getUniqueId().toString() + "-IsoWorld";


                        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
                        if (iwInProcess(pPlayer, worldname)) {
                            return;
                        }

                        // Pull du IsoWorld
                        Task.builder().execute(new Runnable() {
                            @Override
                            public void run() {
                                // Si monde présent en dossier ?
                                // Removing iwInProcess in task
                                if (IsoworldsUtils.checkTag(pPlayer, worldname)) {
                                    // Chargement du isoworld + tp
                                    setWorldProperties(worldname, pPlayer);
                                    Sponge.getServer().loadWorld(worldname);
                                    IsoworldsLocations.teleport(pPlayer, worldname);
                                    plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
                                }

                                // Supprime le lock (worldname, worldname uniquement pour les access confiance)
                                plugin.lock.remove(worldname + ";" + worldname);

                            }
                        })
                                .delay(1, TimeUnit.SECONDS)
                                .name("Pull du IsoWorld.").submit(instance);

                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));

                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Confiance > Accès").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                .build(instance);

        int i = 0;
        int j = 0;
        ResultSet trusts = IsoworldsUtils.getAccess(pPlayer, Msg.keys.SQL);
        try {
            while (trusts.next()) {
                // Récupération uuid
                String[] tmp = trusts.getString(1).split("-IsoWorld");
                UUID uuid = UUID.fromString(tmp[0]);
                Optional<User> user = IsoworldsUtils.getPlayerFromUUID(uuid);

                // Dont show own access
                if (user.get().getName().equals(pPlayer.getName())) {
                    continue;
                }

                // Construction du lore
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of(user.get().getUniqueId().toString()));
                IsoworldsUtils.cm("record: " + trusts.getString(1));

                // Construction des skin itemstack
                SkullData data = Sponge.getGame().getDataManager().getManipulatorBuilder(SkullData.class).get().create();
                data.set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                ItemStack stack = Sponge.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.SKULL).itemData(data)
                        .add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("IsoWorld Accessible: " + user.get().getName())
                                .color(TextColors.GOLD).build())).quantity(1)
                        .build();
                RepresentedPlayerData skinData = Sponge.getGame().getDataManager().getManipulatorBuilder(RepresentedPlayerData.class).get().create();
                IsoworldsUtils.cm("USER: " + user.get().getUniqueId().toString() + user.get().getName());
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
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(8, 3)).set(item2);

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
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu CREATION: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuCreation(pPlayer));
                    } else if (menuName.contains("Refonte")) {
                        IsoworldsUtils.cm("[TRACKING-IW] Clic menu REFONTE: " + pPlayer.getName());
                        closeOpenMenu(pPlayer, getMenuCreation(pPlayer));
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Construction").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);


        // Affiche la refonte si le monde est créé, sinon affiche la création
        if (IsoworldsUtils.iwExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            List<Text> list1 = new ArrayList<Text>();
            list1.add(Text.of("Réinitialiser votre IsoWorld (choix du patern)."));
            ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Refonte")
                    .color(TextColors.GOLD).build())).quantity(1).build();
            menu.query(SlotPos.of(0, 0)).set(item1);
        } else {
            List<Text> list1 = new ArrayList<Text>();
            list1.add(Text.of("Créer votre IsoWorld."));
            ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Création")
                    .color(TextColors.GOLD).build())).quantity(1).build();
            menu.query(SlotPos.of(0, 0)).set(item1);
        }

        // Bouton retour
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retour au menu principal"));
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(8, 0)).set(item2);

        return menu;
    }

    // CREATION
    public static Inventory getMenuCreation(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());

                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Normal")) {
                        Sponge.getCommandManager().process(pPlayer, "iw c n");
                    } else if (menuName.contains("Void")) {
                        Sponge.getCommandManager().process(pPlayer, "iw c v");
                    } else if (menuName.contains("Ocean")) {
                        Sponge.getCommandManager().process(pPlayer, "iw c o");
                    } else if (menuName.contains("Flat")) {
                        Sponge.getCommandManager().process(pPlayer, "iw c f");
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Météo").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 3))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Génération terrain (Classique"));

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Génération vide (Totalement vide)"));

        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Génération ocean (Plat avec uniquement de l'eau)"));

        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Génération plate (Plat avec uniquement de la dirt)"));

        List<Text> list5 = new ArrayList<Text>();
        list5.add(Text.of("Retour au menu principal"));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Normal]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Void")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Ocean")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Flat")
                .color(TextColors.BLUE).build())).quantity(1).build();

        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list5).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(0, 1)).set(item4);
        menu.query(SlotPos.of(1, 1)).set(item5);

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
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Maison").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Vous rendre sur votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Retour au menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.BED).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Maison")
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
                        mtype = "pluie";
                    } else if (menuName.contains("Orage")) {
                        mtype = "storm";
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }

                    if (menuName.contains("10 minutes")) {
                        // Check if charge higher than 0
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
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Météo").color(TextColors.BLUE).build())))
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


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [10 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [30 minutes]")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.YELLOW).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Soleil [1 heure]")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [10 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item5 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [30 minutes]")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item6 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Pluie [1 heure]")
                .color(TextColors.BLUE).build())).quantity(1).build();

        ItemStack item7 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [10 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item8 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [30 minutes]")
                .color(TextColors.RED).build())).quantity(1).build();
        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Orage [1 heure]")
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
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Activation").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Charge votre IsoWorld."));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Décharge votre IsoWorld."));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Retour au menu principal"));


        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Activer")
                .color(TextColors.GREEN).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Désactiver")
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
                            .get(0).getOriginal().get(Keys.ITEM_LORE).get().toString());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("IsoWorld")) {
                        IsoworldsUtils.cm("NOM: " + menuName);
                        teleportMenu(pPlayer, menuName);
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }

                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Téléporation").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 4))
                .build(instance);

        int i = 0;
        int j = 0;
        for (World w : Sponge.getServer().getWorlds()) {
            if (w.getName().contains("-IsoWorld") & w.isLoaded()) {
                String[] split = w.getName().split("-IsoWorld");
                UUID uuid = UUID.fromString(split[0]);
                String name = Sponge.getServer().getPlayer(uuid).get().getName();
                List<Text> list1 = new ArrayList<Text>();
                list1.add(Text.of(w.getName()));
                WorldProperties worldProperties = Sponge.getServer().getWorldProperties(w.getName()).get();
                String id = worldProperties.getAdditionalProperties().getInt(DataQuery.of("SpongeData", "dimensionId")).get().toString();
                ItemStack item1 = ItemStack.builder().itemType(ItemTypes.GRASS).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("IsoWorld: ID " + id)
                        .color(TextColors.GOLD).build())).quantity(1).build();
                if (i >= 8) {
                    j = j++;
                }
                menu.query(SlotPos.of(i, j)).set(item1);
            }
        }

        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Menu principal"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(8, 3)).set(item2);

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
                        commandMenu(pPlayer, "iw temps jour");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Nuit")) {
                        commandMenu(pPlayer, "iw temps nuit");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Temps").color(TextColors.BLUE).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 1))
                .build(instance);

        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Le jour se lève"));
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("La nuit tombe"));
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Menu principal"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.WHITE).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Jour")
                .color(TextColors.YELLOW).build())).quantity(1).build();
        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.BLACK).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Nuit")
                .color(TextColors.BLUE).build())).quantity(1).build();
        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();
        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(8, 0)).set(item3);

        return menu;
    }

    public static Inventory getMenuWarp(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    String menuName = String.valueOf(clickInventoryEvent.getTransactions()
                            .get(0).getOriginal().get(Keys.DISPLAY_NAME).get().toPlain());
                    clickInventoryEvent.setCancelled(true);
                    if (menuName.contains("Exploration")) {
                        commandMenu(pPlayer, "iw warp exploration");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Minage")) {
                        commandMenu(pPlayer, "iw warp minage");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("End")) {
                        commandMenu(pPlayer, "iw warp end");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Nether")) {
                        commandMenu(pPlayer, "iw warp nether");
                        closeMenu(pPlayer);
                    } else if (menuName.contains("Menu principal")) {
                        closeOpenMenu(pPlayer, menuPrincipal(pPlayer));
                    }
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(Text.builder("IsoWorlds: Warp").color(TextColors.DARK_GREEN).build())))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 2))
                .build(instance);

        // Minage
        List<Text> list1 = new ArrayList<Text>();
        list1.add(Text.of("Exploitez les ressources (quarry...)"));
        list1.add(Text.of("Réinitialisé tous 1er du mois à 19h"));

        ItemStack item1 = ItemStack.builder().itemType(ItemTypes.STONE_PICKAXE).add(Keys.ITEM_LORE, list1).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Minage")
                .color(TextColors.GREEN).build())).quantity(1).build();

        // Exploration
        List<Text> list2 = new ArrayList<Text>();
        list2.add(Text.of("Explorez, combattez, enrichissez vous !"));
        list2.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item2 = ItemStack.builder().itemType(ItemTypes.FILLED_MAP).add(Keys.ITEM_LORE, list2).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Exploration")
                .color(TextColors.YELLOW).build())).quantity(1).build();

        // End
        List<Text> list3 = new ArrayList<Text>();
        list3.add(Text.of("Un grondement sourd se fait entendre..."));
        list3.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item3 = ItemStack.builder().itemType(ItemTypes.ENDER_PEARL).add(Keys.ITEM_LORE, list3).add(Keys.DISPLAY_NAME, Text.of(Text.builder("End")
                .color(TextColors.DARK_GRAY).build())).quantity(1).build();

        // Nether
        List<Text> list4 = new ArrayList<Text>();
        list4.add(Text.of("Lieu très hostile !"));
        list4.add(Text.of("Réinitialisé tous les vendredi à 19h"));

        ItemStack item4 = ItemStack.builder().itemType(ItemTypes.NETHER_STAR).add(Keys.ITEM_LORE, list4).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Nether")
                .color(TextColors.DARK_RED).build())).quantity(1).build();

        // Menu principal
        List<Text> list9 = new ArrayList<Text>();
        list9.add(Text.of("Retour au menu principal"));

        ItemStack item9 = ItemStack.builder().itemType(ItemTypes.GOLD_BLOCK).add(Keys.ITEM_LORE, list9).add(Keys.DISPLAY_NAME, Text.of(Text.builder("Menu principal")
                .color(TextColors.RED).build())).quantity(1).build();

        menu.query(SlotPos.of(0, 0)).set(item1);
        menu.query(SlotPos.of(1, 0)).set(item2);
        menu.query(SlotPos.of(2, 0)).set(item3);
        menu.query(SlotPos.of(3, 0)).set(item4);
        menu.query(SlotPos.of(8, 1)).set(item9);

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
                pPlayer.openInventory(menuPrincipal(pPlayer), Cause.of(NamedCause.simulated(pPlayer)));
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

    private static void teleportMenu(Player pPlayer, String cmd) {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                IsoworldsUtils.cm("TP CMD: " + cmd);
                IsoworldsLocations.teleport(pPlayer, cmd);
            }
        })
                .delay(10, TimeUnit.MILLISECONDS)
                .name("Téléporte le joueur dans un isoworld.").submit(instance);
    }
}
