package com.trafficcraft.horizontaltraffic;

import com.trafficcraft.horizontaltraffic.init.ModBlockEntities;
import com.trafficcraft.horizontaltraffic.init.ModBlocks;
import com.trafficcraft.horizontaltraffic.init.ModCreativeTabs;
import com.trafficcraft.horizontaltraffic.init.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorizontalTrafficMod implements ModInitializer {
    public static final String MOD_ID = "horizontal_traffic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Horizontal Traffic addon for TrafficCraft...");

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModCreativeTabs.register();

        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("aft_fabroads")) {
            try {
                Class<?> staticsClass = Class.forName("io.github.aftersans53228.aft_fabroads.AFRoadsStatics");
                java.lang.reflect.Field field = staticsClass.getField("CAN_PILLAR_CONNECT");
                @SuppressWarnings("unchecked")
                java.util.List<net.minecraft.world.level.block.Block> list = (java.util.List<net.minecraft.world.level.block.Block>) field.get(null);
                list.add(ModBlocks.HORIZONTAL_TRAFFIC_LIGHT);
                list.add(ModBlocks.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
                list.add(ModBlocks.HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
                LOGGER.info("Registered Horizontal Traffic lights to aft_fabroads pillar connect list.");
            } catch (Throwable t) {
                LOGGER.warn("Could not register to aft_fabroads CAN_PILLAR_CONNECT: {}", t.getMessage());
            }
        }

        LOGGER.info("Horizontal Traffic successfully initialized!");
    }
}
