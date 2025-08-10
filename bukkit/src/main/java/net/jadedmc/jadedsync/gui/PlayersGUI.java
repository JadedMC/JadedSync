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
package net.jadedmc.jadedsync.gui;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.JadedSyncAPI;
import net.jadedmc.jadedsync.api.player.JadedSyncPlayer;
import net.jadedmc.jadedsync.utils.gui.CustomGUI;
import net.jadedmc.jadedsync.utils.item.SkullBuilder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

/**
 * Displays a GUI of all players online on the network.
 */
public class PlayersGUI extends CustomGUI {

    /**
     * Creates a new custom GUI
     */
    public PlayersGUI(@NotNull final JadedSyncBukkitPlugin plugin) {
        super(54, "Network - Players");
        addFiller(0,1,2,3,4,5,6,7,8);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Get all players and sort them by username.
            final TreeMap<String, JadedSyncPlayer> players = new TreeMap<>();
            for(final JadedSyncPlayer player : JadedSyncAPI.getPlayers().values()) {
                players.put(player.getName(), player);
            }

            // Build the GUI in the main thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                int i = 9;

                // Add the players to the GUI.
                for(final JadedSyncPlayer player : players.values()) {
                    // Exit if the GUI full.
                    if(i == 54) {
                        break;
                    }

                    // Display's the item.
                    final ItemStack playerHead = new SkullBuilder()
                            .fromBase64(player.getSkin())
                            .asItemBuilder()
                            .setDisplayName("<primary>" + player.getName())
                            .addLore("<gray>Server: <primary>" + player.getServer())
                            .addLore("<gray>Online For: <primary>" + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - player.getJoinedTime(), true, true))
                            .build();
                    setItem(i, playerHead);

                    i++;
                }
            });
        });
    }
}