package com.plr.longnbtkiller.mixin;

import com.google.common.collect.Lists;
import com.plr.longnbtkiller.LongNbtKiller;
import com.plr.longnbtkiller.api.IListTag;
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
import java.util.List;

@ParametersAreNonnullByDefault
@Mixin(ListNBT.class)
public abstract class MixinListTag implements IListTag {

    @Mutable
    @Shadow
    @Final
    public static INBTType<ListNBT> TYPE;

    @Mutable
    @Shadow
    @Final
    private List<INBT> list;

    @Shadow
    private byte type;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject$clinit(CallbackInfo ci) {
        TYPE = new INBTType<ListNBT>() {
            public ListNBT load(DataInput input, int depth, NBTSizeTracker tracker) throws IOException {
                tracker.accountBits(296L);
                if (depth > LongNbtKiller.getMaxDepthForListTag()) {
                    LongNbtKiller.LOGGER.warn("Tried to read NBT tag with too high complexity, depth: {}", depth);
                    return new ListNBT();
                } else {
                    byte b0 = input.readByte();
                    int i = input.readInt();
                    if (b0 == 0 && i > 0) {
                        throw new RuntimeException("Missing type on ListTag");
                    } else {
                        tracker.accountBits(32L * (long) i);
                        INBTType<?> inbttype = NBTTypes.getType(b0);
                        List<INBT> list = Lists.newArrayListWithCapacity(i);

                        for (int j = 0; j < i; ++j) {
                            list.add(inbttype.load(input, depth + 1, tracker));
                        }

                        final ListNBT tag = new ListNBT();
                        ((IListTag) tag).longnbtkiller$setListAndType(list, b0);
                        return tag;
                    }
                }
            }

            public String getName() {
                return "LIST";
            }

            public String getPrettyName() {
                return "TAG_List";
            }
        };
    }

    @Override
    public void longnbtkiller$setListAndType(List<INBT> list, byte type) {
        this.list = list;
        this.type = type;
    }
}
