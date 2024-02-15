package net.agakitsune.clockwork.entity;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.entity.spellz.StalactiteEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClockworkEntityType {
    public static final EntityType<StalactiteEntity> STALACTICTE = register(
            "stalacticte",
            FabricEntityTypeBuilder.<StalactiteEntity>create(SpawnGroup.MISC, StalactiteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f,0.75f))
    );

    public static <T extends Entity> EntityType<T> register(String name, FabricEntityTypeBuilder<T> builder) {
        Clockwork.LOGGER.info("Registering " + name);
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(Clockwork.MODID, name), builder.build());
    }

    public static void registerEntityTypes() {
        Clockwork.LOGGER.info("Registering entity type for " + Clockwork.MODID);
    }
}
