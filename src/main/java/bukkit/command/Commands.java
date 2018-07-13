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
package bukkit.command;

import bukkit.command.sub.*;
import bukkit.util.Inventories;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player pPlayer = (Player) sender;

        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("creation") || arg.equals("créer") || arg.equals("creer") || arg.equals("create") || arg.equals("c")) {
                Create.Creation(sender, args);
                return true;
            } else if (arg.equals("lister") || arg.equals("liste") || arg.equals("list") || arg.equals("l")) {
                ListWorlds.Liste(sender, args);
                return true;
            } else if (arg.equals("refonte") || arg.equals("refondre") || arg.equals("r")) {
                Reforge.Refonte(sender, args);
                return true;
            } else if (arg.equals("maison") || arg.equals("home") || arg.equals("h")) {
                Home.Maison(sender, args);
                return true;
                //} else if (arg.equals("off") || arg.equals("desactiver") || arg.equals("désactiver") || arg.equals("décharger") || arg.equals("unload")) {
                //    OffCommande.Off(sender, args);
                //    return true;
                //} else if (arg.equals("on") || arg.equals("charger") || arg.equals("activer") || arg.equals("load")) {
                //    OnCommande.On(sender, args);
                //    return true;
                //} else if (arg.equals("teleport") || arg.equals("tp") || arg.equals("teleportation")) {
                //    TeleportCommande.Teleport(sender, args);
                //    return true;
            } else if (arg.equals("confiance") || arg.equals("trust") || arg.equals("a")) {
                Trust.Confiance(sender, args);
                return true;
            } else if (arg.equals("retirer") || arg.equals("supprimer") || arg.equals("untrust") || arg.equals("remove")) {
                Untrust.RetirerConfiance(sender, args);
                return true;
            } else if (arg.equals("météo") || arg.equals("meteo") || arg.equals("m")) {
                Weather.Meteo(sender, args);
                return true;
            } else if (arg.equals("time") || arg.equals("temps") || arg.equals("t") || arg.equals("cycle")) {
                Time.Time(sender, args);
                return true;
            } else if (arg.equals("biome") || arg.equals("biomes") || arg.equals("b")) {
                Biome.Biome(sender, args);
                return true;
            } else if (arg.equals("warp") || arg.equals("w")) {
                Warp.Warp(sender, args);
                return true;
            } else {
                return true;
            }
        } else {
            Inventories.MenuPrincipal(pPlayer).open(pPlayer);
            //IsoworldsUtils.getHelp(sender);
            return true;
        }
    }
}