/*
 * This file is part of JadedSync, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.jadedsync.commands;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.gui.InstancesGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JadedSyncCMD extends AbstractCommand {
    private final JadedSyncBukkitPlugin plugin;

    public JadedSyncCMD(@NotNull final JadedSyncBukkitPlugin plugin) {
        super("jadedsync", "jadedsync.admin", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, @NotNull final String[] args) {
        // If no sub commands are given, display help menu.
        if(args.length == 0) {
            // TODO: Help menu.
            return;
        }

        // Process sub commands.
        switch(args[0].toLowerCase()) {
            case "instances", "servers" -> instancesCMD(sender);
        }
    }

    public void instancesCMD(@NotNull final CommandSender sender) {
        final Player player = (Player) sender;
        new InstancesGUI(plugin).open(player);
    }
}
