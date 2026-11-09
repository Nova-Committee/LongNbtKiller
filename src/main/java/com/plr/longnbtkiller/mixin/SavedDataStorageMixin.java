package com.plr.longnbtkiller.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.plr.longnbtkiller.NbtReadContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
//? if <26.1 {
/*import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Shadow;
*///? } else {
import net.minecraft.world.level.storage.SavedDataStorage;
//? }
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.DataInput;
import java.io.InputStream;
import java.nio.file.Path;

//? if <26.1 {
/*@Mixin(DimensionDataStorage.class)
*///? } else {
@Mixin(SavedDataStorage.class)
//? }
abstract class SavedDataStorageMixin {
    //? if <26.1 {
    /*@Shadow
    private Path getDataFile(String name) {
        throw new AssertionError();
    }
    *///? }

    @WrapOperation(method = "readTagFromDisk", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/io/InputStream;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag longnbtkiller$readCompressed(InputStream input, NbtAccounter accounter,
                                                      Operation<CompoundTag> original,
                                                      //? if <26.1 {
                                                      /*@Local(argsOnly = true) String name) {
        return NbtReadContext.file(getDataFile(name), true, () -> original.call(input, accounter));
        *///? } else {
                                                      @Local(argsOnly = true) Path path) {
        return NbtReadContext.file(path, true, () -> original.call(input, accounter));
        //? }
    }

    @WrapOperation(method = "readTagFromDisk", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtIo;read(Ljava/io/DataInput;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag longnbtkiller$read(DataInput input, Operation<CompoundTag> original,
                                            //? if <26.1 {
                                            /*@Local(argsOnly = true) String name) {
        return NbtReadContext.file(getDataFile(name), false, () -> original.call(input));
        *///? } else {
                                            @Local(argsOnly = true) Path path) {
        return NbtReadContext.file(path, false, () -> original.call(input));
        //? }
    }
}
