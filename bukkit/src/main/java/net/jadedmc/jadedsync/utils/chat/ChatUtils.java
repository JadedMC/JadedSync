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
package net.jadedmc.jadedsync.utils.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private final static int CENTER_PX = 154;

    /**
     * Attempts to center a message in chat.
     * @param message Message to center.
     * @return Centered message.
     */
    public static String centerText(String message) {
        if(message.equals("")) {
            return message;
        }

        String translated = ChatColor.translateAlternateColorCodes('&', MiniMessage.miniMessage().stripTags(toLegacy(message.replace("<primary>", "").replace("<secondary>", ""))));

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : translated.toCharArray()) {
            if(c == '§') {
                previousCode = true;
            }
            else if(previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }
            else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }

    /**
     * Broadcast a MiniMessage message to all online players.
     * @param message Message to broadcast.
     */
    public static void broadcast(String message) {
        Bukkit.broadcast(translate(message));
    }

    /**
     * Broadcast a MiniMessage message to all online players in a given world.
     * @param world World to broadcast message in.
     * @param message Message to broadcast.
     */
    public static void broadcast(World world, String message) {
        world.sendMessage(translate(message));
    }

    /**
     * Send a MiniMessage message to a given CommandSender.
     * @param commandSender CommandSender to send message to.
     * @param message Message to send.
     */
    public static void chat(CommandSender commandSender, String message) {
        commandSender.sendMessage(translate(message));
    }

    /**
     * Send a MiniMessage message to a given player, from their UUID.
     * @param playerUUID Player to send message to.
     * @param message Message to send.
     */
    public static void chat(@NotNull final UUID playerUUID, @NotNull final String message) {
        final Player player = Bukkit.getPlayer(playerUUID);

        // Make sure the player is online.
        if(player == null) {
            return;
        }

        // Send the message.
        chat(player, message);
    }

    /**
     * Send a MiniMessage message to a group of players, based of their ids.
     * @param playerUUIDs UUIDs of players to send messages to.
     * @param message Message to send.
     */
    public static void chat(@NotNull final Collection<UUID> playerUUIDs, @NotNull final String message) {
        playerUUIDs.forEach(uuid -> chat(uuid, message));
    }

    /**
     * Translates a String to a colorful String using methods in the BungeeCord API.
     * @param message Message to translate.
     * @return Translated Message.
     */
    public static Component translate(String message) {

        // Define custom "tags". (I can't figure out how to do this properly lol).
        message = message.replace("<primary>", "<color:#92fe59>");
        message = message.replace("<secondary>", "<color:#fae39b>");

        // Checks for the "<center>" tag, which centers a message.
        if(message.startsWith("<center>")) {
            message = centerText(message.replaceFirst("<center>", ""));
        }

        return MiniMessage.miniMessage().deserialize(replaceLegacy(message));
    }

    /**
     * Replaces the legacy color codes used in a message with their MiniMessage counterparts.
     * @param message Message to replace color codes in.
     * @return Message with the color codes replaced.
     */
    public static String replaceLegacy(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start() + 1, matcher.end());
            message = message.replace("&" + color, "<reset><color:" + color + ">");
            matcher = pattern.matcher(message);
        }

        // Then replace legacy color codes.
        return message.replace("§", "&")
                .replace("&0", "<!b><!i><!u><!st><!obf><black>")
                .replace("&1", "<!b><!i><!u><!st><!obf><dark_blue>")
                .replace("&2", "<!b><!i><!u><!st><!obf><dark_green>")
                .replace("&3", "<!b><!i><!u><!st><!obf><dark_aqua>")
                .replace("&4", "<!b><!i><!u><!st><!obf><dark_red>")
                .replace("&5", "<!b><!i><!u><!st><!obf><dark_purple>")
                .replace("&6", "<!b><!i><!u><!st><!obf><gold>")
                .replace("&7", "<!b><!i><!u><!st><!obf><gray>")
                .replace("&8", "<!b><!i><!u><!st><!obf><dark_gray>")
                .replace("&9", "<!b><!i><!u><!st><!obf><blue>")
                .replace("&a", "<!b><!i><!u><!st><!obf><green>")
                .replace("&b", "<!b><!i><!u><!st><!obf><aqua>")
                .replace("&c", "<!b><!i><!u><!st><!obf><red>")
                .replace("&d", "<!b><!i><!u><!st><!obf><light_purple>")
                .replace("&e", "<!b><!i><!u><!st><!obf><yellow>")
                .replace("&f", "<!b><!i><!u><!st><!obf><white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<!b><!i><!u><!st><!obf><white>");
    }

    /**
     * Convert a MiniMessage string to its legacy form.
     * Used because some important plugins don't play nice with MiniMessage.
     * @param message MiniMessage String to turn into a legacy String.
     * @return Resulting legacy string.
     */
    public static String toLegacy(String message) {
        return message.replace("<black>", "§0")
                .replace("<dark_blue>", "&1")
                .replace("<dark_green>", "&2")
                .replace("<dark_aqua>", "&3")
                .replace("<dark_red>", "&4")
                .replace("<dark_purple>", "&5")
                .replace("<gold>", "&6")
                .replace("<gray>", "&7")
                .replace("<dark_gray>", "&8")
                .replace("<blue>", "&9")
                .replace("<green>", "&a")
                .replace("<aqua>", "&b")
                .replace("<red>", "&c")
                .replace("<light_purple>", "&d")
                .replace("<yellow>", "&e")
                .replace("<white>", "&f")
                .replace("<obfuscated>", "&k")
                .replace("<obf>", "&k")
                .replace("<bold>", "&l")
                .replace("<b>", "&l")
                .replace("<strikethrough>", "&m")
                .replace("<st>", "&m")
                .replace("<underline>", "&n")
                .replace("<u>", "&n")
                .replace("<i>", "&o")
                .replace("<italic>", "&o")
                .replace("<reset>", "&r")
                .replace("</black>", "")
                .replace("</dark_blue>", "")
                .replace("</dark_green>", "")
                .replace("</dark_aqua>", "")
                .replace("</dark_red>", "")
                .replace("</dark_purple>", "")
                .replace("</gold>", "")
                .replace("</gray>", "")
                .replace("</dark_gray>", "")
                .replace("</blue>", "")
                .replace("</green>", "")
                .replace("</aqua>", "")
                .replace("</red>", "")
                .replace("</light_purple>", "")
                .replace("</yellow>", "")
                .replace("</white>", "")
                .replace("</obfuscated>", "")
                .replace("</obf>", "")
                .replace("</bold>", "")
                .replace("</b>", "")
                .replace("</strikethrough>", "")
                .replace("</st>", "")
                .replace("</underline>", "")
                .replace("</u>", "")
                .replace("</i>", "")
                .replace("</italic>", "");
    }
}
