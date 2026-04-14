package dev.gainsmilk.keepsprint.gui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SliderMathTest {

    private static final double EPS = 1e-9;

    // --- clampOmni ---

    @ParameterizedTest
    @ValueSource(doubles = {-5.0, 0.0, 0.99, 1.00})
    void clampOmniClampsBelowRangeToMin(double v) {
        assertEquals(1.00, SliderMath.clampOmni(v), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.15, 1.16, 2.0, 100.0})
    void clampOmniClampsAboveRangeToMax(double v) {
        assertEquals(1.15, SliderMath.clampOmni(v), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "1.023, 1.02",
            "1.027, 1.03",
            "1.006, 1.01",
            "1.10,  1.10",
            "1.149, 1.15"
    })
    void clampOmniSnapsToStepAndRoundsToTwoDecimals(double in, double expected) {
        assertEquals(expected, SliderMath.clampOmni(in), EPS);
    }

    // --- clampReach ---

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, 0.0, 4.49, 4.5})
    void clampReachClampsBelowRangeToMin(double v) {
        assertEquals(4.5, SliderMath.clampReach(v), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {7.0, 7.01, 10.0, 99.9})
    void clampReachClampsAboveRangeToMax(double v) {
        assertEquals(7.0, SliderMath.clampReach(v), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "4.53, 4.5",
            "4.56, 4.6",
            "6.04, 6.0",
            "6.06, 6.1",
            "6.99, 7.0"
    })
    void clampReachSnapsToStepAndRoundsToOneDecimal(double in, double expected) {
        assertEquals(expected, SliderMath.clampReach(in), EPS);
    }

    // --- clampTimer ---

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, 0.0, 0.49, 0.5})
    void clampTimerClampsBelowRangeToMin(double v) {
        assertEquals(0.50, SliderMath.clampTimer(v), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.50, 1.51, 5.0, 99.0})
    void clampTimerClampsAboveRangeToMax(double v) {
        assertEquals(1.50, SliderMath.clampTimer(v), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "0.503, 0.50",
            "0.507, 0.51",
            "1.00,  1.00",
            "1.234, 1.23",
            "1.499, 1.50"
    })
    void clampTimerSnapsToStepAndRoundsToTwoDecimals(double in, double expected) {
        assertEquals(expected, SliderMath.clampTimer(in), EPS);
    }

    @Test
    void clampOmniNoFloatDrift() {
        // 0.01 in binary float has drift; result must be cleanly rounded.
        double r = SliderMath.clampOmni(1.07);
        assertEquals(1.07, r, 1e-12);
        assertEquals("1.07", Double.toString(r));
    }

    // --- fractionToValue ---

    @Test
    void fractionToValueReturnsMinWhenMouseBelowSliderStart() {
        assertEquals(1.00, SliderMath.fractionToValue(-50, 100, 140, 1.00, 1.15), EPS);
        assertEquals(1.00, SliderMath.fractionToValue(100, 100, 140, 1.00, 1.15), EPS);
    }

    @Test
    void fractionToValueReturnsMaxWhenMouseAtSliderEnd() {
        assertEquals(1.15, SliderMath.fractionToValue(240, 100, 140, 1.00, 1.15), EPS);
        assertEquals(1.15, SliderMath.fractionToValue(9999, 100, 140, 1.00, 1.15), EPS);
    }

    @Test
    void fractionToValueInterpolatesLinearly() {
        // midpoint
        assertEquals(1.075, SliderMath.fractionToValue(170, 100, 140, 1.00, 1.15), EPS);
        // quarter
        assertEquals(1.0375, SliderMath.fractionToValue(135, 100, 140, 1.00, 1.15), EPS);
    }
}
