package com.plr.longnbtkiller.mixin;

import com.google.common.collect.Lists;
import com.plr.longnbtkiller.api.IListTag;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

@MethodsReturnNonnullByDefault
@Mixin(ListTag.class)
public abstract class MixinListTag implements IListTag {
    @Mutable
    @Shadow
    @Final
    public static TagType<ListTag> TYPE;

    @Mutable
    @Shadow
    @Final
    private List<Tag> list;

    @Shadow
    private byte type;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject$clinit(CallbackInfo ci) {
        TYPE = new TagType.VariableSize<>() {
            public ListTag load(DataInput input, int depth, NbtAccounter accounter) throws IOException {
                accounter.accountBytes(296L);
                byte b0 = input.readByte();
                int i = input.readInt();
                if (b0 == 0 && i > 0) {
                    throw new RuntimeException("Missing type on ListTag");
                } else {
                    accounter.accountBytes(32L * (long) i);
                    TagType<?> tagtype = TagTypes.getType(b0);
                    List<Tag> list = Lists.newArrayListWithCapacity(i);
                    for (int j = 0; j < i; ++j) {
                        list.add(tagtype.load(input, depth + 1, accounter));
                    }
                    final ListTag tag = new ListTag();
                    ((IListTag) tag).longnbtkiller$setListAndType(list, b0);
                    return tag;
                }
            }

            public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
                TagType<?> tagtype = TagTypes.getType(input.readByte());
                int i = input.readInt();
                switch (visitor.visitList(tagtype, i)) {
                    case HALT:
                        return StreamTagVisitor.ValueResult.HALT;
                    case BREAK:
                        tagtype.skip(input, i);
                        return visitor.visitContainerEnd();
                    default:
                        int j = 0;

                        while (true) {
                            label45:
                            {
                                if (j < i) {
                                    switch (visitor.visitElement(tagtype, j)) {
                                        case HALT:
                                            return StreamTagVisitor.ValueResult.HALT;
                                        case BREAK:
                                            tagtype.skip(input);
                                            break;
                                        case SKIP:
                                            tagtype.skip(input);
                                            break label45;
                                        default:
                                            switch (tagtype.parse(input, visitor)) {
                                                case HALT:
                                                    return StreamTagVisitor.ValueResult.HALT;
                                                case BREAK:
                                                    break;
                                                default:
                                                    break label45;
                                            }
                                    }
                                }

                                int k = i - 1 - j;
                                if (k > 0) {
                                    tagtype.skip(input, k);
                                }

                                return visitor.visitContainerEnd();
                            }

                            ++j;
                        }
                }
            }

            public void skip(DataInput input) throws IOException {
                TagType<?> tagtype = TagTypes.getType(input.readByte());
                int i = input.readInt();
                tagtype.skip(input, i);
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
    public void longnbtkiller$setListAndType(List<Tag> list, byte type) {
        this.list = list;
        this.type = type;
    }
}
