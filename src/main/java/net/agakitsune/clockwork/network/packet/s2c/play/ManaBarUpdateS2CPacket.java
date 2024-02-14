package net.agakitsune.clockwork.network.packet.s2c.play;

import net.agakitsune.clockwork.network.ExtendedClientPlayPacketListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ManaBarUpdateS2CPacket implements Packet<ExtendedClientPlayPacketListener> {
    private final int mana;
    private final int maxMana;

    public ManaBarUpdateS2CPacket(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public ManaBarUpdateS2CPacket(PacketByteBuf buf) {
        this.mana = buf.readVarInt();
        this.maxMana = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.mana);
        buf.writeVarInt(this.maxMana);
    }

    @Override
    public void apply(ExtendedClientPlayPacketListener listener) {
        listener.onManaBarUpdate(this);
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }
}
