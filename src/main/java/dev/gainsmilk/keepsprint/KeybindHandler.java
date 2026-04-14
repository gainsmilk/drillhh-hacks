package dev.gainsmilk.keepsprint;

import dev.gainsmilk.keepsprint.gui.TrenboloneGui;
import net.minecraft.client.Minecraft;
import net.weavemc.api.KeyboardEvent;
import net.weavemc.api.event.SubscribeEvent;

/**
 * Listens for keyboard events via Weave's event bus. Opens the GUI when
 * RIGHT ARROW (LWJGL keycode 205) is pressed, as long as there is no other
 * screen currently open.
 */
public class KeybindHandler {
    private static final int KEY_RIGHT_ARROW = 205;

    @SubscribeEvent
    public void onKey(KeyboardEvent event) {
        if (!event.getKeyState()) return;
        if (event.getKeyCode() != KEY_RIGHT_ARROW) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.currentScreen != null) return;
        mc.displayGuiScreen(new TrenboloneGui());
    }
}
