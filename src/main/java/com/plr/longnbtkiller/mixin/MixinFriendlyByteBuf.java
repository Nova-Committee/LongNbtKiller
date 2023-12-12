package com.plr.longnbtkiller.mixin;

import com.plr.longnbtkiller.LongNbtKiller;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(FriendlyByteBuf.class)
public abstract class MixinFriendlyByteBuf {
    @Shadow
    @Nullable
    public abstract CompoundTag readNbt(NbtAccounter accounter);

    @Inject(method = "readNbt()Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"), cancellable = true)
    private void inject$readNbt(CallbackInfoReturnable<CompoundTag> cir) {
        if (LongNbtKiller.shouldRemoveByteBufTagLimit()) cir.setReturnValue(this.readNbt(NbtAccounter.UNLIMITED));
    }
}
