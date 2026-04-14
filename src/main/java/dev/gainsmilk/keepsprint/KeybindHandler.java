package dev.gainsmilk.keepsprint;

import dev.gainsmilk.keepsprint.config.Config;
import dev.gainsmilk.keepsprint.gui.TrenboloneGui;
import net.minecraft.client.Minecraft;
import net.weavemc.api.KeyboardEvent;
import net.weavemc.api.event.SubscribeEvent;

/**
 * Listens for keyboard events via Weave's event bus. Opens the GUI on
 * RIGHT_ARROW (LWJGL 205) and toggles each module when its configured
 * keybind is pressed. Keybinds live in {@link Config} - 0 means unbound.
 */
public class KeybindHandler {
    private static final int KEY_RIGHT_ARROW = 205;

    @SubscribeEvent
    public void onKey(KeyboardEvent event) {
        if (!event.getKeyState()) return;
        int key = event.getKeyCode();

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) return;

        // GUI toggle reserved for when no other screen is open.
        if (key == KEY_RIGHT_ARROW && mc.currentScreen == null) {
            mc.displayGuiScreen(new TrenboloneGui());
            return;
        }

        // Module toggles only fire when in-world (no menu open).
        if (mc.currentScreen != null) return;
        Config cfg = KeepSprintMod.CONFIG;
        if (cfg == null) return;

        boolean changed = false;
        if (cfg.keepSprintKey != 0 && key == cfg.keepSprintKey) {
            cfg.keepSprintEnabled = !cfg.keepSprintEnabled;
            changed = true;
        } else if (cfg.omniSprintKey != 0 && key == cfg.omniSprintKey) {
            cfg.omniSprintEnabled = !cfg.omniSprintEnabled;
            changed = true;
        } else if (cfg.fastPlaceKey != 0 && key == cfg.fastPlaceKey) {
            cfg.fastPlaceEnabled = !cfg.fastPlaceEnabled;
            changed = true;
        } else if (cfg.blockReachKey != 0 && key == cfg.blockReachKey) {
            cfg.blockReachEnabled = !cfg.blockReachEnabled;
            changed = true;
        } else if (cfg.timerKey != 0 && key == cfg.timerKey) {
            cfg.timerEnabled = !cfg.timerEnabled;
            changed = true;
        }

        if (changed) cfg.save();
    }
}
