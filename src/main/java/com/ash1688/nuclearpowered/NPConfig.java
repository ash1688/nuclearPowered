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

        Common(ForgeConfigSpec.Builder b) {
            b.push("debug");
            this.enableDebugLogging = b
                    .comment("Verbose logging for reactor sim, recipe-unlock hooks, and quest events.")
                    .define("enableDebugLogging", false);
            b.pop();
        }
    }

    private NPConfig() {}
}
