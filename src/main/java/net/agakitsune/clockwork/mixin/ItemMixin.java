package net.agakitsune.clockwork.mixin;

import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.agakitsune.clockwork.item.ClockworkItems;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(Item.class)
public class ItemMixin {

    @Inject(method= "useOnBlock", at= @At("RETURN"), cancellable = true)
    private void overrideGunpowder(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getStack().isOf(Items.GUNPOWDER) && !cir.getReturnValue().isAccepted()) {
            ActionResult result = ClockworkItems._GUNPOWDER_.useOnBlock(context);
            if (result.isAccepted())
                cir.setReturnValue(result);
        }
    }
}
