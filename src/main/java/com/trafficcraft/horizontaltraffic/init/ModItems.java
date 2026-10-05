package com.trafficcraft.horizontaltraffic.init;

import com.trafficcraft.horizontaltraffic.HorizontalTrafficMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final BlockItem HORIZONTAL_TRAFFIC_LIGHT =
            new BlockItem(ModBlocks.HORIZONTAL_TRAFFIC_LIGHT, new Item.Properties());
    public static final BlockItem HORIZONTAL_DOUBLE_TRAFFIC_LIGHT =
            new BlockItem(ModBlocks.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT, new Item.Properties());
    public static final BlockItem HORIZONTAL_SINGLE_TRAFFIC_LIGHT =
            new BlockItem(ModBlocks.HORIZONTAL_SINGLE_TRAFFIC_LIGHT, new Item.Properties());

    public static void register() {
        registerItem("horizontal_traffic_light", HORIZONTAL_TRAFFIC_LIGHT);
        registerItem("horizontal_double_traffic_light", HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
        registerItem("horizontal_single_traffic_light", HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(HorizontalTrafficMod.MOD_ID, name), item);
    }
}
