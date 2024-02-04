package net.agakitsune.clockwork;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clockwork implements ModInitializer {
	public static final String MODID = "clockwork";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello " + MODID);
	}
}