package dev.alxx.keepsprint.mixins;

import dev.alxx.keepsprint.KeepSprintMod;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BlockReach: extends client-side block interaction distance.
 * Vanilla: 4.5 (survival) / 5.0 (creative).
 * This override returns CONFIG.blockReachDistance instead.
 *
 * Note: server anticheats enforce their own reach cap (~6.0 typical,
 * hypixel watchdog flags at 7+). Client-only change.
 */
@Mixin(PlayerControllerMP.class)
public abstract class BlockReachMixin {
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    private void customReach(CallbackInfoReturnable<Float> cir) {
        if (KeepSprintMod.CONFIG == null) return;
        if (!KeepSprintMod.CONFIG.blockReachEnabled) return;
        cir.setReturnValue((float) KeepSprintMod.CONFIG.blockReachDistance);
    }
}
