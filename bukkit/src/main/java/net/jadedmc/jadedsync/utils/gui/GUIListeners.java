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
package net.jadedmc.jadedsync.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class GUIListeners implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        UUID pU = p.getUniqueId();
        UUID iU = CustomGUI.getOpenInventories().get(pU);

        if(iU == null) {
            return;
        }

        //e.setCancelled(true);
        CustomGUI gui = CustomGUI.getInventories().get(iU);

        gui.onClick(e);

        CustomGUI.ClickAction action = gui.getActions().get(e.getSlot());

        // Prevent number keys from working.
        if(e.getClick() == ClickType.NUMBER_KEY) {
            e.setCancelled(true);
            return;
        }

        if(action != null && e.getAction() != InventoryAction.SWAP_WITH_CURSOR && e.getAction() != InventoryAction.HOTBAR_SWAP && e.getAction() != InventoryAction.HOTBAR_MOVE_AND_READD) {
            action.click(p, e.getClick());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        UUID pU = p.getUniqueId();

        if(CustomGUI.getOpenInventories().containsKey(pU)) {
            UUID iU = CustomGUI.getOpenInventories().get(pU);
            CustomGUI gui = CustomGUI.getInventories().get(iU);
            gui.onClose(p);
            gui.delete();

            CustomGUI.getOpenInventories().remove(pU);

            p.setItemOnCursor(null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID u = CustomGUI.getOpenInventories().get(e.getPlayer().getUniqueId());
        CustomGUI gui = CustomGUI.getInventories().get(u);

        if(gui != null) {
            gui.delete();
        }

        CustomGUI.getInventories().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getWhoClicked();
        UUID pU = p.getUniqueId();
        UUID iU = CustomGUI.getOpenInventories().get(pU);

        if(iU == null) {
            return;
        }

        CustomGUI gui = CustomGUI.getInventories().get(iU);

        gui.onDrag(event);
    }
}