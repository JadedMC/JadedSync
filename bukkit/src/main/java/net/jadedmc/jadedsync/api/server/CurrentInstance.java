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
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
     * Get how long (in ms) the server has been up for.
     * @return Server uptime in milliseconds.
     */
    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
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
                .append("majorVersion", majorVersion)
                .append("minorVersion", minorVersion);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getRedis().set("jadedsync:servers:backend:" + this.name, document.toJson());
        });
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
}