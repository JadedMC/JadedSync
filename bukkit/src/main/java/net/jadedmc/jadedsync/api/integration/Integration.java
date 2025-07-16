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
package net.jadedmc.jadedsync.api.integration;

import net.jadedmc.jadedsync.api.player.JadedSyncPlayer;
import net.jadedmc.jadedsync.api.server.CurrentInstance;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a plugin hooking into JadedSync.
 */
public abstract class Integration {
    private final String id;

    /**
     * Creates the integration.
     * @param id ID of the integration.
     */
    public Integration(@NotNull final String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the Integration.
     * @return Integration's ID.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Updates the data being stored by the JadedSyncPlayer.
     * @param player Player to update the data of.
     * @return Updated data in JSON.
     */
    public abstract String getPlayerIntegration(@NotNull final JadedSyncPlayer player);

    /**
     * Updates the data being stored by the ServerInstance.
     * @param serverInstance Server to update the data of.
     * @return Updated data in JSON.
     */
    public abstract String getServerIntegration(@NotNull final CurrentInstance serverInstance);

    /**
     * Runs when a player joins the server.
     * @param player JadedSyncPlayer of the player who joined.
     */
    public void onPlayerJoin(@NotNull final JadedSyncPlayer player) {}
}