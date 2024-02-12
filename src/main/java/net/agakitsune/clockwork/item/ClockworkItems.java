package net.agakitsune.clockwork.item;

import net.agakitsune.clockwork.Clockwork;
import net.agakitsune.clockwork.block.ClockworkBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClockworkItems {
    public static final Item SCROLL = registerItem("scroll", new ScrollItem());
    public static final Item _GUNPOWDER_ = registerItem("test", new AliasedBlockItem(ClockworkBlocks.GUNPOWDER_WIRE, new FabricItemSettings()));
    public static final Item GUNPOWDER_BARREL = registerItem("gunpowder_barrel", new BlockItem(ClockworkBlocks.GUNPOWDER_BARREL, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        Clockwork.LOGGER.info("Registering " + name);
        return Registry.register(Registries.ITEM, new Identifier(Clockwork.MODID, name), item);
    }

    public static void registerItems() {
        Clockwork.LOGGER.info("Registering items for " + Clockwork.MODID);
    }

}
