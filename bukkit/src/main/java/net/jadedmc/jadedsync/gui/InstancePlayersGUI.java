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
import net.jadedmc.jadedsync.api.JadedSyncAPI;
import net.jadedmc.jadedsync.api.player.JadedSyncPlayer;
import net.jadedmc.jadedsync.api.server.ServerInstance;
import net.jadedmc.jadedsync.utils.gui.CustomGUI;
import net.jadedmc.jadedsync.utils.item.ItemBuilder;
import net.jadedmc.jadedsync.utils.item.SkullBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InstancePlayersGUI extends CustomGUI {

    /**
     * Creates a new custom GUI
     */
    public InstancePlayersGUI(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final ServerInstance instance) {
        super(54, instance.getName() + " - Players");
        addFiller(0,1,2,3,5,6,7,8);

        final ItemBuilder instanceItem = new ItemBuilder(instance.getStatus().getIconMaterial())
                .setDisplayName(instance.getStatus().getColor() + instance.getName())
                .addLore("<primary>Players: <white>" + instance.getOnline() + "<secondary>/<white>" + instance.getCapacity())
                .addLore("")
                .addLore(instance.getStatus().getDisplayName());
        setItem(4, instanceItem.build());

        JadedSyncAPI.getPlayersAsync(instance.getPlayers()).whenComplete((players, exception) -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                int i = 9;

                for(final JadedSyncPlayer player : players) {
                    if(i == 54) {
                        break;
                    }

                    final ItemStack playerHead = new SkullBuilder()
                            .fromBase64(player.getSkin())
                            .asItemBuilder()
                            .setDisplayName("<primary>" + player.getName())
                            .build();
                    setItem(i, playerHead);

                    i++;
                }
            });
        });
    }
}