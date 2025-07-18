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
import net.jadedmc.jadedsync.api.JadedSyncAPI;
import net.jadedmc.jadedsync.api.player.JadedSyncPlayer;
import net.jadedmc.jadedsync.api.server.InstanceStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {
    private final JadedSyncBukkitPlugin plugin;

    public PlayerJoinListener(@NotNull final JadedSyncBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Mark the server as full if it reaches capacity.
        if(plugin.getServer().getOnlinePlayers().size() == plugin.getServer().getMaxPlayers()) {
            plugin.getInstanceMonitor().getCurrentInstance().setStatus(InstanceStatus.FULL);
        }

        JadedSyncAPI.getPlayerAsync(player.getUniqueId()).whenComplete((jadedSyncPlayer, exception) -> {
           plugin.getServer().getScheduler().runTask(plugin, () -> {

               // Print exception to the console if one is thrown.
               if(exception != null) {
                   exception.printStackTrace();
                   return;
               }

               // Creates a new player if they do not already exist.
               if(jadedSyncPlayer == null) {
                    final JadedSyncPlayer newPlayer = new JadedSyncPlayer(plugin, player);
                    plugin.getJadedSyncPlayerManager().addPlayer(newPlayer);
                    plugin.getIntegrationManager().getIntegrations().forEach(integration -> integration.onPlayerJoin(newPlayer));
                    newPlayer.syncData();
               }
               // Loads player data if they do already exist.
               else {
                   plugin.getJadedSyncPlayerManager().addPlayer(jadedSyncPlayer);
                   plugin.getIntegrationManager().getIntegrations().forEach(integration -> integration.onPlayerJoin(jadedSyncPlayer));
                   jadedSyncPlayer.updateIntegrations();
                   jadedSyncPlayer.syncData();
               }
           });
        });
    }
}