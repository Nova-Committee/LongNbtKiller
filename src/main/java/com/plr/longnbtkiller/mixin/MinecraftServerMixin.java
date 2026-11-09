package com.plr.longnbtkiller.mixin;

import com.plr.longnbtkiller.LongNbtKillerConfig;
import com.plr.longnbtkiller.NbtReadContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Shadow public abstract PlayerList getPlayerList();

    @Unique private boolean longnbtkiller$deepNbtDetected;
    @Unique private boolean longnbtkiller$warningSent;
    @Unique private int longnbtkiller$ticksSinceWarning;

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void longnbtkiller$warnAboutDeepNbt(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        longnbtkiller$deepNbtDetected |= NbtReadContext.takePendingWarning();
        if (!longnbtkiller$deepNbtDetected) {
            return;
        }

        int interval = LongNbtKillerConfig.warningIntervalTicks();
        if (interval == 0) {
            longnbtkiller$warningSent = false;
            longnbtkiller$ticksSinceWarning = 0;
            return;
        }
        if (longnbtkiller$warningSent && longnbtkiller$ticksSinceWarning < interval) {
            longnbtkiller$ticksSinceWarning++;
            if (longnbtkiller$ticksSinceWarning < interval) {
                return;
            }
        }

        PlayerList players = getPlayerList();
        if (players.getPlayerCount() > 0) {
            players.broadcastSystemMessage(Component.translatableWithFallback("longnbtkiller.warning.deep_nbt",
                    "[Long NBT Killer] This world contains NBT nested beyond 512 levels. Check the server log for its location."),
                    false);
            longnbtkiller$warningSent = true;
            longnbtkiller$ticksSinceWarning = 0;
        }
    }

    @Inject(method = "stopServer", at = @At("RETURN"))
    private void longnbtkiller$clearPendingWarning(CallbackInfo ci) {
        NbtReadContext.takePendingWarning();
    }
}
