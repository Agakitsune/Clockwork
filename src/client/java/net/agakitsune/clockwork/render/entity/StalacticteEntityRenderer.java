package net.agakitsune.clockwork.render.entity;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.entity.spellz.StalactiteEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class StalacticteEntityRenderer extends EntityRenderer<StalactiteEntity> {
    public StalacticteEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(StalactiteEntity entity) {
        return new Identifier(Clockwork.MODID, "textures/entity/projectiles/spellz/stalacticte.png");
    }
}
