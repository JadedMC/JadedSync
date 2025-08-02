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
package net.jadedmc.jadedsync;

import net.jadedmc.jadedsync.api.JadedSyncAPI;
import net.jadedmc.jadedsync.api.integration.IntegrationManager;
import net.jadedmc.jadedsync.api.player.JadedSyncPlayerManager;
import net.jadedmc.jadedsync.api.server.InstanceMonitor;
import net.jadedmc.jadedsync.commands.AbstractCommand;
import net.jadedmc.jadedsync.config.ConfigManager;
import net.jadedmc.jadedsync.config.HookManager;
import net.jadedmc.jadedsync.database.Redis;
import net.jadedmc.jadedsync.listeners.PlayerJoinListener;
import net.jadedmc.jadedsync.listeners.PlayerQuitListener;
import net.jadedmc.jadedsync.utils.gui.GUIListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class JadedSyncBukkitPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private HookManager hookManager;
    private InstanceMonitor instanceMonitor;
    private IntegrationManager integrationManager;
    private JadedSyncPlayerManager jadedSyncPlayerManager;
    private Redis redis;

    @Override
    public void onEnable() {
        // Plugin startup logic
        JadedSyncAPI.initialize(this);

        this.integrationManager = new IntegrationManager();
        this.jadedSyncPlayerManager = new JadedSyncPlayerManager();

        configManager = new ConfigManager(this);
        hookManager = new HookManager(this);
        redis = new Redis(this);
        instanceMonitor = new InstanceMonitor(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListeners(), this);

        AbstractCommand.registerCommands(this);
    }

    @Override
    public void onDisable() {
        // Deletes the search from Redis
        redis.del("jadedsync:servers:backend:" + this.instanceMonitor.getCurrentInstance().getName());
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public InstanceMonitor getInstanceMonitor() {
        return this.instanceMonitor;
    }

    public IntegrationManager getIntegrationManager() {
        return this.integrationManager;
    }

    public JadedSyncPlayerManager getJadedSyncPlayerManager() {
        return this.jadedSyncPlayerManager;
    }

    public Redis getRedis() {
        return this.redis;
    }
}
