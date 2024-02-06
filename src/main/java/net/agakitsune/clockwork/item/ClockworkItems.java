package net.agakitsune.clockwork.item;

import net.agakitsune.clockwork.Clockwork;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClockworkItems {
    public static final Item SCROLL = registerItem("scroll", new ScrollItem());

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Clockwork.MODID, name), item);
    }

    public static void registerItems() {
        Clockwork.LOGGER.info("Registering items for " + Clockwork.MODID);
    }

}
