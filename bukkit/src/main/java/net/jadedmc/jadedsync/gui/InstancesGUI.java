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
package net.jadedmc.jadedsync.gui;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.server.InstanceStatus;
import net.jadedmc.jadedsync.api.server.ServerInstance;
import net.jadedmc.jadedsync.utils.gui.CustomGUI;
import net.jadedmc.jadedsync.utils.item.ItemBuilder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

/**
 * Opens a GUI displaying all
 */
public class InstancesGUI extends CustomGUI {

    /**
     * Creates the GUI.
     * @param plugin Instance of the plugin.
     */
    public InstancesGUI(@NotNull final JadedSyncBukkitPlugin plugin) {
        super(54, "Instances");

        plugin.getInstanceMonitor().getInstancesAsync().whenComplete((instances,exception) -> {
            // Sort instances by name.
            final TreeMap<String, ServerInstance> sortedInstances = new TreeMap<>();
            for(final ServerInstance instance : instances) {
                sortedInstances.put(instance.getName(), instance);
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                int slot = 0;
                for(final String name : sortedInstances.keySet()) {
                    final ServerInstance instance = sortedInstances.get(name);

                    // Exit if no more room.
                    if(slot > 53) {
                        break;
                    }

                    // Creates the item.
                    final ItemBuilder builder = new ItemBuilder(instance.getStatus().getIconMaterial())
                            .setDisplayName(instance.getStatus().getColor() + instance.getName())
                            .addLore("<gray>Version: " + instance.getStatus().getColor() + instance.getVersion())
                            .addLore("<gray>Online: " + instance.getStatus().getColor() + instance.getOnline() + "<gray>/" + instance.getStatus().getColor() + instance.getCapacity())
                            .addLore("<gray>Address: " + instance.getStatus().getColor() + instance.getAddress() + ":"  + instance.getPort());

                    // Show Uptime or Time since last heartbeat, depending on if the server has responded.
                    if(instance.getStatus() == InstanceStatus.UNRESPONSIVE) {
                        builder.addLore("<gray>Last Heartbeat: " + instance.getStatus().getColor() + DurationFormatUtils.formatDurationWords( - System.currentTimeMillis() - instance.getLastHeartbeat(), true, true) + " ago");
                    }
                    else {
                        builder.addLore("<gray>Uptime: " + instance.getStatus().getColor() + DurationFormatUtils.formatDurationWords(instance.getUptime(), true, true));
                    }


                    builder.addLore("").addLore(instance.getStatus().getDisplayName());
                    setItem(slot, builder.build(), (p,a) -> new InstanceGUI(plugin, instance, this).open(p));

                    slot++;
                }
            });
        });
    }
}