package com.plr.longnbtkiller.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.plr.longnbtkiller.NbtReadContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;

import java.io.DataInput;
import java.nio.file.Path;

@Mixin(NbtIo.class)
abstract class NbtIoMixin {
    @WrapMethod(method = "readCompressed(Ljava/nio/file/Path;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;")
    private static CompoundTag longnbtkiller$readCompressed(Path path, NbtAccounter accounter,
                                                              Operation<CompoundTag> original) {
        return NbtReadContext.file(path, true, () -> original.call(path, accounter));
    }

    @WrapMethod(method = "read(Ljava/nio/file/Path;)Lnet/minecraft/nbt/CompoundTag;")
    private static CompoundTag longnbtkiller$read(Path path, Operation<CompoundTag> original) {
        return NbtReadContext.file(path, false, () -> original.call(path));
    }

    @WrapMethod(method = "parseCompressed(Ljava/nio/file/Path;Lnet/minecraft/nbt/StreamTagVisitor;Lnet/minecraft/nbt/NbtAccounter;)V")
    private static void longnbtkiller$parseCompressed(Path path, StreamTagVisitor visitor, NbtAccounter accounter,
                                                       Operation<Void> original) {
        NbtReadContext.inspectFile(path, () -> original.call(path, visitor, accounter));
    }

    @WrapMethod(method = "readUnnamedTag")
    private static Tag longnbtkiller$decode(DataInput input, NbtAccounter accounter, Operation<Tag> original) {
        return NbtReadContext.decode(accounter, () -> original.call(input, accounter));
    }

    @WrapMethod(method = "parse")
    private static void longnbtkiller$parse(DataInput input, StreamTagVisitor visitor, NbtAccounter accounter,
                                             Operation<Void> original) {
        NbtReadContext.decode(accounter, () -> original.call(input, visitor, accounter));
    }
}
