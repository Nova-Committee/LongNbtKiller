package com.plr.longnbtkiller.mixin;

import com.plr.longnbtkiller.NbtReadContext;
import net.minecraft.nbt.NbtAccounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtAccounter.class)
abstract class NbtAccounterMixin {
    @Shadow private int depth;
    @Shadow @Final private int maxDepth;

    @Inject(method = "pushDepth", at = @At(value = "NEW", target = "net/minecraft/nbt/NbtAccounterException"),
            cancellable = true)
    private void longnbtkiller$onDepthLimit(CallbackInfo ci) {
        if (NbtReadContext.exceedDepth((NbtAccounter) (Object) this, depth, maxDepth)) {
            depth++;
            ci.cancel();
        }
    }
}
