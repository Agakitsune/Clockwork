package net.agakitsune.clockwork;

import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.agakitsune.clockwork.entity.ClockworkEntityType;
import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.agakitsune.clockwork.network.packet.s2c.play.ManaBarUpdateS2CPacket;
import net.agakitsune.clockwork.render.entity.StalacticteEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;

public class ClockworkClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ClockworkBlocks.GUNPOWDER_WIRE, RenderLayer.getCutout());
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x666666, ClockworkBlocks.GUNPOWDER_WIRE);

		EntityRendererRegistry.register(ClockworkEntityType.STALACTICTE, StalacticteEntityRenderer::new);

		ClientPlayNetworking.registerGlobalReceiver(ManaBarUpdateS2CPacket.TYPE, (packet, player, responseSender) -> {
			((ManaEntity)player).setMana(packet.getMana());
			((ManaEntity)player).setMaxMana(packet.getMaxMana());
		});
	}
}