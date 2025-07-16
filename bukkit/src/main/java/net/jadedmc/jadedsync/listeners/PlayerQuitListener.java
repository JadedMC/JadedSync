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
package net.jadedmc.jadedsync.listeners;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.server.InstanceStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {
    private final JadedSyncBukkitPlugin plugin;

    public PlayerQuitListener(@NotNull final JadedSyncBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        // Remove the player from the local cache.
        plugin.getJadedSyncPlayerManager().removePlayer(event.getPlayer().getUniqueId());

        // If the server is full and someone leaves, it is no longer full.
        if(plugin.getInstanceMonitor().getCurrentInstance().getStatus() == InstanceStatus.FULL) {
            plugin.getInstanceMonitor().getCurrentInstance().setStatus(InstanceStatus.ONLINE);
        }

        // Shut down the server if it is closed and empty.
        if(plugin.getServer().getOnlinePlayers().size() == 1 && plugin.getInstanceMonitor().getCurrentInstance().getStatus() == InstanceStatus.CLOSED) {
            System.out.println("Empty Server Detected. Waiting 15 seconds before shutdown.");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getServer().shutdown(),15*20);
        }
    }
}