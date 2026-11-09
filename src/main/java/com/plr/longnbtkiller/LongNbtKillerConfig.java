package com.plr.longnbtkiller;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LongNbtKillerConfig {
    static final int VANILLA_DEPTH = 512;
    private static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;
    private static final int DEFAULT_WARNING_INTERVAL = 1200;
    static final String FILE_NAME = "longnbtkiller-common.toml";
    static final ModConfig.Type TYPE =
            //? if neoforge && >=26.3 {
            /*ModConfig.Type.LOCAL;
            *///? } else {
            ModConfig.Type.COMMON;
            //? }
    static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue MAX_DEPTH;
    private static final ModConfigSpec.IntValue WARNING_INTERVAL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MAX_DEPTH = builder.comment("Maximum NBT nesting depth for supported save reads. Set to 512 for the vanilla limit.")
                .defineInRange("maxDepth", DEFAULT_MAX_DEPTH, VANILLA_DEPTH, Integer.MAX_VALUE);
        WARNING_INTERVAL = builder.comment("Ticks between in-game warnings after the first warning. Set to 0 to disable chat warnings.")
                .defineInRange("warningIntervalTicks", DEFAULT_WARNING_INTERVAL, 0, Integer.MAX_VALUE);
        SPEC = builder.build();
    }

    private LongNbtKillerConfig() {
    }

    static int maxDepth() {
        return SPEC.isLoaded() ? MAX_DEPTH.getAsInt() : DEFAULT_MAX_DEPTH;
    }

    public static int warningIntervalTicks() {
        return SPEC.isLoaded() ? WARNING_INTERVAL.getAsInt() : DEFAULT_WARNING_INTERVAL;
    }
}
