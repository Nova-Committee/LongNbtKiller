package com.plr.longnbtkiller;

import net.minecraft.nbt.Tag;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class NbtPathScanner {
    private static final int BYTE_BUDGET = 16 * 1024 * 1024;
    private static final int TAG_BUDGET = 262_144;

    private NbtPathScanner() {
    }

    static Optional<String> firstBeyond(InputStream source, int limit) throws IOException {
        DataInputStream input = new DataInputStream(new BufferedInputStream(new BudgetInputStream(source)));
        if (input.readUnsignedByte() != Tag.TAG_COMPOUND) {
            throw new IOException("Expected a compound root");
        }
        skipString(input);

        List<Container> stack = new ArrayList<>();
        stack.add(Container.compound(""));
        int remainingTags = TAG_BUDGET;
        while (!stack.isEmpty()) {
            if (--remainingTags < 0) {
                throw new IOException("NBT diagnostic tag budget exceeded");
            }
            Container parent = stack.getLast();
            int type = parent.nextType(input);
            if (type == Tag.TAG_END) {
                stack.removeLast();
                continue;
            }
            boolean container = type == Tag.TAG_COMPOUND || type == Tag.TAG_LIST;
            String segment = parent.nextSegment(input, container);
            switch (type) {
                case Tag.TAG_COMPOUND, Tag.TAG_LIST -> {
                    if (stack.size() >= limit) {
                        return Optional.of(path(stack, segment));
                    }
                    stack.add(type == Tag.TAG_COMPOUND
                            ? Container.compound(segment) : Container.list(input, segment));
                }
                case Tag.TAG_BYTE -> input.skipNBytes(1);
                case Tag.TAG_SHORT -> input.skipNBytes(2);
                case Tag.TAG_INT, Tag.TAG_FLOAT -> input.skipNBytes(4);
                case Tag.TAG_LONG, Tag.TAG_DOUBLE -> input.skipNBytes(8);
                case Tag.TAG_STRING -> skipString(input);
                case Tag.TAG_BYTE_ARRAY -> skipArray(input, 1);
                case Tag.TAG_INT_ARRAY -> skipArray(input, 4);
                case Tag.TAG_LONG_ARRAY -> skipArray(input, 8);
                default -> throw new IOException("Unknown NBT tag type " + type);
            }
        }
        return Optional.empty();
    }

    private static void skipString(DataInputStream input) throws IOException {
        input.skipNBytes(input.readUnsignedShort());
    }

    private static void skipArray(DataInputStream input, int width) throws IOException {
        int count = input.readInt();
        if (count < 0) {
            throw new IOException("Negative NBT array length");
        }
        input.skipNBytes((long) count * width);
    }

    private static String path(List<Container> stack, String lastSegment) {
        StringBuilder path = new StringBuilder("$");
        int headEnd = Math.min(stack.size(), 25);
        for (int i = 1; i < headEnd; i++) {
            path.append(stack.get(i).segment);
        }
        int tailStart = Math.max(headEnd, stack.size() - 7);
        if (tailStart > headEnd) {
            path.append("…(").append(tailStart - headEnd).append(" segments)…");
        }
        for (int i = tailStart; i < stack.size(); i++) {
            path.append(stack.get(i).segment);
        }
        return path.append(lastSegment).toString();
    }

    static String quote(String value, int maxLength) {
        StringBuilder quoted = new StringBuilder("\"");
        int length = Math.min(value.length(), maxLength);
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '\\' || c == '"') {
                quoted.append('\\').append(c);
            } else if (Character.isISOControl(c) || c == '\u2028' || c == '\u2029') {
                quoted.append("\\u").append(java.util.HexFormat.of().toHexDigits(c));
            } else {
                quoted.append(c);
            }
        }
        if (length < value.length()) {
            quoted.append('…');
        }
        return quoted.append('"').toString();
    }

    private static final class Container {
        private final String segment;
        private final int listType;
        private final int listSize;
        private int index;

        private Container(String segment, int listType, int listSize) {
            this.segment = segment;
            this.listType = listType;
            this.listSize = listSize;
        }

        static Container compound(String segment) {
            return new Container(segment, -1, 0);
        }

        static Container list(DataInputStream input, String segment) throws IOException {
            int type = input.readUnsignedByte();
            int size = input.readInt();
            if (size < 0 || (type == Tag.TAG_END && size > 0)) {
                throw new IOException("Invalid NBT list");
            }
            return new Container(segment, type, size);
        }

        int nextType(DataInputStream input) throws IOException {
            if (listType == -1) {
                return input.readUnsignedByte();
            }
            return index < listSize ? listType : Tag.TAG_END;
        }

        String nextSegment(DataInputStream input, boolean keep) throws IOException {
            if (listType != -1) {
                int element = index++;
                return keep ? "[" + element + "]" : "";
            }
            if (keep) {
                return "[" + quote(input.readUTF(), 96) + "]";
            }
            skipString(input);
            return "";
        }
    }

    private static final class BudgetInputStream extends FilterInputStream {
        private int remaining = BYTE_BUDGET;

        private BudgetInputStream(InputStream input) {
            super(input);
        }

        @Override
        public int read() throws IOException {
            requireBudget(1);
            int value = in.read();
            if (value >= 0) {
                remaining--;
            }
            return value;
        }

        @Override
        public int read(byte[] bytes, int offset, int length) throws IOException {
            requireBudget(length);
            int count = in.read(bytes, offset, Math.min(length, remaining));
            if (count > 0) {
                remaining -= count;
            }
            return count;
        }

        @Override
        public long skip(long length) throws IOException {
            requireBudget(length);
            long count = in.skip(Math.min(length, remaining));
            remaining -= (int) count;
            return count;
        }

        private void requireBudget(long length) throws IOException {
            if (remaining == 0 && length > 0) {
                throw new IOException("NBT diagnostic byte budget exceeded");
            }
        }
    }
}
