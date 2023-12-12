package com.plr.longnbtkiller.mixin;

import com.plr.longnbtkiller.LongNbtKiller;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PacketBuffer.class)
public abstract class MixinFriendlyByteBuf {

    @Shadow
    @Nullable
    public abstract CompoundNBT readNbt(NBTSizeTracker tracker);

    @Inject(method = "readNbt()Lnet/minecraft/nbt/CompoundNBT;", at = @At("HEAD"), cancellable = true)
    private void inject$readNbt(CallbackInfoReturnable<CompoundNBT> cir) {
        if (LongNbtKiller.shouldRemoveByteBufTagLimit()) cir.setReturnValue(this.readNbt(NBTSizeTracker.UNLIMITED));
    }
}
