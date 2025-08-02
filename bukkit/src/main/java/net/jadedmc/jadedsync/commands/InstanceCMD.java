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
package net.jadedmc.jadedsync.commands;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.server.CurrentInstance;
import net.jadedmc.jadedsync.api.server.InstanceStatus;
import net.jadedmc.jadedsync.api.server.ServerInstance;
import net.jadedmc.jadedsync.gui.InstancePlayersGUI;
import net.jadedmc.jadedsync.utils.chat.ChatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Runs the /instance command, which allows the sender to manage and control remote instances.
 */
public class InstanceCMD extends AbstractCommand {
    private final JadedSyncBukkitPlugin plugin;

    public InstanceCMD(@NotNull final JadedSyncBukkitPlugin plugin) {
        super("instance", "jadedsync.admin", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        // If no sub commands are given, display the statistics of the current instance.
        if(args.length == 0) {
            viewCurrentInstanceCMD(sender);
            return;
        }

        // Process sub commands.
        switch(args[0].toLowerCase()) {
            case "close" -> closeCMD(sender, args);
            case "open" -> openCMD(sender, args);
            case "players" -> playersCMD(sender, args);
            case "view" -> viewRemoteInstance(sender, args);
        }
    }

    /**
     * Marks a given instance as closed.
     * @param sender Command Sender.
     * @param args command arguments.
     */
    private void closeCMD(@NotNull final CommandSender sender, final String[] args) {
        // If too many arguments are given, explain how to use the command.
        if(args.length > 2) {
            ChatUtils.chat(sender, "<red><bold>Usage</bold></red> <dark_gray>»</dark_gray> <red>/instance close [server]</red>");
            return;
        }

        // If the only argument is the "close", close the current instance.
        if(args.length == 1) {
            ChatUtils.chat(sender, "<primary><bold>Instance</bold> <dark_gray>» <primary>Instance has been marked as " + InstanceStatus.CLOSED.getDisplayName() + "<primary>.");
            plugin.getInstanceMonitor().getCurrentInstance().setStatus(InstanceStatus.CLOSED);
            return;
        }

        // If there is another argument, continue in another thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Find the instance they are trying to close.
            final ServerInstance instance = plugin.getInstanceMonitor().getInstance(args[1]);

            // Make sure the instance exists.
            if(instance == null) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>Could not find an instance with that name!</red>");
                return;
            }

            // Closes the instance.
            plugin.getInstanceMonitor().closeInstance(instance);
            ChatUtils.chat(sender, "<primary><bold>Instance</bold> <dark_gray>» <primary>Instance has been marked as " + InstanceStatus.CLOSED.getDisplayName() + "<primary>.");
        });
    }

    /**
     * Marks a given instance as online.
     * @param sender Command Sender.
     * @param args command arguments.
     */
    private void openCMD(@NotNull final CommandSender sender, final String[] args) {
        // If too many arguments are given, explain how to use the command.
        if(args.length > 2) {
            ChatUtils.chat(sender, "<red><bold>Usage</bold></red> <dark_gray>»</dark_gray> <red>/instance open [server]</red>");
            return;
        }

        // If the only argument is the "open", open the current instance.
        if(args.length == 1) {
            // Make sure the instance is closed.
            if(plugin.getInstanceMonitor().getCurrentInstance().getStatus() != InstanceStatus.CLOSED) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>This instance is already open!</red>");
                return;
            }

            // Opens the instance.
            ChatUtils.chat(sender, "<primary><bold>Instance</bold> <dark_gray>» <primary>Instance has been marked as " + InstanceStatus.ONLINE.getDisplayName() + "<primary>.");
            plugin.getInstanceMonitor().getCurrentInstance().setStatus(InstanceStatus.ONLINE);
            return;
        }

        // If there is another argument, continue in another thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Find the instance they are trying to open.
            final ServerInstance instance = plugin.getInstanceMonitor().getInstance(args[1]);

            // Make sure the instance exists.
            if(instance == null) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>Could not find an instance with that name!</red>");
                return;
            }

            // Make sure the instance is not already open.
            if(instance.getStatus() != InstanceStatus.CLOSED) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>That instance is already open!</red>");
                return;
            }

            // Opens the instance.
            plugin.getInstanceMonitor().openInstance(instance);
            ChatUtils.chat(sender, "<primary><bold>Instance</bold> <dark_gray>» <primary>Instance has been marked as " + InstanceStatus.ONLINE.getDisplayName() + "<primary>.");
        });
    }

    /**
     * Display the online players of a given instance.
     * @param sender Command sender.
     * @param args Command arguments.
     */
    private void playersCMD(@NotNull final CommandSender sender, final String[] args) {
        // Make sure the sender is a player.
        if(!(sender instanceof Player)) {
            ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>Only players can use that command!</red>");
            return;
        }

        // If too many arguments are given, explain how to use the command.
        if(args.length > 2) {
            ChatUtils.chat(sender, "<red><bold>Usage</bold></red> <dark_gray>»</dark_gray> <red>/instance players [server]</red>");
            return;
        }

        // Get the instance to look at players for based on the command arguments.
        String instanceName;
        if(args.length < 2) {
            instanceName = plugin.getInstanceMonitor().getCurrentInstance().getName();
        }
        else {
            instanceName = args[1];
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Find the instance they are trying to open.
            final ServerInstance instance = plugin.getInstanceMonitor().getInstance(instanceName);

            // Make sure the instance exists.
            if(instance == null) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>Could not find an instance with that name!</red>");
                return;
            }

            // Display the GUI to the player.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                new InstancePlayersGUI(plugin, instance).open(((Player) sender));
            });
        });
    }

    /**
     * View the statistics of the current instance.
     * @param sender Command Sender.
     */
    private void viewCurrentInstanceCMD(@NotNull final CommandSender sender) {
        final CurrentInstance instance = plugin.getInstanceMonitor().getCurrentInstance();

        // Display the statistics.
        ChatUtils.chat(sender, "");
        ChatUtils.chat(sender, "<center><secondary><st>             </st> <primary><bold>Current Instance</bold> <secondary><st>             </st>");
        ChatUtils.chat(sender, "  <primary>Name: <white>" + instance.getName());
        ChatUtils.chat(sender, "  <primary>Players: <white>" + instance.getOnline() + "<secondary>/<white>" + instance.getCapacity());
        ChatUtils.chat(sender, "  <primary>Version: <white>1." + instance.getMajorVersion() + "." + instance.getMinorVersion());
        ChatUtils.chat(sender, "  <primary>Address: <white>" + instance.getAddress() + ":" + instance.getPort());
        ChatUtils.chat(sender, "  <primary>Status: " + instance.getStatus().getDisplayName());
        ChatUtils.chat(sender, "  <primary>Uptime: <white>" + DurationFormatUtils.formatDurationWords(instance.getUptime(), true, true));
        ChatUtils.chat(sender, "");
    }

    /**
     * View the statistics of a given server instance.
     * @param sender Command Sender.
     * @param args Command Arugments.
     */
    private void viewRemoteInstance(@NotNull final CommandSender sender, final String[] args) {
        // Make sure the right amount of arguments are used.
        if(args.length != 2) {
            ChatUtils.chat(sender, "<red><bold>Usage</bold></red> <dark_gray>»</dark_gray> <red>/instance view <server></red>");
            return;
        }

        // If the instance is the current instance, use other command instead.
        if(args[1].equalsIgnoreCase(plugin.getInstanceMonitor().getCurrentInstance().getName())) {
            viewCurrentInstanceCMD(sender);
            return;
        }

        // Search for the instance in another thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final ServerInstance instance = plugin.getInstanceMonitor().getInstance(args[1]);

            // Makes sure the instance exists.
            if(instance == null) {
                ChatUtils.chat(sender, "<red><bold>Error</bold></red> <dark_gray>»</dark_gray> <red>Could not find an instance with that name!</red>");
                return;
            }

            // Display the statistics.
            ChatUtils.chat(sender, "");
            ChatUtils.chat(sender, "<center><secondary><st>             </st> <primary><bold>Remote Instance</bold> <secondary><st>             </st>");
            ChatUtils.chat(sender, "  <primary>Name: <white>" + instance.getName());
            ChatUtils.chat(sender, "  <primary>Players: <white>" + instance.getOnline() + "<secondary>/<white>" + instance.getCapacity());
            ChatUtils.chat(sender, "  <primary>Version: <white>" + instance.getVersion());
            ChatUtils.chat(sender, "  <primary>Address: <white>" + instance.getAddress() + ":" + instance.getPort());
            ChatUtils.chat(sender, "  <primary>Status: " + instance.getStatus().getDisplayName());
            ChatUtils.chat(sender, "  <primary>Uptime: <white>" + DurationFormatUtils.formatDurationWords(instance.getUptime(), true, true));
            ChatUtils.chat(sender, "");
        });
    }
}