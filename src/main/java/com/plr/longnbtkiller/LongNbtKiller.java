package com.plr.longnbtkiller;

//? if neoforge {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(LongNbtKiller.MOD_ID)
public final class LongNbtKiller {
    public static final String MOD_ID = "longnbtkiller";

    public LongNbtKiller(ModContainer container) {
        container.registerConfig(LongNbtKillerConfig.TYPE, LongNbtKillerConfig.SPEC, LongNbtKillerConfig.FILE_NAME);
    }
}
*///? } else {
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;

public final class LongNbtKiller implements ModInitializer {
    public static final String MOD_ID = "longnbtkiller";

    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(MOD_ID, LongNbtKillerConfig.TYPE, LongNbtKillerConfig.SPEC,
                LongNbtKillerConfig.FILE_NAME);
    }
}
//? }
