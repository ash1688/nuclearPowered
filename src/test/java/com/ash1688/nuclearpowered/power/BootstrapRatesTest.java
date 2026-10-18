package com.ash1688.nuclearpowered.power;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootstrapRatesTest {
    private static final int WATER = 10, STEAM = 10;   // Boiler defaults (mB/t)
    private static final int CAP = 40, RATIO = 2;      // Engine defaults (FE/t, FE per mB)

    // ===== Coal Boiler =====

    @Test
    void boilerMakesSteamAtTheConfiguredRate() {
        var step = BootstrapRates.boilerStep(100, 8000, 8000, WATER, STEAM);
        assertEquals(WATER, step.waterUsed());
        assertEquals(STEAM, step.steamMade());
        assertTrue(step.burnAdvances());
    }

    @Test
    void boilerFreezesBurnWithoutWater() {
        var step = BootstrapRates.boilerStep(100, 0, 8000, WATER, STEAM);
        assertEquals(0, step.waterUsed());
        assertEquals(0, step.steamMade());
        assertFalse(step.burnAdvances());
    }

    @Test
    void boilerFreezesBurnWhenSteamTankIsFull() {
        var step = BootstrapRates.boilerStep(100, 8000, 0, WATER, STEAM);
        assertEquals(0, step.steamMade());
        assertFalse(step.burnAdvances());
    }

    @Test
    void boilerNeverMakesPartialSteam() {
        // Only 5 mB of room for 10 mB of steam: wait rather than overfill or waste water.
        var step = BootstrapRates.boilerStep(100, 8000, 5, WATER, STEAM);
        assertEquals(0, step.steamMade());
        assertEquals(0, step.waterUsed());
        assertFalse(step.burnAdvances());
    }

    @Test
    void boilerDoesNothingWithoutAnActiveBurn() {
        var step = BootstrapRates.boilerStep(0, 8000, 8000, WATER, STEAM);
        assertEquals(0, step.steamMade());
        assertFalse(step.burnAdvances());
    }

    @Test
    void boilerOnlyLightsNewFuelWhenItCouldBoil() {
        assertTrue(BootstrapRates.canBoil(8000, 8000, WATER, STEAM));
        assertFalse(BootstrapRates.canBoil(0, 8000, WATER, STEAM));
        assertFalse(BootstrapRates.canBoil(8000, 0, WATER, STEAM));
    }

    // ===== Steam Engine =====

    @Test
    void engineRunsAtCapWithPlentyOfSteam() {
        var step = BootstrapRates.engineStep(8000, 10_000, CAP, RATIO);
        assertEquals(CAP, step.feMade());
        assertEquals(CAP / RATIO, step.steamUsed());
    }

    @Test
    void engineNeverExceedsTheCap() {
        for (int steam = 0; steam <= 1000; steam += 7) {
            assertTrue(BootstrapRates.engineStep(steam, 10_000, CAP, RATIO).feMade() <= CAP);
        }
    }

    @Test
    void engineNeverExceedsFreeBufferSpace() {
        var step = BootstrapRates.engineStep(8000, 9, CAP, RATIO);
        assertTrue(step.feMade() <= 9);
    }

    @Test
    void steamUsedMatchesFeMadeExactly() {
        for (int space = 0; space <= 50; space++) {
            var step = BootstrapRates.engineStep(8000, space, CAP, RATIO);
            assertEquals(step.feMade(), step.steamUsed() * RATIO, "space=" + space);
        }
    }

    @Test
    void engineLimitedBySteamAvailable() {
        var step = BootstrapRates.engineStep(3, 10_000, CAP, RATIO);
        assertEquals(3, step.steamUsed());
        assertEquals(6, step.feMade());
    }

    @Test
    void engineIdlesWithoutSteam() {
        var step = BootstrapRates.engineStep(0, 10_000, CAP, RATIO);
        assertEquals(0, step.feMade());
        assertEquals(0, step.steamUsed());
    }

    @Test
    void engineUsesNoSteamWhenBufferIsFull() {
        var step = BootstrapRates.engineStep(8000, 0, CAP, RATIO);
        assertEquals(0, step.steamUsed());
    }

    @Test
    void oneEngineAtCapNeedsTwoBoilersAtDefaults() {
        int steamPerEngine = BootstrapRates.engineStep(8000, 10_000, CAP, RATIO).steamUsed();
        assertEquals(2, steamPerEngine / STEAM);
    }
}
