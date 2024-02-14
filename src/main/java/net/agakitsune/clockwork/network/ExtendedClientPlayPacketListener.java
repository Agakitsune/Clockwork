package net.agakitsune.clockwork.network;

import net.agakitsune.clockwork.network.packet.s2c.play.ManaBarUpdateS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;

public interface ExtendedClientPlayPacketListener extends ClientPlayPacketListener {

    void onManaBarUpdate(ManaBarUpdateS2CPacket packet);
}
