package com.plr.longnbtkiller.mixin;

import com.google.common.collect.Maps;
import com.plr.longnbtkiller.LongNbtKiller;
import com.plr.longnbtkiller.api.ICompoundTag;
import net.minecraft.nbt.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.DataInput;
import java.io.IOException;
import java.util.Map;

@ParametersAreNonnullByDefault
@Mixin(CompoundNBT.class)
public abstract class MixinCompoundTag implements ICompoundTag {

    @Shadow
    private static INBT readNamedTagData(INBTType<?> p_229680_0_, String p_229680_1_, DataInput p_229680_2_, int p_229680_3_, NBTSizeTracker p_229680_4_) {
        throw new RuntimeException();
    }

    @Shadow
    private static String readNamedTagName(DataInput p_152448_0_, NBTSizeTracker p_152448_1_) throws IOException {
        throw new RuntimeException();
    }

    @Shadow
    private static byte readNamedTagType(DataInput p_152447_0_, NBTSizeTracker p_152447_1_) throws IOException {
        throw new RuntimeException();
    }

    @Mutable
    @Shadow
    @Final
    private Map<String, INBT> tags;

    @Mutable
    @Shadow
    @Final
    public static INBTType<CompoundNBT> TYPE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject$clinit(CallbackInfo ci) {
        TYPE = new INBTType<CompoundNBT>() {
            public CompoundNBT load(DataInput input, int depth, NBTSizeTracker tracker) throws IOException {
                tracker.accountBits(384L);
                if (depth > LongNbtKiller.getMaxDepthForCompoundTag()) {
                    LongNbtKiller.LOGGER.warn("Tried to read NBT tag with too high complexity, depth: {}", depth);
                    return new CompoundNBT();
                } else {
                    Map<String, INBT> map = Maps.newHashMap();

                    byte b0;
                    while ((b0 = readNamedTagType(input, tracker)) != 0) {
                        String s = readNamedTagName(input, tracker);
                        tracker.accountBits(224 + 16 * s.length());
                        tracker.accountBits(32); //Forge: 4 extra bytes for the object allocation.
                        INBT inbt = readNamedTagData(NBTTypes.getType(b0), s, input, depth + 1, tracker);
                        if (map.put(s, inbt) != null) {
                            tracker.accountBits(288L);
                        }
                    }

                    final CompoundNBT tag = new CompoundNBT();
                    ((ICompoundTag) tag).longnbtkiller$setTags(map);
                    return tag;
                }
            }

            public String getName() {
                return "COMPOUND";
            }

            public String getPrettyName() {
                return "TAG_Compound";
            }
        };
    }

    @Override
    public void longnbtkiller$setTags(Map<String, INBT> tags) {
        this.tags = tags;
    }
}
