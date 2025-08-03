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

import net.jadedmc.jadedsync.utils.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes ItemStacks easier to create.
 */
@SuppressWarnings("unused")
public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * Creates an item builder using a base ItemStack.
     * @param itemStack Starting ItemStack.
     */
    public ItemBuilder(final ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = itemStack.getItemMeta().clone();
    }

    /**
     * Creates an item builder using a specific material and item count.
     * @param material Material to use.
     * @param amount Number of items.
     */
    public ItemBuilder(Material material, final int amount) {
        this(new ItemStack(material, amount));
    }

    /**
     * Creates an item builder using a base material.
     * @param material Material to use.
     */
    public ItemBuilder(final Material material) {
        this(material, 1);
    }

    /**
     * Adds an attribute to the item.
     * @param attribute Attribute to add.
     * @param attributeModifier Attribute modifier.
     * @return ItemBuilder.
     */
    public ItemBuilder addAttributeModifier(final Attribute attribute, final AttributeModifier attributeModifier) {
        this.itemMeta.addAttributeModifier(attribute, attributeModifier);
        return this;
    }


    /**
     * Adds an enchantment to the item.
     * @param enchantment Enchantment to add.
     * @param level Level of the enchantment.
     * @param ignore Whether the enchantment should ignore the enchantment cap.
     * @return ItemBuilder.
     */
    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level, final boolean ignore) {
        itemMeta.addEnchant(enchantment, level, ignore);
        return this;
    }

    /**
     * Adds an enchantment to the item.
     * @param enchantment Enchantment to add.
     * @param level Level of the enchantment.
     * @return ItemBuilder.
     */
    public ItemBuilder addEnchantment(final Enchantment enchantment, int level) {
        this.addEnchantment(enchantment, level, true);
        return this;
    }

    /**
     * Add an ItemFlag to the item.
     * @param itemFlag ItemFlag to add.
     * @return ItemBuilder.
     */
    public ItemBuilder addFlag(final ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    /**
     * Adds a String to the item lore.
     * Supports MiniMessage formatting.
     * @param str String to add to the lore.
     * @return ItemBuilder.
     */
    public ItemBuilder addLore(String str) {
        List<Component> lore = this.itemMeta.lore();

        if(lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(ChatUtils.translate("<!i>" + str));
        this.itemMeta.lore(lore);

        return this;
    }

    /**
     * Add several lines of lore, each starting with the same value.
     * @param lore Array of Strings to add as lore.
     * @param starter Started string to be added before each line.
     * @return ItemBuilder.
     */
    public ItemBuilder addLore(String[] lore, String starter) {

        for(String string : lore) {
            addLore(starter + string);
        }
        return this;
    }

    /**
     * Creates the ItemStack.
     * @return Built ItemStack.
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Set the number of items in the ItemStack.
     * @param amount New Number of items.
     * @return ItemBuilder.
     */
    public ItemBuilder setAmount(final int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    /**
     * Change the CustomModelData of the item.
     * @param customModelData New CustomModelData.
     * @return ItemBuilder.
     */
    public ItemBuilder setCustomModelData(final int customModelData) {
        this.itemMeta.setCustomModelData(customModelData);
        return this;
    }

    /**
     * Sets the item display name with a String.
     * Supports MiniMessage formatting.
     * @param displayName String to set display name to.
     * @return ItemBuilder.
     */
    public ItemBuilder setDisplayName(final String displayName) {
        this.itemMeta.displayName(ChatUtils.translate("<!i>" + displayName));
        return this;
    }

    /**
     * Replaces the ItemStack being used in the builder.
     * Probably doing something wrong if you are using this, but you can I guess.
     * @param itemStack New ItemStack to use.
     * @return ItemBuilder.
     */
    public ItemBuilder setItem(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        return this;
    }

    /**
     * Replaces the material being used in the builder.
     * @param material New material.
     * @return ItemBuilder.
     */
    public ItemBuilder setMaterial(final Material material) {
        this.itemStack.setType(material);
        return this;
    }

    /**
     * Adds persistent data to the item.
     * @param plugin Plugin being used.
     * @param key Key for the data.
     * @param value value for the data.
     * @return ItemBuilder.
     */
    public ItemBuilder setPersistentData(final Plugin plugin, final String key, final String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        this.itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);

        return this;
    }

    /**
     * Adds persistent data to the item.
     * @param plugin Plugin being used.
     * @param key Key for the data.
     * @param value value for the data.
     * @return ItemBuilder.
     */
    public ItemBuilder setPersistentData(final Plugin plugin, final String key, final int value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        this.itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);

        return this;
    }

    /**
     * Set if the item should be unbreakable.
     * Items are unbreakable by default, so should only really be set to true.
     * @param unbreakable Whether the item should be unbreakable.
     * @return ItemBuilder.
     */
    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }
}