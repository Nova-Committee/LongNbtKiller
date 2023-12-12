package com.plr.longnbtkiller.mixin;

import com.google.common.collect.Maps;
import com.plr.longnbtkiller.LongNbtKiller;
import com.plr.longnbtkiller.api.ICompoundTag;
import net.minecraft.MethodsReturnNonnullByDefault;
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
@MethodsReturnNonnullByDefault
@Mixin(CompoundTag.class)
public abstract class MixinCompoundTag implements ICompoundTag {
    @Mutable
    @Shadow
    @Final
    public static TagType<CompoundTag> TYPE;

    @Shadow
    static byte readNamedTagType(DataInput input, NbtAccounter accounter) {
        throw new RuntimeException();
    }

    @Shadow
    static String readNamedTagName(DataInput input, NbtAccounter accounter) {
        throw new RuntimeException();
    }

    @Shadow
    static Tag readNamedTagData(TagType<?> type, String s, DataInput input, int i, NbtAccounter accounter) {
        throw new RuntimeException();
    }

    @Mutable
    @Shadow
    @Final
    private Map<String, Tag> tags;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject$clinit(CallbackInfo ci) {
        TYPE = new TagType.VariableSize<>() {
            public CompoundTag load(DataInput input, int depth, NbtAccounter accounter) throws IOException {
                if (depth > LongNbtKiller.getMaxDepthForCompoundTag()) {
                    LongNbtKiller.LOGGER.warn("Tried to read NBT tag with too high complexity, depth: {}", depth);
                    return new CompoundTag();
                }
                accounter.accountBits(384L);
                Map<String, Tag> map = Maps.newHashMap();
                byte b0;
                while ((b0 = readNamedTagType(input, accounter)) != 0) {
                    String s = readNamedTagName(input, accounter);
                    accounter.accountBits(224 + 16L * s.length());
                    accounter.accountBits(32); //Forge: 4 extra bytes for the object allocation.
                    Tag tag = readNamedTagData(TagTypes.getType(b0), s, input, depth + 1, accounter);
                    if (map.put(s, tag) != null) {
                        accounter.accountBits(288L);
                    }
                }
                final CompoundTag tag = new CompoundTag();
                ((ICompoundTag) tag).longnbtkiller$setTags(map);
                return tag;
            }

            public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
                while (true) {
                    byte b0;
                    if ((b0 = input.readByte()) != 0) {
                        TagType<?> tagtype = TagTypes.getType(b0);
                        switch (visitor.visitEntry(tagtype)) {
                            case HALT:
                                return StreamTagVisitor.ValueResult.HALT;
                            case BREAK:
                                StringTag.skipString(input);
                                tagtype.skip(input);
                                break;
                            case SKIP:
                                StringTag.skipString(input);
                                tagtype.skip(input);
                                continue;
                            default:
                                String s = input.readUTF();
                                switch (visitor.visitEntry(tagtype, s)) {
                                    case HALT:
                                        return StreamTagVisitor.ValueResult.HALT;
                                    case BREAK:
                                        tagtype.skip(input);
                                        break;
                                    case SKIP:
                                        tagtype.skip(input);
                                        continue;
                                    default:
                                        switch (tagtype.parse(input, visitor)) {
                                            case HALT:
                                                return StreamTagVisitor.ValueResult.HALT;
                                            case BREAK:
                                            default:
                                                continue;
                                        }
                                }
                        }
                    }

                    if (b0 != 0) {
                        while ((b0 = input.readByte()) != 0) {
                            StringTag.skipString(input);
                            TagTypes.getType(b0).skip(input);
                        }
                    }

                    return visitor.visitContainerEnd();
                }
            }

            public void skip(DataInput input) throws IOException {
                byte b0;
                while ((b0 = input.readByte()) != 0) {
                    StringTag.skipString(input);
                    TagTypes.getType(b0).skip(input);
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
    public void longnbtkiller$setTags(Map<String, Tag> tags) {
        this.tags = tags;
    }
}
