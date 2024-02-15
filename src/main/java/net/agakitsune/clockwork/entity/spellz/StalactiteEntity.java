package net.agakitsune.clockwork.entity.spellz;

import net.agakitsune.clockwork.entity.ClockworkEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class StalactiteEntity extends ProjectileEntity {

    public StalactiteEntity(EntityType<? extends StalactiteEntity> entityType, World world) {
        super(entityType, world);
    }

    public StalactiteEntity(World world) {
        super(ClockworkEntityType.STALACTICTE, world);
    }

    /**
     * Initializes data tracker.
     *
     * @apiNote Subclasses should override this and call {@link DataTracker#startTracking}
     * for any data that needs to be tracked.
     */
    @Override
    protected void initDataTracker() {

    }
}
