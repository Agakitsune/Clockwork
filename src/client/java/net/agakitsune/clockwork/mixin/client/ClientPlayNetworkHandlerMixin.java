package net.agakitsune.clockwork.mixin.client;

import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.agakitsune.clockwork.network.ExtendedClientPlayPacketListener;
import net.agakitsune.clockwork.network.packet.s2c.play.ManaBarUpdateS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ExtendedClientPlayPacketListener {

    @Override
    public void onManaBarUpdate(ManaBarUpdateS2CPacket packet) {
        ClientCommonNetworkHandlerMixin mixin = (ClientCommonNetworkHandlerMixin) this;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, mixin.getClient());
        ((ManaEntity)mixin.getClient().player).setMana(packet.getMana());
        ((ManaEntity)mixin.getClient().player).setMaxMana(packet.getMaxMana());
    }
}
