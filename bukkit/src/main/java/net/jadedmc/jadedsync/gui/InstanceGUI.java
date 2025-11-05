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
import net.jadedmc.jadedsync.utils.item.Skull;
import net.jadedmc.jadedsync.utils.item.SkullBuilder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class InstanceGUI extends CustomGUI {

    public InstanceGUI(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final ServerInstance instance, final CustomGUI previous) {
        super(54, instance.getName());
        addFiller(0,1,2,3,5,6,7,8,45,46,47,48,49,50,51,52,53);

        // Adds the Server Icon.
        {
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

            setItem(4, builder.build());
        }

        final ItemBuilder playersItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("<primary><bold>Players");
        setItem(19, playersItem.build(), (p,a) -> new InstancePlayersGUI(plugin, instance).open(p));

        final ItemBuilder statusItem = new ItemBuilder(Material.OAK_SIGN)
                .setDisplayName("<primary><bold>Change Status")
                .addLore("<red>Coming Soon");
        setItem(22, statusItem.build());

        final ItemBuilder tagsItem = new ItemBuilder(Material.NAME_TAG).setDisplayName("<primary><bold>Tags");
        instance.getTags().forEach(tag -> tagsItem.addLore("<gray>  - " + tag));
        setItem(25, tagsItem.build());

        // Adds the back button.
        if(previous != null) {
            setItem(0, new SkullBuilder().fromSkull(Skull.RED_LEFT_ARROW).asItemBuilder().setDisplayName("<red>Back").build(), (p, a) -> new InstancesGUI(plugin).open(p));
        }
    }
}
