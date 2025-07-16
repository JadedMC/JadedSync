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

import org.jetbrains.annotations.NotNull;

/**
 * Represents the current status of an Instance.
 */
public enum InstanceStatus {
    /**
     * Server is running and fully operational.
     */
    ONLINE("<green>", "The server is online and healthy."),

    /**
     * Server is at capacity.
     */
    FULL("<red>", "The server has reached maximum capacity."),

    /**
     * Server has not sent a heartbeat in 90 seconds.
     */
    UNRESPONSIVE("<dark_gray>", "The server has not responded<newline>to recent heartbeats."),

    /**
     * Server is not open to new players. Will shut down when all players leave.
     */
    CLOSED("<red>", "The server is closed and will reboot<newline>when empty."),

    /**
     * Server is blocked from the discovery system and only accessible by staff.
     */
    MAINTENANCE("<dark_purple>", "The server is locked for maintenance.");

    private final String color;
    private final String description;

    InstanceStatus(@NotNull final String color, @NotNull final String description) {
        this.color = color;
        this.description = description;
    }

    public String getColor() {
        return this.color;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayName() {
        return "<hover:show_text:\'" + this.color + this.description + "\'><bold>" + this.color + this + "</hover>";
    }
}