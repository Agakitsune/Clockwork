package net.agakitsune.clockwork.sounds;

import net.agakitsune.clockwork.Clockwork;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ClockworkSounds {
    public static final SoundEvent BLOCK_GUNPOWDER_WIRE_IGNITE = register("block.gunpowder_wire.ignite");
    public static final SoundEvent BLOCK_GUNPOWDER_WIRE_FUSE = register("block.gunpowder_wire.fuse");

    public static SoundEvent register(String name) {
        Clockwork.LOGGER.info("Registering " + name);
        Identifier id = new Identifier(Clockwork.MODID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Clockwork.LOGGER.info("Registering sounds for " + Clockwork.MODID);
    }
}
