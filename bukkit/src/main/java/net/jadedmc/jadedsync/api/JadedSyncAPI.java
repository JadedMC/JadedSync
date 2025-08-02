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
package net.jadedmc.jadedsync.api;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.integration.Integration;
import net.jadedmc.jadedsync.api.player.JadedSyncPlayer;
import net.jadedmc.jadedsync.database.Redis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JadedSyncAPI {
    private static JadedSyncBukkitPlugin plugin;

    /**
     * Initializes the API.
     * @param pl Instance of the PlayerPasswords plugin.
     */
    public static void initialize(@NotNull final JadedSyncBukkitPlugin pl) {
        plugin = pl;
    }

    public static Integration getIntegration(@NotNull final String id) {
        return plugin.getIntegrationManager().getIntegration(id);
    }

    public static Collection<Integration> getIntegrations() {
        return plugin.getIntegrationManager().getIntegrations();
    }

    /**
     *
     * @return
     */
    public static Redis getRedis() {
        return plugin.getRedis();
    }

    public static boolean hasIntegration(@NotNull final String integration) {
        return (plugin.getIntegrationManager().getIntegration(integration) != null);
    }

    /**
     * Gets the JadedSyncPlayer of a player with the given UUID.
     * <p><b>Warning: This is done on the thread this is called from. Should be used asynchronously.</b></p>
     * @param uuid UUID of the player to check.
     * @return JadedSyncPlayer object. Null if does not exist.
     */
    public static JadedSyncPlayer getPlayer(@NotNull final UUID uuid) {
        // Check if a local copy of the player exists first.
        final JadedSyncPlayer localPlayer = plugin.getJadedSyncPlayerManager().getPlayer(uuid);
        if(localPlayer != null) {
            return localPlayer;
        }

        // If not, check if the player is saved in Redis at all.
        if(!hasPlayer(uuid)) {
            return null;
        }

        // If so, gets that player.
        return new JadedSyncPlayer(plugin, plugin.getRedis().get("jadedsync:players:" + uuid));
    }

    public static CompletableFuture<JadedSyncPlayer> getPlayerAsync(@NotNull final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayer(uuid));
    }

    public static List<JadedSyncPlayer> getPlayers(@NotNull final Collection<UUID> uuids) {
        final List<JadedSyncPlayer> players = new ArrayList<>();

        for(final UUID uuid : uuids) {
            final JadedSyncPlayer player = getPlayer(uuid);

            // Skip the player if they do not exist.
            if(player == null) {
                continue;
            }

            players.add(player);
        }

        return players;
    }

    public static CompletableFuture<List<JadedSyncPlayer>> getPlayersAsync(@NotNull final Collection<UUID> uuids) {
        return CompletableFuture.supplyAsync(() -> getPlayers(uuids));
    }

    /**
     * Check if a given player is already cached in Redis.
     * <p><b>Warning: This is done on the thread this is called from. Should be used asynchronously.</b></p>
     * @param uuid UUID of the player to check.
     * @return true if they are in Redis, false if they are not.
     */
    public static boolean hasPlayer(@NotNull final UUID uuid) {
        return plugin.getRedis().exists("jadedsync:players:" + uuid);
    }

    public static CompletableFuture<Boolean> hasPlayerAsync(@NotNull final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> hasPlayer(uuid));
    }
}