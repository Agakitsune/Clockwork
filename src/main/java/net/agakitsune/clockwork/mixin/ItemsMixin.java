package net.agakitsune.clockwork.mixin;

import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Debug(export = true)
@Mixin(Items.class)
public class ItemsMixin {

    @Redirect(
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {
                                    "stringValue=gunpowder"
                            },
                            ordinal = 0
                    )
            ),

            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/item/Item;*",
                    ordinal = 0
            ),
            method = "<clinit>")
    private static Item gunpowder(Item.Settings settings) {
        return new AliasedBlockItem(ClockworkBlocks.GUNPOWDER_WIRE, settings);
    }
}
