package dev.gainsmilk.keepsprint;

import dev.gainsmilk.keepsprint.config.Config;
import net.weavemc.api.ModInitializer;
import net.weavemc.api.event.EventBus;

/**
 * Mod entrypoint. Loads config from disk and registers the keybind handler
 * on Weave's event bus. Holds the live {@link Config} reference used by every
 * mixin - nil until {@link #init()} returns, so mixins must guard.
 */
public class KeepSprintMod implements ModInitializer {
    public static Config CONFIG;

    @Override
    public void init() {
        CONFIG = Config.load();
        EventBus.subscribe(new KeybindHandler());
        System.out.println("[TrenboloneBridgonate] initialized. keepSprintEnabled="
                + CONFIG.keepSprintEnabled
                + " omniSprintEnabled="
                + CONFIG.omniSprintEnabled
                + " omniSprintMultiplier="
                + CONFIG.omniSprintMultiplier
                + " fastPlaceEnabled="
                + CONFIG.fastPlaceEnabled);
    }
}
