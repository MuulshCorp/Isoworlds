/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
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
package sponge.listener;

import common.ManageFiles;
import common.Msg;
import common.action.ChargeAction;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.scheduler.Task;
import sponge.location.Locations;
import sponge.util.console.Logger;
import sponge.Main;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.util.message.Message;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static sponge.Main.instance;

public class Listeners {
    private final Main plugin = instance;

    @Listener
    public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
        Player p = event.getTargetEntity();
        String worldname = (p.getUniqueId() + "-IsoWorld");

        // Teleport to spawn of own isoworld if is loaded
        if (Sponge.getServer().getWorld(worldname).isPresent() && Sponge.getServer().getWorld(worldname).get().isLoaded()) {
            Task.builder().execute(new Runnable() {
                @Override
                public void run() {
                    Locations.teleport(p, worldname);
                }
            }).delay(2, TimeUnit.MILLISECONDS).submit(instance);
        } else {
            // If own isoworld not loaded then go to default world
            if (Sponge.getServer().getWorld("Isolonice").isPresent()) {
                Task.builder().execute(new Runnable() {
                    @Override
                    public void run() {
                        Locations.teleport(p, "Isolonice");
                    }
                }).delay(2, TimeUnit.MILLISECONDS).submit(instance);
            }
        }
    }

    @Listener
    // Anti grief spawn
    public void onDestructSpawn(ChangeBlockEvent.Pre event, @First Player player) {

        String worldname = player.getWorld().getName();

        Player p = player;
        if (p.hasPermission("isoworlds.bypass.spawn")) {
            return;
        }

        if (worldname.equals("Isolonice")) {
            event.setCancelled(true);
        }

        // If break in chunk of spawn layer 60, remove drop
        if (event.getLocations().get(0).getBlockX() == Locations.getAxis(worldname).getX() & event.getLocations().get(0).getBlockZ() == Locations.getAxis(worldname).getZ()) {
            if (event.getLocations().get(0).getBlock().getType() == BlockTypes.DIRT) {
                event.setCancelled(true);
                event.getLocations().get(0).setBlockType(BlockTypes.AIR, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
            }
        }
    }

    // On téléporte tous les joueurs à la déconnexion
    @Listener
    public void onStop(GameStoppingServerEvent event) {
        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        String worldname = ("Isolonice");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(),
                Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getY(), Locations.getAxis(worldname).getZ());
        Location<World> top = Locations.getHighestLoc(maxy)
                .orElse(new Location<>(spawn.getExtent(), Locations.getAxis(worldname).getX(), 61, Locations.getAxis(worldname).getZ()));

        for (Player p : players) {
            p.setLocation(top);
            Logger.info("TP DECO: " + p.getName() + " : " + top.getExtent().getName().toLowerCase());
        }
    }

    @Listener
    public void onConnect(ClientConnectionEvent.Join event) {
        // Message de bienvenue pour IsoWorlds (quelle commande), tutoriel après 5 secondes
        if (ChargeAction.firstTime(event.getTargetEntity().getUniqueId().toString()) == null) {
            Task.builder().execute(new Runnable() {
                @Override
                public void run() {
                    event.getTargetEntity().sendMessage(Text.of(Text.builder("[IsoWorlds]").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(" Sijania vous souhaite la bienvenue sur Isolonice !\n" +
                                    "Dans ce royaume, vous possédez votre propre monde nommé: IsoWorld.\n" +
                                    "Vous êtes seul maître à bord, il est à vous et vous pouvez choisir qui peut y accéder.\n" +
                                    "Essayez dès maintenant via la commande: /iw").color(TextColors.GREEN))).build()));
                    Sponge.getCommandManager().process(event.getTargetEntity(), "iw");
                }
            }).delay(5, TimeUnit.SECONDS).submit(instance);
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Login event) {
        String worldname = ("Isolonice");

        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                Locations.teleport(event.getTargetUser().getPlayer().get(), worldname);
            }
        }).delay(1/5, TimeUnit.SECONDS).submit(instance);
    }

    // Logout event, tp spawn
    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event) {
        String worldname = ("Isolonice");
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(),
                Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getY(), Locations.getAxis(worldname).getZ());
        Location<World> top = Locations.getHighestLoc(maxy)
                .orElse(new Location<>(spawn.getExtent(), Locations.getAxis(worldname).getX(), 61, Locations.getAxis(worldname).getZ()));
        event.getTargetEntity().setLocationSafely(top);
        Logger.info("Joueur téléporté au spawn");
    }

    // Debug load world
    @Listener
    public void onLoadWorld(LoadWorldEvent event) {
        String worldname = event.getTargetWorld().getName();
        World world = event.getTargetWorld();

        // Check si généré de façon anormale, on log le tout pour sanctionner ;.;
        File file = new File(ManageFiles.getPath() + "/" + event.getTargetWorld().getName() + "@PUSHED");
        if (file.exists()) {
            // Anomalie
            Logger.warning("--- Anomalie: LOADING " + event.getTargetWorld().getName() + " WORLD, CAUSED BY: " + event.getCause().toString() + " ---");
            // Check if players in there
            for (Player p : world.getPlayers()) {
                p.kick(Text.of("Vous faites quelque chose d'anormal, veuillez avertir le staff"));
                Logger.severe("--- Le joueur " + p.getName() + " fait partie de l'anomalie ---");
            }
            // Déchargement et suppression
            Sponge.getServer().unloadWorld(world);
            Sponge.getServer().deleteWorld(world.getProperties());
        } else {
            Logger.info("LOADING " + event.getTargetWorld().getName() + " WORLD, CAUSED BY: " + event.getCause().toString());
        }
    }

    // TP lors du unload d'un monde
    @Listener
    public void onUnloadWorld(UnloadWorldEvent event) {
        String worldname = ("Isolonice");
        World world = event.getTargetWorld();
        Location<World> spawn = Sponge.getServer().getWorld(worldname).get().getSpawnLocation();
        Location<World> maxy = new Location<>(spawn.getExtent(),
                Locations.getAxis(worldname).getX(), Locations.getAxis(worldname).getY(), Locations.getAxis(worldname).getZ());
        Location<World> top = Locations.getHighestLoc(maxy)
                .orElse(new Location<>(spawn.getExtent(), Locations.getAxis(worldname).getX(), 61, Locations.getAxis(worldname).getZ()));

        // Kick players
        for (Player p : event.getTargetWorld().getPlayers()) {
            p.setLocation(top);
            Logger.info("TP UNLOAD: " + p.getName() + " : " + top.getExtent().getName().toLowerCase());
        }

        // Check if file exist, to detect mirrors
        File file = new File(ManageFiles.getPath() + "/" + event.getTargetWorld().getName() + "@PUSHED");
        File file2 = new File(ManageFiles.getPath() + "/" + event.getTargetWorld().getName());
        // If exists and contains Isoworld
        if (file.exists() & event.getTargetWorld().getName().contains("-IsoWorld")) {
            // Anomalie
            Logger.severe("--- Anomalie: UNLOADING " + event.getTargetWorld().getName() + " WORLD, CAUSED BY: " + event.getCause().toString() + " ---");
            // Check if players in there
            for (Player p : world.getPlayers()) {
                p.kick(Text.of("Vous faites quelque chose d'anormal, veuillez avertir le staff"));
                Logger.warning("--- Le joueur " + p.getName() + " fait partie de l'anomalie ---");
            }
            return;
        } else {
            Logger.info("UNLOADING " + event.getTargetWorld().getName() + " WORLD, CAUSED BY: " + event.getCause().toString());
        }
    }

    @Listener
    public void onPlayerChangeWorld(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player pPlayer) {
        final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_p;
        String check_w;

        Logger.info("[TRACKING-IW] Téléporation: " + pPlayer.getName() + " [FROM: " + event.getFromTransform().toString() + "] - [TO: " + event.getToTransform().toString() + "] - [CAUSE: "
                + event.getCause().toString() + "]");

        String eventworld = event.getToTransform().getExtent().getName();

        // Check if world folder is present
        File checkFolder = new File(ManageFiles.getPath() + eventworld);
        if (!checkFolder.exists() & eventworld.contains("IsoWorld")) {
            event.setCancelled(true);
            Logger.warning("Isoworld non actif, téléporation annulée !");
            return;
        }

        Sponge.getServer().loadWorld(event.getToTransform().getExtent().getName());

        if (eventworld.contains("-IsoWorld")) {
            try {
                PreparedStatement check = plugin.database.prepare(CHECK);
                // UUID_P
                check_p = pPlayer.getUniqueId().toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = eventworld;
                check.setString(2, check_w);
                // Requête
                check.setString(3, plugin.servername);
                ResultSet rselect = check.executeQuery();
                Logger.info("Monde event: " + eventworld);
                if (pPlayer.hasPermission("isoworlds.bypass.teleport")) {
                    return;
                }

                if (rselect.isBeforeFirst()) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION).color(TextColors.AQUA))).build()));
                    // Cas du untrust, pour ne pas rester bloquer
                } else if (pPlayer.getWorld().getName() == eventworld) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION).color(TextColors.AQUA))).build()));
                } else {
                    event.setCancelled(true);
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.DENY_TELEPORT).color(TextColors.AQUA))).build()));
                }

            } catch (Exception se) {
                pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            }

        }
    }

    @Listener
    public void onInteractSpawn(HandInteractEvent event, @First Player p) {
        if (!event.getInteractionPoint().isPresent()) {
            return;
        }
        // Allow break if permission
        if (p.hasPermission("isoworlds.bypass.spawn")) {
            return;
        }
        if (p.getWorld().getName().equals("Isolonice")) {
            event.setCancelled(true);
        }
    }

}
