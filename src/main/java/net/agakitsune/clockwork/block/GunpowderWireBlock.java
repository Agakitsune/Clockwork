package net.agakitsune.clockwork.block;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.block.enums.IgniteSource;
import net.agakitsune.clockwork.sounds.ClockworkSounds;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GunpowderWireBlock extends Block {

    private static final EnumProperty<WireConnection> NORTH = Properties.NORTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> EAST = Properties.EAST_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WEST = Properties.WEST_WIRE_CONNECTION;
    private static final BooleanProperty IGNITED = BooleanProperty.of("ignited");
    private static final IntProperty FUSE = IntProperty.of("fuse", 0, 10);
    private static final EnumProperty<IgniteSource> SOURCE = EnumProperty.of("source", IgniteSource.class);

    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0);

    private static final VoxelShape NORTH_UP_SHAPE = VoxelShapes.union(NORTH_SHAPE, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0));
    private static final VoxelShape EAST_UP_SHAPE = VoxelShapes.union(EAST_SHAPE, Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0));
    private static final VoxelShape SOUTH_UP_SHAPE = VoxelShapes.union(SOUTH_SHAPE, Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0));
    private static final VoxelShape WEST_UP_SHAPE = VoxelShapes.union(WEST_SHAPE, Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0));

    public GunpowderWireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(NORTH, WireConnection.NONE)
                        .with(EAST, WireConnection.NONE)
                        .with(SOUTH, WireConnection.NONE)
                        .with(WEST, WireConnection.NONE)
                        .with(IGNITED, false)
                        .with(FUSE, 10)
                        .with(SOURCE, IgniteSource.NONE)
        );
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = state.get(directionToWire(direction));
            if (connection == WireConnection.SIDE) {
                switch (direction) {
                    case NORTH -> voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
                    case SOUTH -> voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
                    case EAST -> voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
                    case WEST -> voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
                }
                continue;
            }
            if (connection != WireConnection.UP) continue;
            switch (direction) {
                case NORTH -> voxelShape = VoxelShapes.union(voxelShape, NORTH_UP_SHAPE);
                case SOUTH -> voxelShape = VoxelShapes.union(voxelShape, SOUTH_UP_SHAPE);
                case EAST -> voxelShape = VoxelShapes.union(voxelShape, EAST_UP_SHAPE);
                case WEST -> voxelShape = VoxelShapes.union(voxelShape, WEST_UP_SHAPE);
            }
        }
        return voxelShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForState(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            EnumProperty<WireConnection> connection = directionToWire(direction);
            if (!state.get(connection).isConnected()) {
                WireConnection wireConnection = this.getConnection(world, pos, direction);
                state = state.with(connection, wireConnection);
            }
        }

        return state;
    }

    private WireConnection getConnection(BlockView world, BlockPos pos, Direction direction) {
        BlockPos offset = pos.offset(direction);
        BlockState blockstate = world.getBlockState(offset);
        if (!world.getBlockState(pos.up()).isSolidBlock(world, pos)) {
            if (this.canTop(world, offset, blockstate) && connectsTo(world.getBlockState(offset.up()))) {
                if (blockstate.isSideSolidFullSquare(world, offset, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.NONE;
            }
        }

        if (connectsTo(blockstate, direction)) {
            return WireConnection.SIDE;
        }
        if (!blockstate.isSolidBlock(world, offset) && connectsTo(world.getBlockState(offset.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    private static boolean stronglyConnectsTo(BlockState state) {
        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        return state.isOf(ClockworkBlocks.GUNPOWDER_WIRE);
    }

    private static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    private static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (stronglyConnectsTo(state))
            return true;
        if (state.isOf(Blocks.REPEATER)) {
            Direction direction = state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        }
        if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        }
        if (isExplosive(state))
            return true;
        return state.emitsRedstonePower() && dir != null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 ->
                    state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90 ->
                    state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90 ->
                    state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK -> state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default -> super.mirror(state, mirror);
        };
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, IGNITED, FUSE, SOURCE);
    }

    public static boolean canBeIgnited(BlockState state) {
        return state.isOf(ClockworkBlocks.GUNPOWDER_WIRE) && !state.get(IGNITED);
    }

    public static void ignite(World world, BlockState state, BlockPos pos, IgniteSource source, boolean ignited) {
        world.setBlockState(pos, state.with(IGNITED, true).with(SOURCE, source));
        if (ignited) {
            world.playSound(null, pos, ClockworkSounds.BLOCK_GUNPOWDER_WIRE_IGNITE, SoundCategory.BLOCKS, 0.5f, world.getRandom().nextFloat() * 0.4f + 0.8f);
        } else {
            Clockwork.LOGGER.info("Should play some sound like right now bro: " + world.isClient);
            world.playSound(null, pos, ClockworkSounds.BLOCK_GUNPOWDER_WIRE_FUSE, SoundCategory.BLOCKS, 0.5f, world.getRandom().nextFloat() * 0.4f + 0.8f);
        }
        world.scheduleBlockTick(pos, ClockworkBlocks.GUNPOWDER_WIRE, 1);
    }

    private static <T extends ParticleEffect> void emitParticle(ServerWorld world, T particle, BlockPos pos, int count, double speed) {
        emitParticle(world, particle, pos, 0.5, 0.5, count, speed);
    }

    private static <T extends ParticleEffect> void emitParticle(ServerWorld world, T particle, BlockPos pos, double offsetX, double offsetZ, int count, double speed) {
        emitParticle(world, particle, pos, offsetX, 0.0, offsetZ, count, speed);
    }

    private static <T extends ParticleEffect> void emitParticle(ServerWorld world, T particle, BlockPos pos, double offsetX, double offsetY, double offsetZ, int count, double speed) {
        world.spawnParticles(
                particle,
                pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ,
                count,
                0.01, 0.01, 0.01,
                speed
        );
    }

    private void emitParticle(BlockState state, ServerWorld world, BlockPos pos, Random random, int fuse) {
        final IgniteSource source = state.get(SOURCE);
        List<Direction> directions = new ArrayList<>();

        if (state.get(NORTH).isConnected())
            directions.add(Direction.NORTH);
        if (state.get(SOUTH).isConnected())
            directions.add(Direction.SOUTH);
        if (state.get(EAST).isConnected())
            directions.add(Direction.EAST);
        if (state.get(WEST).isConnected())
            directions.add(Direction.WEST);

        final int n = 2 + (random.nextInt() % 2);
        final double speed = 0.01f;
        if (source == IgniteSource.PLAYER) {
            if (fuse == 10 || directions.isEmpty()) {
                emitParticle(world,ParticleTypes.FLAME,pos,n,speed);
                emitParticle(world,ParticleTypes.SMOKE,pos,n,speed);
            } else {
                for (Direction dir : directions) {
                    if (state.get(directionToWire(dir)) == WireConnection.UP) {
                        double dx;
                        double dy = 0.0f;
                        double dz;
                        if (fuse > 5) {
                            final float factor = (fuse - 5) / 5.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f * (1.0f - factor);
                            dz = 0.5f + dir.getOffsetZ() * 0.5f * (1.0f - factor);
                        } else {
                            final float factor = fuse  / 5.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f;
                            dz = 0.5f + dir.getOffsetZ() * 0.5f;
                            dy = 1.0f - factor;
                        }
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dy, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dy, dz, n, speed);
                    } else {
                        final float factor = fuse / 10.0f;
                        final double dx = 0.5f + dir.getOffsetX() * 0.5f * (1.0f - factor);
                        final double dz = 0.5f + dir.getOffsetZ() * 0.5f * (1.0f - factor);
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dz, n, speed);
                    }
                }
            }
        } else {
            final List<Direction> sources = IgniteSource.toDirections(source);
            for (Direction r : sources) {
                directions.remove(r);
            }
            if (fuse > 5) {
                for (Direction dir : sources) {
                    if (state.get(directionToWire(dir)) == WireConnection.UP) {
                        double dx;
                        double dy = 0.0f;
                        double dz;
                        if (fuse > 7) {
                            final float factor = (fuse - 7) / 3.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f;
                            dz = 0.5f + dir.getOffsetZ() * 0.5f;
                            dy = factor;
                        } else {
                            final float factor = (fuse - 5) / 2.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f * factor;
                            dz = 0.5f + dir.getOffsetZ() * 0.5f * factor;
                        }
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dy, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dy, dz, n, speed);
                    } else {
                        final float factor = (fuse - 5) / 5.0f;
                        final double dz = 0.5f + dir.getOffsetZ() * 0.5f * factor;
                        final double dx = 0.5f + dir.getOffsetX() * 0.5f * factor;
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dz, n, speed);
                    }
                }
            } else if (directions.isEmpty()) {
                emitParticle(world,ParticleTypes.FLAME,pos,n,speed);
                emitParticle(world,ParticleTypes.SMOKE,pos,n,speed);
            } else {
                for (Direction dir : directions) {
                    if (state.get(directionToWire(dir)) == WireConnection.UP) {
                        double dx;
                        double dy = 0.0f;
                        double dz;
                        if (fuse > 3) {
                            final float factor = (fuse - 3) / 2.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f * (1.0f - factor);
                            dz = 0.5f + dir.getOffsetZ() * 0.5f * (1.0f - factor);
                        } else {
                            final float factor = fuse / 3.0f;
                            dx = 0.5f + dir.getOffsetX() * 0.5f;
                            dz = 0.5f + dir.getOffsetZ() * 0.5f;
                            dy = 1.0f - factor;
                        }
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dy, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dy, dz, n, speed);
                    } else {
                        final float factor = fuse / 5.0f;
                        final double dz = 0.5f + dir.getOffsetZ() * 0.5f * (1.0f - factor);
                        final double dx = 0.5f + dir.getOffsetX() * 0.5f * (1.0f - factor);
                        emitParticle(world, ParticleTypes.FLAME, pos, dx, dz, n, speed);
                        emitParticle(world, ParticleTypes.SMOKE, pos, dx, dz, n, speed);
                    }
                }
            }
        }
    }

    private static boolean isExplosive(BlockState state) {
        return state.isOf(Blocks.TNT) || state.isOf(ClockworkBlocks.GUNPOWDER_BARREL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(IGNITED)) {
            return;
        }
        int fuse = state.get(FUSE);
        if (fuse > 0) {
            world.scheduleBlockTick(pos, this, 1);
            world.setBlockState(pos, state.with(FUSE, fuse - 1));

            emitParticle(state,world,pos,random,fuse);
        } else {
            final List<EnumProperty<WireConnection>> connections = new ArrayList<>(4);
            connections.add(NORTH);
            connections.add(SOUTH);
            connections.add(EAST);
            connections.add(WEST);

            world.removeBlock(pos, false);

            for (EnumProperty<WireConnection> connect : connections) {
                if (!state.get(connect).isConnected())
                    continue;
                BlockState floor = world.getBlockState(pos.down());
                if (isExplosive(floor)) {
                    if (floor.isOf(ClockworkBlocks.GUNPOWDER_BARREL)) {
                        GunpowderBarrelBlock.explode(world, pos.down());
                    } else {
                        world.setBlockState(pos.down(), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                        TntBlock.primeTnt(world, pos.down());
                    }
                }
                Direction direction = wireToDirection(connect);
                BlockPos next = pos.offset(direction);
                if (state.get(connect) == WireConnection.UP)
                    next = next.up();
                BlockState nextState = world.getBlockState(next);
                if (isExplosive(nextState)) {
                    if (nextState.isOf(ClockworkBlocks.GUNPOWDER_BARREL)) {
                        GunpowderBarrelBlock.explode(world, next);
                    } else {
                        world.setBlockState(next, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                        TntBlock.primeTnt(world, next);
                    }
                } else if (!nextState.isOf(this)) {
                    next = next.down();
                    nextState = world.getBlockState(next);
                    if (nextState.isOf(this))
                        ignite(world, nextState.with(directionToWire(direction.getOpposite()), WireConnection.UP), next, nextState.get(SOURCE).union(IgniteSource.fromDirection(direction.getOpposite())), false);
                } else {
                    ignite(world, nextState.with(directionToWire(direction.getOpposite()), WireConnection.SIDE), next, nextState.get(SOURCE).union(IgniteSource.fromDirection(direction.getOpposite())), false);
                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos down = pos.down();
        BlockState downState = world.getBlockState(down);
        return canTop(world, down, downState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            if (!this.canTop(world, neighborPos, neighborState)) {
                return Blocks.AIR.getDefaultState();
            }
            return state;
        }

        if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        }

        WireConnection wireConnection = this.getConnection(world, pos, direction);
        return state.with(directionToWire(direction), wireConnection);
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = state.get(directionToWire(direction));

            if (connection == WireConnection.NONE || stronglyConnectsTo(world.getBlockState(pos.offset(direction)))) continue;

            BlockPos downSide = pos.offset(direction).down();
            BlockState downSideState = world.getBlockState(downSide);

            if (stronglyConnectsTo(downSideState)) {
                BlockPos down = pos.down();
                world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(down), downSide, down, flags, maxUpdateDepth);
            }

            BlockPos upSide = pos.offset(direction).up();
            BlockState upSideState = world.getBlockState(upSide);

            if (!stronglyConnectsTo(upSideState)) continue;

            BlockPos up = pos.down();
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(up), upSide, up, flags, maxUpdateDepth);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient) {
            return;
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateDeepNeighbors(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, false);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateDeepNeighbors(world, pos);
    }

    private void updateDeepNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos offset = pos.offset(direction);
            if (world.getBlockState(offset).isSolidBlock(world, offset)) {
                this.updateNeighbors(world, offset.up());
                continue;
            }
            this.updateNeighbors(world, offset.down());
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!stronglyConnectsTo(world.getBlockState(pos))) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    private boolean shouldIgnite(World world, BlockPos pos, Direction direction) {
        return world.isEmittingRedstonePower(pos.offset(direction), direction);
    }

    private IgniteSource getIgniteSource(World world, BlockPos pos, Direction direction) {
        if (direction == Direction.DOWN) {
            for (Direction offset : Direction.values()) {
                if (world.getBlockState(pos.offset(offset).down()).isOf(Blocks.REDSTONE_WIRE))
                    return IgniteSource.fromDirection(offset);
            }
        }
        return IgniteSource.fromDirection(direction);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.canModifyBlocks())
            return ActionResult.PASS;
        ItemStack itemstack = player.getStackInHand(hand);
        if (itemstack.isOf(Items.FLINT_AND_STEEL) || itemstack.isOf(Items.FIRE_CHARGE)) {
            ignite(world, state, pos, IgniteSource.PLAYER, true);
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient)
            return;
        if (state.canPlaceAt(world, pos)) {
            for (Direction dir : Direction.values()) {
                EnumProperty<WireConnection> connection = directionToWire(dir);
                if (connection != null && !state.get(connection).isConnected())
                    continue;
                if (shouldIgnite(world, pos, dir)) {
                    ignite(world, state, pos, state.get(SOURCE).union(getIgniteSource(world, pos, dir)), true);
                }
            }
        } else {
            GunpowderWireBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient)
            return;
        if (entity instanceof ProjectileEntity projectile) {
            if (projectile.isOnFire() && projectile.canModifyAt(world, pos)) {
                ignite(world, state, pos, IgniteSource.PLAYER, true);
            }
        }
    }

    private boolean canTop(BlockView world, BlockPos pos, BlockState state) {
        return state.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    private EnumProperty<WireConnection> directionToWire(Direction direction) {
        return switch (direction) {
            case DOWN, UP -> null;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    private Direction wireToDirection(EnumProperty<WireConnection> wire) {
        if (wire == NORTH)
            return Direction.NORTH;
        if (wire == SOUTH)
            return Direction.SOUTH;
        if (wire == EAST)
            return Direction.EAST;
        if (wire == WEST)
            return Direction.WEST;
        return null;
    }

}
