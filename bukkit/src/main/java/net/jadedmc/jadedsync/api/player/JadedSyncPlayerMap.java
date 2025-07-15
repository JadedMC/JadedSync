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
package net.jadedmc.jadedsync.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class JadedSyncPlayerMap extends HashMap<UUID, JadedSyncPlayer> {
    /**
     * Check if the map contains a player with a given username.
     * @param username Username to check.
     * @return true if the player is contained, false if they are not.
     */
    public boolean contains(@NotNull final String username) {
        for(final JadedSyncPlayer syncPlayer : this.values()) {
            if(syncPlayer.getName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the map contains a player with a given uuid.
     * @param uuid UUID to check.
     * @return true if the player is contained, false if they are not.
     */
    public boolean contains(@NotNull final UUID uuid) {
        return containsKey(uuid);
    }

    /**
     * Check if the map contains a player with a given Bukkit Player.
     * @param player Player to check.
     * @return true if the player is contained, false if they are not.
     */
    public boolean contains(@NotNull final Player player) {
        return contains(player.getUniqueId());
    }

    /**
     * Get a player from their username.
     * @param username Username of the player to get.
     * @return The associated player, null if not found.
     */
    public JadedSyncPlayer get(@NotNull final String username) {
        for(final JadedSyncPlayer syncPlayer : this.values()) {
            if(syncPlayer.getName().equalsIgnoreCase(username)) {
                return syncPlayer;
            }
        }

        return null;
    }
}
