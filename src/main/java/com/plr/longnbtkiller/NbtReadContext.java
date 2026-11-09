package com.plr.longnbtkiller;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

import static com.plr.longnbtkiller.LongNbtKillerConfig.VANILLA_DEPTH;

public final class NbtReadContext {
    private static final Logger LOGGER = LoggerFactory.getLogger("LongNBTKiller");
    private static final ThreadLocal<NbtReadContext> CURRENT = new ThreadLocal<>();
    private static final AtomicBoolean PENDING_WARNING = new AtomicBoolean();

    private final Path file;
    private final boolean compressed;
    private final RegionStorageInfo regionInfo;
    private final ChunkPos chunk;
    private final boolean notifyServer;
    private RegionFile region;
    private NbtAccounter accounter;
    private int attemptedDepth;
    private int configuredLimit;
    private boolean rejected;

    private NbtReadContext(Path file, boolean compressed, RegionStorageInfo regionInfo, ChunkPos chunk,
                           boolean notifyServer) {
        this.file = file;
        this.compressed = compressed;
        this.regionInfo = regionInfo;
        this.chunk = chunk;
        this.notifyServer = notifyServer;
    }

    public static <T> T file(Path file, boolean compressed, Supplier<T> read) {
        return new NbtReadContext(file, compressed, null, null, true).run(read);
    }

    public static <T> T inspectFile(Path file, Supplier<T> read) {
        return new NbtReadContext(file, true, null, null, false).run(read);
    }

    public static <T> T region(RegionStorageInfo info, ChunkPos chunk, Supplier<T> read) {
        return new NbtReadContext(null, false, info, chunk, true).run(read);
    }

    public static boolean takePendingWarning() {
        return PENDING_WARNING.get() && PENDING_WARNING.getAndSet(false);
    }

    public static void setRegionFile(RegionFile region) {
        NbtReadContext context = CURRENT.get();
        if (context != null && context.chunk != null) {
            context.region = region;
        }
    }

    public static <T> T decode(NbtAccounter accounter, Supplier<T> read) {
        NbtReadContext context = CURRENT.get();
        if (context == null || context.accounter != null) {
            return read.get();
        }
        context.accounter = accounter;
        try {
            return read.get();
        } finally {
            context.accounter = null;
        }
    }

    public static boolean exceedDepth(NbtAccounter accounter, int depth, int limit) {
        NbtReadContext context = CURRENT.get();
        if (context == null || context.accounter != accounter || limit != VANILLA_DEPTH) {
            return false;
        }
        if (context.attemptedDepth == 0) {
            context.configuredLimit = LongNbtKillerConfig.maxDepth();
            if (context.notifyServer) {
                PENDING_WARNING.set(true);
            }
        }
        int nextDepth = depth == Integer.MAX_VALUE ? depth : depth + 1;
        context.attemptedDepth = Math.max(context.attemptedDepth, nextDepth);
        boolean allow = depth < context.configuredLimit;
        context.rejected |= !allow;
        return allow;
    }

    private <T> T run(Supplier<T> read) {
        NbtReadContext previous = CURRENT.get();
        CURRENT.set(this);
        try {
            return read.get();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
            if (attemptedDepth > 0) {
                report();
            }
        }
    }

    private void report() {
        String source = file != null
                ? NbtPathScanner.quote(file.toString(), 512)
                : NbtPathScanner.quote(region == null ? regionInfo.level() : region.getPath().toString(), 512)
                        + " dimension=" + NbtPathScanner.quote(regionInfo.dimension().identifier().toString(), 128)
                        + " chunk=" + chunk;
        String path = "unavailable";
        try (InputStream input = open()) {
            if (input != null) {
                path = NbtPathScanner.firstBeyond(input, VANILLA_DEPTH).orElse("not found during replay");
            }
        } catch (IOException | RuntimeException error) {
            LOGGER.debug("Could not replay NBT source {}", source, error);
        }
        LOGGER.warn("Excessive NBT nesting in {}: depthReached={}, vanillaLimit={}, configuredLimit={}, action={}, nbtPath={}",
                source, attemptedDepth, VANILLA_DEPTH, configuredLimit, rejected ? "rejected" : "continued", path);
    }

    private InputStream open() throws IOException {
        if (file == null) {
            return region == null ? null : region.getChunkDataInputStream(chunk);
        }
        InputStream input = Files.newInputStream(file);
        if (!compressed) {
            return input;
        }
        try {
            return new GZIPInputStream(input);
        } catch (IOException | RuntimeException error) {
            try {
                input.close();
            } catch (IOException closeError) {
                error.addSuppressed(closeError);
            }
            throw error;
        }
    }
}
