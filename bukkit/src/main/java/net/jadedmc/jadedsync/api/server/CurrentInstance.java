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
package net.jadedmc.jadedsync.api.server;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.JadedSyncAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Stores information from the current server instance, as obtained through Redis.
 */
public class CurrentInstance {
    private final JadedSyncBukkitPlugin plugin;
    private final String name;
    private final long startTime;
    private InstanceStatus status;
    private final int majorVersion;
    private final int minorVersion;
    private String address;
    private final int port;
    private final Map<String, String> integrations = new HashMap<>();
    private final Collection<String> tags = new ArrayList<>();

    /**
     * Creates the CurrentInstance object.
     * @param plugin Instance of the plugin.
     */
    public CurrentInstance(@NotNull final JadedSyncBukkitPlugin plugin) {
        this.plugin = plugin;
        this.name = plugin.getConfigManager().getConfig().getString("Server.name");
        this.startTime = System.currentTimeMillis();
        this.status = InstanceStatus.ONLINE;

        final String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        this.majorVersion = Integer.parseInt(version.split("\\.")[1]);
        this.minorVersion = Integer.parseInt(version.split("\\.")[2]);

        // Get the IP address of the machine it's being run on.
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.address = socket.getLocalAddress().getHostAddress();
        }
        catch (UnknownHostException | SocketException exception) {
            this.address = "0.0.0.0";
        }

        this.port = plugin.getServer().getPort();

        // Load tags.
        if(plugin.getConfigManager().getConfig().contains("Server.tags")) {
            this.tags.addAll(plugin.getConfigManager().getConfig().getStringList("Server.tags"));
        }

        this.updateIntegrations();
    }

    /**
     * Add a tag to the Instance.
     * @param tag Tag to add.
     */
    public void addTag(@NotNull final String tag) {
        this.tags.add(tag);
    }

    /**
     * Gets the address of the machine the instance is running on.
     * @return Instance address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Get the maximum capacity of the Instance.
     * @return Maximum number of players the Instance can hold.
     */
    public int getCapacity() {
        return this.plugin.getServer().getMaxPlayers();
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
     * Get the major version of the server.
     * E.G 1.X.4
     * @return Server major version.
     */
    public int getMajorVersion() {
        return this.majorVersion;
    }

    /**
     * Get the minor version of the server.
     * E.G 1.20.X
     * @return Server minor version.
     */
    public int getMinorVersion() {
        return this.minorVersion;
    }

    /**
     * Get the name of the Instance.
     * @return Instance name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the number of players currently on the Instance.
     * @return Players online.
     */
    public int getOnline() {
        return this.plugin.getServer().getOnlinePlayers().size();
    }

    /**
     * Get the number of slots available for players to join.
     * @return Number of open spots.
     */
    public int getOpenSlots() {
        return this.getCapacity() - this.getOnline();
    }

    /**
     * Get the port the Instance is running on.
     * @return Port of the Instance.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Get the time (in milliseconds since epoch) that the server was started.
     * @return Server start time.
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Gets the current status of the Instance.
     * @return Instance Status.
     */
    public InstanceStatus getStatus() {
        return this.status;
    }

    /**
     * Gets all tags for the instance.
     * @return Instance tags.
     */
    public Collection<String> getTags() {
        return this.tags;
    }

    /**
     * Get how long (in ms) the server has been up for.
     * @return Server uptime in milliseconds.
     */
    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Check if the instance has a given tag.
     * @param tag Tag to check.
     * @return Whether the instance has that tag.
     */
    public boolean hasTag(@NotNull final String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Sends a heartbeat message to Redis, containing various data about the instance.
     */
    public void heartbeat() {
        final Document document = new Document()
                .append("serverName", name)
                .append("status", getStatus().toString())
                .append("online", getOnline())
                .append("capacity", getCapacity())
                .append("heartbeat", System.currentTimeMillis())
                .append("address", getAddress())
                .append("port", getPort())
                .append("startTime", getStartTime())
                .append("majorVersion", this.majorVersion)
                .append("minorVersion", this.minorVersion)
                .append("tags", this.tags);

        // Add all online players to the document.
        final List<String> players = new ArrayList<>();
        for(final Player player : plugin.getServer().getOnlinePlayers()) {
            players.add(player.getUniqueId().toString());
        }
        document.append("players", players);

        // Update integrations
        this.updateIntegrations();


        // Store integrations.
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

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getRedis().set("jadedsync:servers:backend:" + this.name, document.toJson());
        });
    }

    /**
     * Remove a tag from the Instance.
     * @param tag Tag to remove.
     */
    public void removeTag(@NotNull final String tag) {
        this.tags.remove(tag);
    }

    /**
     * Updates the status of the instance.
     * @param status New Instance Status.
     */
    public void setStatus(final InstanceStatus status) {
        // If the server is closed and empty, shut it down.
        if(status == InstanceStatus.CLOSED && this.getOnline() == 0) {
            plugin.getServer().shutdown();
            return;
        }

        this.status = status;
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
        this.integrations.put(integration, plugin.getIntegrationManager().getIntegration(integration).getServerIntegration(this));
    }

    /**
     * Updates the cached data for all integrations.
     */
    public void updateIntegrations() {
        plugin.getIntegrationManager().getIntegrations().forEach(integration -> this.integrations.put(integration.getId(), integration.getServerIntegration(this)));
    }
}