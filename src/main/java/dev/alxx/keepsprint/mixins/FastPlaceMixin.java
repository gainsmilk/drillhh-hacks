package dev.alxx.keepsprint.mixins;

import dev.alxx.keepsprint.KeepSprintMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FastPlace: zeroes Minecraft's rightClickDelayTimer every tick.
 * Vanilla sets it to 4 after every placement - this mod keeps it at 0
 * so holding RMB places a block every tick.
 */
@Mixin(Minecraft.class)
public abstract class FastPlaceMixin {
    @Shadow public int rightClickDelayTimer;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void fastPlace(CallbackInfo ci) {
        if (KeepSprintMod.CONFIG == null || !KeepSprintMod.CONFIG.fastPlaceEnabled) return;
        this.rightClickDelayTimer = 0;
    }
}
