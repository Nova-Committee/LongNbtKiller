package com.plr.longnbtkiller;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LongNbtKiller.MODID)
public class LongNbtKiller {
    public static final String MODID = "longnbtkiller";
    private static final ForgeConfigSpec CFG;
    private static final ForgeConfigSpec.IntValue _maxDepthForCompoundTag;
    private static final ForgeConfigSpec.IntValue _maxDepthForListTag;
    private static final ForgeConfigSpec.BooleanValue _removeByteBufTagLimit;
    private static int maxDepthForCompoundTag = Integer.MAX_VALUE;
    private static int maxDepthForListTag = Integer.MAX_VALUE;
    private static boolean removeByteBufTagLimit = true;
    public static final Logger LOGGER = LogManager.getLogger();

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("LongNbtKiller Config").push("depth");
        _maxDepthForCompoundTag = builder.defineInRange("maxDepthForCompoundTag", Integer.MAX_VALUE, 512, Integer.MAX_VALUE);
        _maxDepthForListTag = builder.defineInRange("maxDepthForListTag", Integer.MAX_VALUE, 512, Integer.MAX_VALUE);
        _removeByteBufTagLimit = builder.define("removeByteBufTagLimit", true);
        builder.pop();
        CFG = builder.build();
    }

    public LongNbtKiller() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "", (a, b) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG);
    }

    public static int getMaxDepthForCompoundTag() {
        return maxDepthForCompoundTag;
    }

    public static int getMaxDepthForListTag() {
        return maxDepthForListTag;
    }

    public static boolean shouldRemoveByteBufTagLimit() {
        return removeByteBufTagLimit;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventHandler {
        @SubscribeEvent
        public static void onConfigLoad(ModConfig.ModConfigEvent event) {
            maxDepthForCompoundTag = _maxDepthForCompoundTag.get();
            maxDepthForListTag = _maxDepthForListTag.get();
            removeByteBufTagLimit = _removeByteBufTagLimit.get();
        }
    }
}
