package dev.alxx.keepsprint.mixins;

import dev.alxx.keepsprint.KeepSprintMod;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * KeepSprint: forces sprint=true each tick as long as the player is moving
 * (any direction) and has enough hunger. Preserves sprint through backwards
 * movement, sideways strafe, and turning - the conditions vanilla normally
 * drops sprint on.
 */
@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin {
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void keepSprint(CallbackInfo ci) {
        if (KeepSprintMod.CONFIG == null || !KeepSprintMod.CONFIG.keepSprintEnabled) return;
        EntityPlayerSP self = (EntityPlayerSP) (Object) this;
        if (self.movementInput == null) return;

        boolean moving = self.movementInput.moveForward != 0
                      || self.movementInput.moveStrafe != 0;
        boolean fed = self.getFoodStats().getFoodLevel() > 6;

        if (moving && fed) {
            self.setSprinting(true);
        }
    }
}
