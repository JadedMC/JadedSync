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

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Stores information from a server instance, as obtained through Redis.
 */
public class ServerInstance {
    private final String name;
    private final int online;
    private final int capacity;
    private final InstanceStatus status;
    private final long lastHeartbeat;
    private final int majorVersion;
    private final int minorVersion;
    private final String address;
    private final int port;
    private final long startTime;
    private final List<UUID> players = new ArrayList<>();
    private final Map<String, String> integrations = new HashMap<>();

    /**
     * Creates an instance with a given BSON document.
     * @param document Document to create instance with.
     */
    public ServerInstance(@NotNull final Document document) {
        this.name = document.getString("serverName");
        this.online = document.getInteger("online");
        this.capacity = document.getInteger("capacity");
        this.majorVersion = document.getInteger("majorVersion");
        this.minorVersion = document.getInteger("minorVersion");
        this.address = document.getString("address");
        this.port = document.getInteger("port");
        this.lastHeartbeat = document.getLong("heartbeat");
        this.startTime = document.getLong("startTime");

        if((System.currentTimeMillis() - lastHeartbeat > 90000)) {
            // If the server has not responded in 90 seconds, mark it as unresponsive.
            this.status = InstanceStatus.UNRESPONSIVE;
        }
        else if(capacity == online) {
            // If the server is at capacity, mark it as full.
            this.status = InstanceStatus.FULL;
        }
        else {
            // Otherwise, read the status from the document.
            this.status = InstanceStatus.valueOf(document.getString("status"));
        }

        // Load online players.
        for(final String uuid : document.getList("players", String.class)) {
            players.add(UUID.fromString(uuid));
        }

        // Load integrations.
        final Document integrationsDocument = document.get("integrations", Document.class);
        for(final String integration : integrations.keySet()) {
            this.integrations.put(integration, integrationsDocument.get(integration, Document.class).toJson());
        }
    }

    /**
     * Create an Instance with a JSON String.
     * Does so by creating a BSON document with the JSON.
     * @param json JSON to create instance with.
     */
    public ServerInstance(@NotNull final String json) {
        this(Document.parse(json));
    }

    /**
     * Gets the address of the machine the instance is running on.
     * @return Instance address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the maximum capacity of the Instance.
     * @return Maximum number of players the Instance can hold.
     */
    public int getCapacity() {
        return capacity;
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
     * Get the last time (in ms since epoch) that the Instance sent a heartbeat message.
     * @return Last time a heartbeat message was sent.
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * Get the name of the Instance.
     * @return Instance name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of players currently on the Instance.
     * @return Players online.
     */
    public int getOnline() {
        return online;
    }

    /**
     * Get the number of slots available for players to join.
     * @return Number of open spots.
     */
    public int getOpenSlots() {
        return getCapacity() - getOnline();
    }

    /**
     * Get a list of all players online on the instance.
     * @return Players on the instance.
     */
    public List<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Get the port the Instance is running on.
     * @return Port of the Instance.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the time (in milliseconds since epoch) that the server was started.
     * @return Server start time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the current status of the Instance.
     * @return Instance Status.
     */
    public InstanceStatus getStatus() {
        return status;
    }

    /**
     * Get how long (in ms) the server has been up for.
     * @return Server uptime in milliseconds.
     */
    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Get the full version the server is running, as a String.
     * @return String form of the server version.
     */
    public String getVersion() {
        return "1." + majorVersion + "." + minorVersion;
    }
}