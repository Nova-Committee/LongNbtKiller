package com.plr.longnbtkiller;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod(LongNbtKiller.MODID)
public class LongNbtKiller {
    public static final String MODID = "longnbtkiller";
    private static final ForgeConfigSpec CFG;
    private static final ForgeConfigSpec.BooleanValue _removeByteBufTagLimit;
    private static boolean removeByteBufTagLimit = true;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("LongNbtKiller Config").push("depth");
        _removeByteBufTagLimit = builder.define("removeByteBufTagLimit", true);
        builder.pop();
        CFG = builder.build();
    }

    public LongNbtKiller() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG);
    }

    public static boolean shouldRemoveByteBufTagLimit() {
        return removeByteBufTagLimit;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventHandler {
        @SubscribeEvent
        public static void onConfigLoad(ModConfigEvent event) {
            removeByteBufTagLimit = _removeByteBufTagLimit.get();
        }
    }
}
