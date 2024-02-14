package net.agakitsune.clockwork.mixin.client;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final private static Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE;

    @Shadow @Final private static Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE;

    @Redirect(method= "render", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"))
    public void renderExperienceBar(InGameHud hud, DrawContext context, int x) {
        this.client.getProfiler().push("expBar");

        assert this.client.player != null;
        int nextLevelExperience = this.client.player.getNextLevelExperience();
        if (nextLevelExperience > 0) {
            int offset = (int)(this.client.player.experienceProgress * 183.0f);
            int y = this.scaledHeight - 32 + 1;
            context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, x, y, 182, 5);
            if (offset > 0) {
                context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, x, y, offset, 5);
            }
        }

        this.client.getProfiler().pop();

        renderManaBar(context, x);
        renderLevel(context);
        renderMana(context);
        renderSeparator(context, x);
    }

    @ModifyConstant(method= "renderStatusBars", constant= @Constant(intValue= 39, ordinal= 0))
    public int moveBarUp(int value) {
        if (this.client.player.getJumpingMount() != null)
            return value;
        return 46;
    }

    public void renderManaBar(DrawContext context, int x) {
        this.client.getProfiler().push("manaBar");

        assert this.client.player != null;
        int y = this.scaledHeight - 36;
        float offset = (float) ((ManaEntity)this.client.player).getMana() / ((ManaEntity)this.client.player).getMaxMana();
        int line = (int) (offset * 182);
        context.drawTexture(new Identifier(Clockwork.MODID, "textures/gui/hud/mana_bar.png"), x, y, 0, 0, 182, 5, 182, 10);
        context.drawTexture(new Identifier(Clockwork.MODID, "textures/gui/hud/mana_bar.png"), x, y, 0, 5, line, 5, 182, 10);

        this.client.getProfiler().pop();
    }

    public void renderSeparator(DrawContext context, int x) {
        this.client.getProfiler().push("separator");

        int k = x + 80;
        int y = this.scaledHeight - 34;
        context.drawTexture(new Identifier(Clockwork.MODID, "textures/gui/hud/separator.png"), k, y, 0, 0, 22, 6, 22, 6);

        this.client.getProfiler().pop();
    }

    public void renderLevel(DrawContext context) {
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");

            String string = "" + this.client.player.experienceLevel;
            int x = this.scaledWidth / 2 - this.getTextRenderer().getWidth(string) - 12;
            int y = this.scaledHeight - 32;
            context.drawText(this.getTextRenderer(), string, x + 1, y, 0, false);
            context.drawText(this.getTextRenderer(), string, x - 1, y, 0, false);
            context.drawText(this.getTextRenderer(), string, x, y + 1, 0, false);
            context.drawText(this.getTextRenderer(), string, x, y - 1, 0, false);
            context.drawText(this.getTextRenderer(), string, x, y, 8453920, false);

            this.client.getProfiler().pop();
        }
    }

    public void renderMana(DrawContext context) {
        this.client.getProfiler().push("manaLevel");

        assert this.client.player != null;
        String string = "" + ((ManaEntity)this.client.player).getMana();
        int x = this.scaledWidth / 2 + 12;
        int y = this.scaledHeight - 37;
        context.drawText(this.getTextRenderer(), string, x + 1, y, 0, false);
        context.drawText(this.getTextRenderer(), string, x - 1, y, 0, false);
        context.drawText(this.getTextRenderer(), string, x, y + 1, 0, false);
        context.drawText(this.getTextRenderer(), string, x, y - 1, 0, false);
        context.drawText(this.getTextRenderer(), string, x, y, 60646, false);

        this.client.getProfiler().pop();
    }

}