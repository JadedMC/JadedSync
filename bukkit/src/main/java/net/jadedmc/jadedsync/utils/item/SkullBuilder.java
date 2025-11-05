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
package net.jadedmc.jadedsync.utils.item;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SkullBuilder {
    private ItemStack item;
    private SkullMeta meta;

    public SkullBuilder() {}

    public SkullBuilder fromBase64(@NotNull final String base64) {
        this.item = XSkull.createItem().profile(Profileable.of(ProfileInputType.BASE64, base64)).apply();
        return this;
    }

    public SkullBuilder fromPlayer(@NotNull final OfflinePlayer offlinePlayer) {
        // Work around for bedrock players.
        if(offlinePlayer.getName() != null && offlinePlayer.getName().contains(".")) {
            this.item = new ItemStack(Material.PLAYER_HEAD);
            return this;
        }

        this.item = XSkull.createItem().profile(Profileable.of(offlinePlayer)).apply();
        return this;
    }

    public SkullBuilder fromSkull(final Skull skull) {
        return fromBase64(skull.getTexture());
    }

    public SkullBuilder fromUUID(@NotNull final UUID uuid) {
        this.item = XSkull.createItem().profile(Profileable.of(uuid)).apply();
        return this;
    }

    public ItemBuilder asItemBuilder() {
        return new ItemBuilder(item);
    }
}