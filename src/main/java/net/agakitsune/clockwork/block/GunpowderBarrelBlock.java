package net.agakitsune.clockwork.block;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.block.enums.IgniteSource;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class GunpowderBarrelBlock extends Block {
    public GunpowderBarrelBlock(Settings settings) {
        super(settings);
    }

    private static boolean inWater(BlockPos pos, World world) {
        FluidState fluid = world.getFluidState(pos);
        if (!fluid.isIn(FluidTags.WATER)) {
            return false;
        }
        if (fluid.isStill()) {
            return true;
        }
        float f = fluid.getLevel();
        return !(f < 2.0f);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.canModifyBlocks())
            return ActionResult.PASS;
        ItemStack itemstack = player.getStackInHand(hand);
        if (itemstack.isOf(Items.FLINT_AND_STEEL) || itemstack.isOf(Items.FIRE_CHARGE)) {
            Clockwork.LOGGER.info(String.valueOf(world.isClient));
            explode(world, pos, player);
            if (!player.isCreative()) {
                if (itemstack.isOf(Items.FLINT_AND_STEEL)) {
                    world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                    itemstack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                } else {
                    world.playSound(player, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                    itemstack.decrement(1);
                }
            }
            player.incrementStat(Stats.USED.getOrCreateStat(itemstack.getItem()));
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.isAir() || explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK) {
            return;
        }
        explode(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        Clockwork.LOGGER.info(String.valueOf(world.isClient));
        for (Direction dir : Direction.values()) {
            if (world.isEmittingRedstonePower(pos.offset(dir), dir)) {
                explode(world, pos);
            }
        }
    }


    public static void explode(World world, BlockPos pos) {
        explode(world, pos, null);
    }

    public static void explode(World world, BlockPos explodedPos, @Nullable Entity emitter) {
        world.removeBlock(explodedPos, false);
        boolean hasWaterSide = Direction.Type.HORIZONTAL.stream().map(explodedPos::offset).anyMatch(pos -> GunpowderBarrelBlock.inWater(pos, world));
        final boolean isInWater = hasWaterSide || world.getFluidState(explodedPos.up()).isIn(FluidTags.WATER);
        ExplosionBehavior behavior = new ExplosionBehavior(){

            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                if (pos.equals(explodedPos) && isInWater) {
                    return Optional.of(Blocks.WATER.getBlastResistance());
                }
                return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
            }
        };
        world.createExplosion(null, world.getDamageSources().create(DamageTypes.EXPLOSION, emitter), behavior, explodedPos.toCenterPos(), 5.0f, false, World.ExplosionSourceType.BLOCK);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        Clockwork.LOGGER.info(String.valueOf(world.isClient));
        //if (!world.isClient) {
            BlockPos pos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.canModifyAt(world, pos)) {
                explode(world, pos, entity);
            }
        //}
    }
}
