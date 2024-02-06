package net.agakitsune.clockwork.block;

import net.agakitsune.clockwork.Clockwork;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClockworkBlocks {
    public static final Block GUNPOWDER_WIRE = registerBlock("gunpowder_wire", new GunpowderWireBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE)));
    public static final Block TEST_BLOCK = registerBlock("test_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Clockwork.MODID, name), block);
    }

    public static void registerBlocks() {
        Clockwork.LOGGER.info("Registering blocks for " + Clockwork.MODID);
    }
}
