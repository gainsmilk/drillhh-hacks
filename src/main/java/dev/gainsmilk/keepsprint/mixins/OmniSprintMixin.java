package dev.gainsmilk.keepsprint.mixins;

import dev.gainsmilk.keepsprint.KeepSprintMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * OmniSprint: velocity boost on pure strafe (A/D without W) or pure backwards
 * (S alone) to compensate for vanilla's directional sprint loss. Leaves forward
 * and diagonal (W combinations) alone since vanilla sprint already handles them.
 *
 * Gates: on ground, not in water/lava/ladder, not falling, not sneaking, fed.
 *
 * Defaults to 1.12x when enabled. Range 1.00-1.15.
 */
@Mixin(EntityPlayerSP.class)
public abstract class OmniSprintMixin {
    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void applyOmniSprint(CallbackInfo ci) {
        if (KeepSprintMod.CONFIG == null) return;
        if (!KeepSprintMod.CONFIG.omniSprintEnabled) return;

        double mult = KeepSprintMod.CONFIG.omniSprintMultiplier;
        if (mult < 1.0) mult = 1.0;
        if (mult > 1.15) mult = 1.15;
        if (mult == 1.0) return;

        EntityPlayerSP self = (EntityPlayerSP) (Object) this;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) return;

        // Gate: on ground, not water/lava/ladder, not falling, not sneaking, fed
        if (!self.onGround) return;
        if (self.isInWater()) return;
        if (self.isInLava()) return;
        if (self.isOnLadder()) return;
        if (self.fallDistance >= 0.1f) return;
        if (self.isSneaking()) return;
        if (self.getFoodStats().getFoodLevel() <= 6) return;

        GameSettings gs = mc.gameSettings;
        boolean w = gs.keyBindForward.isKeyDown();
        boolean a = gs.keyBindLeft.isKeyDown();
        boolean s = gs.keyBindBack.isKeyDown();
        boolean d = gs.keyBindRight.isKeyDown();

        if (!(w || a || s || d)) return;

        self.setSprinting(true);

        // Pure strafe without forward
        if ((a || d) && !w) {
            self.motionX *= mult;
            self.motionZ *= mult;
            return;
        }
        // Pure backwards
        if (s && !w && !a && !d) {
            self.motionX *= mult;
            self.motionZ *= mult;
        }
    }
}
