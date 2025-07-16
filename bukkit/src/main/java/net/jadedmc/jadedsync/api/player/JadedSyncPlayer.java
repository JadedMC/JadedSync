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

import com.destroystokyo.paper.profile.ProfileProperty;
import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.JadedSyncAPI;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player being synced across the network.
 * Data is stored in Redis
 */
public class JadedSyncPlayer {
    private final JadedSyncBukkitPlugin plugin;
    private final UUID uuid;
    private final String name;
    private String skin = "";
    private final Map<String, String> integrations = new HashMap<>();
    private final long joinedTime;

    /**
     * Creates a JadedSyncPlayer from a Bukkit Player object.
     * Used when creating a new player not already saved to Redis.
     * @param plugin Instance of the plugin.
     * @param player Player to save.
     */
    public JadedSyncPlayer(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.joinedTime = System.currentTimeMillis();

        for(final ProfileProperty property : player.getPlayerProfile().getProperties()) {
            if(!property.getName().equals("textures")) {
                continue;
            }

            this.skin = property.getValue();
        }

        this.updateIntegrations();
    }

    /**
     * Creates a JadedSyncPlayer from given Json data.
     * Used when loading a player from Redis.
     * @param plugin Instance of the plugin.
     * @param json Json data that makes up the player.
     */
    public JadedSyncPlayer(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final String json) {
        this.plugin = plugin;

        final Document document = Document.parse(json);

        this.uuid = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name");
        this.skin = document.getString("skin");
        this.joinedTime = document.getLong("joinedTime");

        final Document integrationsDocument = document.get("integrations", Document.class);
        for(final String integration : integrations.keySet()) {
            this.integrations.put(integration, integrationsDocument.get(integration, Document.class).toJson());
        }
    }

    /**
     * Deletes saved data from a specific integration.
     * @param integration ID of the integration to delete.
     */
    public void deleteIntegration(@NotNull final String integration) {
        this.integrations.remove(integration);
    }

    /**
     * Gets the Json data being cached by a given integration.
     * @param integration Integration to get the data of.
     * @return Json data.
     */
    public String getIntegration(@NotNull final String integration) {
        if(this.integrations.containsKey(integration)) {
            return this.integrations.get(integration);
        }

        return "";
    }

    /**
     * Get the time (in ms since epoch) that the player joined the network.
     * @return Time the player joined.
     */
    public long getJoinedTime() {
        return this.joinedTime;
    }

    /**
     * Get the player's username.
     * @return Username of the player.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the player's UUID.
     * @return UUID.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Syncs the player's data to Redis.
     */
    public void syncData() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getRedis().set("jadedsync:players:" + this.uuid.toString(), this.toJson());
        });
    }

    /**
     * Get the Json representation of the player.
     * @return Json representing the player.
     */
    public String toJson() {
        final Document document = new Document()
                .append("uuid", this.uuid.toString())
                .append("name", this.name)
                .append("skin", this.skin)
                .append("joinedTime", this.joinedTime);

        final Document integrationsDocument = new Document();

        // Get data saved from the integration.
        for(final String integration : this.integrations.keySet()) {
            final String json = this.integrations.get(integration);

            // Skip integrations that are empty.
            if(json == null || json.isEmpty()) {
                continue;
            }

            final Document integrationDocument = Document.parse(json);
            integrationsDocument.append(integration, integrationDocument);
        }

        document.append("integrations", integrationsDocument);

        // Convert Bson to Json.
        return document.toJson();
    }

    /**
     * Get the base64 encoding of the player's skin.
     * Useful for displaying player heads cross-server.
     * @return Base64 skin encoding.
     */
    public String getSkin() {
        return this.skin;
    }

    /**
     * Gets the Player object being represented.
     * @return Bukkit Player being cached, null if not online.
     */
    public Player toPlayer() {
        return plugin.getServer().getPlayer(this.uuid);
    }

    /**
     * Updates the cached data for a specific integration.
     * @param integration ID of the integration to update.
     */
    public void updateIntegration(@NotNull final String integration) {
        // Makes sure the integration exists before trying to update it.
        if(!JadedSyncAPI.hasIntegration(integration)) {
            return;
        }

        // Updates the data saved by the integration.
        this.integrations.put(integration, plugin.getIntegrationManager().getIntegration(integration).getPlayerIntegration(this));
    }

    /**
     * Updates the cached data for all integrations.
     */
    public void updateIntegrations() {
        plugin.getIntegrationManager().getIntegrations().forEach(integration -> this.integrations.put(integration.getId(), integration.getPlayerIntegration(this)));
    }
}