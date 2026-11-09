package com.plr.longnbtkiller.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plr.longnbtkiller.NbtReadContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.io.DataInputStream;

@Mixin(RegionFileStorage.class)
abstract class RegionFileStorageMixin {
    @Shadow public abstract RegionStorageInfo info();

    @WrapMethod(method = "read")
    private CompoundTag longnbtkiller$read(ChunkPos chunk, Operation<CompoundTag> original) {
        return NbtReadContext.region(info(), chunk, () -> original.call(chunk));
    }

    @WrapMethod(method = "scanChunk")
    private void longnbtkiller$scan(ChunkPos chunk, StreamTagVisitor visitor, Operation<Void> original) {
        NbtReadContext.region(info(), chunk, () -> original.call(chunk, visitor));
    }

    @WrapOperation(method = {"read", "scanChunk"}, at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/chunk/storage/RegionFile;getChunkDataInputStream(Lnet/minecraft/world/level/ChunkPos;)Ljava/io/DataInputStream;"),
            require = 2)
    private DataInputStream longnbtkiller$captureRegion(RegionFile region, ChunkPos chunk,
                                                       Operation<DataInputStream> original) {
        NbtReadContext.setRegionFile(region);
        return original.call(region, chunk);
    }
}
