/*
 * This file is part of Isoworlds, licensed under the MIT License (MIT).
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
package bukkit.util.task.SAS;

import bukkit.Main;
import bukkit.util.message.Message;
import common.Msg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class Pull extends BukkitRunnable {
    private int check = 20;
    private Player pPlayer;
    private File file;
    public static Main instance;


    public Pull(Player pPlayer, File file) {
        this.pPlayer = pPlayer;
        this.file = file;
        instance = Main.getInstance();
    }

    @Override
    public void run() {
        if (check == 20) {
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("ProcessingPull")));
        }
        check --;
        if (check < 1) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("FailPull")));
            instance.lock.remove(file.getName() + ";" + file.getName());
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            cancel();
        } else if (file.exists()) {
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("SuccessPull")));
            instance.lock.remove(file.getName() + ";" + file.getName());
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            cancel();
        }
    }
}