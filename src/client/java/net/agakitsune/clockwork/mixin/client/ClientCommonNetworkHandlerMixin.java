package net.agakitsune.clockwork.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Debug(export = true)
@Mixin(ClientCommonNetworkHandler.class)
public interface ClientCommonNetworkHandlerMixin {

    @Accessor
    MinecraftClient getClient();
}
