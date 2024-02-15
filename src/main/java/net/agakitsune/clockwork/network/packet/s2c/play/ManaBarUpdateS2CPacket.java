package net.agakitsune.clockwork.network.packet.s2c.play;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.network.ExtendedClientPlayPacketListener;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ManaBarUpdateS2CPacket implements FabricPacket {
    private final int mana;
    private final int maxMana;

    public static final Identifier PACKET_ID = new Identifier(Clockwork.MODID, "mana");
    public static final PacketType<ManaBarUpdateS2CPacket> TYPE = PacketType.create(PACKET_ID, ManaBarUpdateS2CPacket::new);

    private ManaBarUpdateS2CPacket(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    private ManaBarUpdateS2CPacket(PacketByteBuf buf) {
        this.mana = buf.readVarInt();
        this.maxMana = buf.readVarInt();
    }

    public static ManaBarUpdateS2CPacket create(int mana, int maxMana) {
        return new ManaBarUpdateS2CPacket(mana, maxMana);
    }

    public void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this);
    }

    public int getMana() {
        return this.mana;
    }

    public int getMaxMana() {
        return this.maxMana;
    }

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(mana);
        buf.writeVarInt(maxMana);
    }

    /**
     * Returns the packet type of this packet.
     *
     * <p>Implementations should store the packet type instance in a {@code static final}
     * field and return that here, instead of creating a new instance.
     *
     * @return the type of this packet
     */
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
