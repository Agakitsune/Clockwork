package net.agakitsune.clockwork;

import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.agakitsune.clockwork.command.ClockworkCommands;
import net.agakitsune.clockwork.entity.ClockworkEntityType;
import net.agakitsune.clockwork.item.ClockworkItems;
import net.agakitsune.clockwork.sounds.ClockworkSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clockwork implements ModInitializer {
	public static final String MODID = "clockwork";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static void breaker() {}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello " + MODID);

		ClockworkItems.registerItems();
		ClockworkBlocks.registerBlocks();
		ClockworkSounds.registerSounds();

		CommandRegistrationCallback.EVENT.register(ClockworkCommands::registerServer);

		ClockworkEntityType.registerEntityTypes();
	}
}