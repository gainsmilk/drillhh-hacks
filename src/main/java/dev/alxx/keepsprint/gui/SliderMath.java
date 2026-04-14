package dev.alxx.keepsprint.gui;

/**
 * Pure slider math extracted from {@link TrenboloneGui} for unit testing.
 * All methods clamp to a module-specific range and snap to a step grid.
 */
public final class SliderMath {

    // --- Slider Bounds ---
    public static final double OMNI_MIN  = 1.00, OMNI_MAX  = 1.15, OMNI_STEP  = 0.01;
    public static final double REACH_MIN = 4.5,  REACH_MAX = 7.0,  REACH_STEP = 0.1;
    public static final double TIMER_MIN = 0.50, TIMER_MAX = 1.50, TIMER_STEP = 0.01;

    private SliderMath() {}

    /** Clamps to [OMNI_MIN, OMNI_MAX], snaps to OMNI_STEP, rounds to 2 decimals. */
    public static double clampOmni(double v) {
        if (v < OMNI_MIN) v = OMNI_MIN;
        if (v > OMNI_MAX) v = OMNI_MAX;
        double steps = Math.round((v - OMNI_MIN) / OMNI_STEP);
        return Math.round((OMNI_MIN + steps * OMNI_STEP) * 100.0) / 100.0;
    }

    /** Clamps to [REACH_MIN, REACH_MAX], snaps to REACH_STEP, rounds to 1 decimal. */
    public static double clampReach(double v) {
        if (v < REACH_MIN) v = REACH_MIN;
        if (v > REACH_MAX) v = REACH_MAX;
        double steps = Math.round((v - REACH_MIN) / REACH_STEP);
        return Math.round((REACH_MIN + steps * REACH_STEP) * 10.0) / 10.0;
    }

    /** Clamps to [TIMER_MIN, TIMER_MAX], snaps to TIMER_STEP, rounds to 2 decimals. */
    public static double clampTimer(double v) {
        if (v < TIMER_MIN) v = TIMER_MIN;
        if (v > TIMER_MAX) v = TIMER_MAX;
        double steps = Math.round((v - TIMER_MIN) / TIMER_STEP);
        return Math.round((TIMER_MIN + steps * TIMER_STEP) * 100.0) / 100.0;
    }

    /**
     * Linear interpolation of a mouse X position over a slider track into [min, max].
     * Clamps the fraction to [0, 1] before interpolation.
     */
    public static double fractionToValue(int mouseX, int sx, int sliderW, double min, double max) {
        double frac = (mouseX - sx) / (double) sliderW;
        if (frac < 0) frac = 0;
        if (frac > 1) frac = 1;
        return min + frac * (max - min);
    }
}
