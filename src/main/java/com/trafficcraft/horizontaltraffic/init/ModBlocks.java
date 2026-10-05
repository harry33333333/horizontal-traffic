package com.trafficcraft.horizontaltraffic.init;

import com.trafficcraft.horizontaltraffic.HorizontalTrafficMod;
import com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock;
import de.mrjulsen.trafficcraft.block.data.TrafficLightModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
    public static final HorizontalTrafficLightBlock HORIZONTAL_TRAFFIC_LIGHT =
            new HorizontalTrafficLightBlock(TrafficLightModel.THREE_LIGHTS);
    public static final HorizontalTrafficLightBlock HORIZONTAL_DOUBLE_TRAFFIC_LIGHT =
            new HorizontalTrafficLightBlock(TrafficLightModel.TWO_LIGHTS);
    public static final HorizontalTrafficLightBlock HORIZONTAL_SINGLE_TRAFFIC_LIGHT =
            new HorizontalTrafficLightBlock(TrafficLightModel.ONE_LIGHT);

    public static void register() {
        registerBlock("horizontal_traffic_light", HORIZONTAL_TRAFFIC_LIGHT);
        registerBlock("horizontal_double_traffic_light", HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
        registerBlock("horizontal_single_traffic_light", HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(HorizontalTrafficMod.MOD_ID, name), block);
    }
}
