package com.ash1688.nuclearpowered.power;

/**
 * Bootstrap-tier balance maths (Coal Boiler + Steam Engine, ADR 0002). Pure functions with
 * no Minecraft types so balance can be unit-tested; block entities feed in tank/buffer
 * state and apply the returned step.
 */
public final class BootstrapRates {

    /** One Coal Boiler tick: water consumed, steam produced, whether the burn timer ticks down. */
    public record BoilerStep(int waterUsed, int steamMade, boolean burnAdvances) {
        static final BoilerStep IDLE = new BoilerStep(0, 0, false);
    }

    /** One Steam Engine tick: steam consumed and FE produced (always steamUsed × fePerMb). */
    public record EngineStep(int steamUsed, int feMade) {}

    /** True if the boiler has enough water and steam room to boil one full tick. Gates both burning and lighting new fuel. */
    public static boolean canBoil(int waterStored, int steamSpace, int waterPerTick, int steamPerTick) {
        return waterStored >= waterPerTick && steamSpace >= steamPerTick;
    }

    /**
     * The burn timer only advances on ticks that actually boil, so a boiler that runs dry or
     * backs up keeps its partly-burnt fuel. Never boils a partial tick.
     */
    public static BoilerStep boilerStep(int burnTicksLeft, int waterStored, int steamSpace, int waterPerTick, int steamPerTick) {
        if (burnTicksLeft <= 0 || !canBoil(waterStored, steamSpace, waterPerTick, steamPerTick)) return BoilerStep.IDLE;
        return new BoilerStep(waterPerTick, steamPerTick, true);
    }

    /**
     * FE is bounded by the per-engine cap, free buffer space and available steam. Steam is only
     * spent for FE that fits, rounding down to whole mB so nothing is lost.
     */
    public static EngineStep engineStep(int steamStored, int feSpace, int maxFePerTick, int fePerMb) {
        if (fePerMb <= 0) return new EngineStep(0, 0);
        int feLimit = Math.min(maxFePerTick, feSpace);
        int steamUsed = Math.max(0, Math.min(steamStored, feLimit / fePerMb));
        return new EngineStep(steamUsed, steamUsed * fePerMb);
    }

    private BootstrapRates() {}
}
