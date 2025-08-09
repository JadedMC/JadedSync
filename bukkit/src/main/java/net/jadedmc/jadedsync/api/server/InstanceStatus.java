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

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the current status of an Instance.
 */
public enum InstanceStatus {
    /**
     * Server is running and fully operational.
     */
    ONLINE("<green>", "The server is online and healthy.", Material.GREEN_TERRACOTTA),

    /**
     * Server is at capacity.
     */
    FULL("<red>", "The server has reached maximum capacity.", Material.RED_TERRACOTTA),

    /**
     * Server has not sent a heartbeat in 90 seconds.
     */
    UNRESPONSIVE("<dark_gray>", "The server has not responded<newline>to recent heartbeats.", Material.BLACK_TERRACOTTA),

    /**
     * Server is not open to new players. Will shut down when all players leave.
     */
    CLOSED("<gold>", "The server is closed and will reboot<newline>when empty.", Material.ORANGE_TERRACOTTA),

    /**
     * Server is blocked from the discovery system and only accessible by staff.
     */
    MAINTENANCE("<dark_purple>", "The server is locked for maintenance.", Material.PURPLE_TERRACOTTA);

    private final String color;
    private final String description;
    private final Material iconMaterial;

    /**
     * Creates the status.
     * @param color Color (in MiniMessage format) of the status.
     * @param description Description of the status.
     * @param iconMaterial Material used in Instance icons.
     */
    InstanceStatus(@NotNull final String color, @NotNull final String description, final Material iconMaterial) {
        this.color = color;
        this.description = description;
        this.iconMaterial = iconMaterial;
    }

    /**
     * Gets the color of the instance (as a String)
     * @return Instance color.
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Gets the description of the status.
     * @return Status description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the status display name. Used in chat messages.
     * @return Status display name.
     */
    public String getDisplayName() {
        return "<hover:show_text:\'" + this.color + this.description + "\'><bold>" + this.color + this + "</hover>";
    }

    /**
     * Gets the Icon Material, used in GUIs.
     * @return Status icon material.
     */
    public Material getIconMaterial() {
        return this.iconMaterial;
    }
}