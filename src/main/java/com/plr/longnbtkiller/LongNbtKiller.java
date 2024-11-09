package com.plr.longnbtkiller;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(LongNbtKiller.MODID)
public class LongNbtKiller {
    public static final String MODID = "longnbtkiller";
    private static final ModConfigSpec CFG;
    private static final ModConfigSpec.BooleanValue _removeByteBufTagLimit;
    private static boolean removeByteBufTagLimit = true;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("LongNbtKiller Config").push("depth");
        _removeByteBufTagLimit = builder.define("removeByteBufTagLimit", true);
        builder.pop();
        CFG = builder.build();
    }

    public LongNbtKiller(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CFG);
    }

    public static boolean shouldRemoveByteBufTagLimit() {
        return removeByteBufTagLimit;
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventHandler {
        @SubscribeEvent
        public static void onConfigLoad(ModConfigEvent event) {
            removeByteBufTagLimit = _removeByteBufTagLimit.get();
        }
    }
}
