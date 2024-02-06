package net.agakitsune.clockwork;

import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

public class ClockworkClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ClockworkBlocks.GUNPOWDER_WIRE, RenderLayer.getCutout());
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x666666, ClockworkBlocks.GUNPOWDER_WIRE);
	}
}