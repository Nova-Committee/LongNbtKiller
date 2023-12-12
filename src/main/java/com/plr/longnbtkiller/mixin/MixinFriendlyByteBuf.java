package com.plr.longnbtkiller.mixin;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FriendlyByteBuf.class)
public abstract class MixinFriendlyByteBuf {
    @Redirect(method = "readNbt()Lnet/minecraft/nbt/CompoundTag;", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtAccounter;create(J)Lnet/minecraft/nbt/NbtAccounter;"))
    private NbtAccounter inject$readNbt(long l) {
        return NbtAccounter.unlimitedHeap();
    }
}
