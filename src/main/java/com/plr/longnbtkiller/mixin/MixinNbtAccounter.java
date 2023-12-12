package com.plr.longnbtkiller.mixin;

import net.minecraft.nbt.NbtAccounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtAccounter.class)
public abstract class MixinNbtAccounter {
    @Mutable
    @Shadow
    @Final
    private int maxDepth;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void inject$init(long l, int i, CallbackInfo ci) {
        this.maxDepth = Integer.MAX_VALUE;
    }
}
