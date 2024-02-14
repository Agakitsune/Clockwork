package net.agakitsune.clockwork.mixin;

import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.agakitsune.clockwork.network.packet.s2c.play.ManaBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Shadow public ServerPlayNetworkHandler networkHandler;
    private int syncedMana = -1;
    private int syncedMaxMana = -1;

    @Inject(method= "playerTick", at= @At("RETURN"))
    public void playerTick(CallbackInfo info) {
        if (((ManaEntity)this).getMana() != this.syncedMana || ((ManaEntity)this).getMaxMana() != this.syncedMaxMana) {
            this.syncedMana = ((ManaEntity)this).getMana();
            this.syncedMaxMana =((ManaEntity)this).getMaxMana();
            this.networkHandler.sendPacket(new ManaBarUpdateS2CPacket(this.syncedMana, this.syncedMaxMana));
        }
    }
}
