package com.ash1688.nuclearpowered;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod config skeleton. Cloth Config is available for an in-game GUI but for MVP the
 * underlying values live in standard Forge config (TOML) — Cloth wraps it later in M10.
 *
 * Values are deliberately empty for M1. Balance-relevant settings are added per milestone
 * (heat thresholds in M5, water-loss factor in M6, coin costs in M8, etc.).
 */
public final class NPConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue enableDebugLogging;
        public final ForgeConfigSpec.IntValue steamEngineMaxFePerTick;
        public final ForgeConfigSpec.IntValue steamEngineFePerMb;
        public final ForgeConfigSpec.IntValue boilerSteamPerTick;
        public final ForgeConfigSpec.IntValue boilerWaterPerTick;

        Common(ForgeConfigSpec.Builder b) {
            b.push("debug");
            this.enableDebugLogging = b
                    .comment("Verbose logging for reactor sim, recipe-unlock hooks, and quest events.")
                    .define("enableDebugLogging", false);
            b.pop();

            b.comment("Bootstrap tier (Coal Boiler + Steam Engine), see ADR 0002.").push("bootstrap");
            this.steamEngineMaxFePerTick = b
                    .comment("Max FE/t one Steam Engine produces. MUST stay below the Thermocouple FE/t so coal never outscales nuclear.")
                    .defineInRange("steamEngineMaxFePerTick", 40, 1, 100_000);
            this.steamEngineFePerMb = b
                    .comment("FE produced per mB of steam consumed.")
                    .defineInRange("steamEngineFePerMb", 2, 1, 1_000);
            this.boilerSteamPerTick = b
                    .comment("Steam (mB) one Coal Boiler makes per burning tick.")
                    .defineInRange("boilerSteamPerTick", 10, 1, 10_000);
            this.boilerWaterPerTick = b
                    .comment("Water (mB) one Coal Boiler uses per burning tick.")
                    .defineInRange("boilerWaterPerTick", 10, 1, 10_000);
            b.pop();
        }
    }

    private NPConfig() {}
}
