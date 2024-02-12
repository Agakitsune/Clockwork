package net.agakitsune.clockwork.mixin;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {

    @Inject(method= "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at= @At("TAIL"), cancellable = true)
    private static void connectsTo(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(ClockworkBlocks.GUNPOWDER_WIRE)) {
            cir.setReturnValue(true);
        }
        if (state.isOf(ClockworkBlocks.GUNPOWDER_BARREL)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method= "prepare",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal= 1)
    )
    public boolean prepareIsOf(BlockState state, Block block) {
        Clockwork.breaker();
        return state.isOf(block) || state.isOf(ClockworkBlocks.GUNPOWDER_WIRE);
    }

    @Redirect(
            method= "prepare",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal= 2)
    )
    public boolean prepareIsOf2(BlockState state, Block block) {
        return !state.isOf(block) && !state.isOf(ClockworkBlocks.GUNPOWDER_WIRE);
    }
}
