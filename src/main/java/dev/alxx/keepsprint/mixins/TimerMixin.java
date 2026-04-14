package dev.alxx.keepsprint.mixins;

import dev.alxx.keepsprint.KeepSprintMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Timer: overrides Minecraft.timer.timerSpeed every tick.
 * Vanilla is 1.0 (20 ticks/sec = 50ms per tick).
 * 1.1 = 22 ticks/sec, 0.9 = 18 ticks/sec etc.
 *
 * Compounds with movement/placement/click rate. Server anticheats flag
 * timer > 1.02-ish via packet frequency analysis. Fine on cheat-allowed
 * servers.
 */
@Mixin(Minecraft.class)
public abstract class TimerMixin {
    @Shadow public Timer timer;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void applyTimer(CallbackInfo ci) {
        if (KeepSprintMod.CONFIG == null || timer == null) return;
        float target = KeepSprintMod.CONFIG.timerEnabled
                ? (float) KeepSprintMod.CONFIG.timerMultiplier
                : 1.0f;
        this.timer.timerSpeed = target;
    }
}
