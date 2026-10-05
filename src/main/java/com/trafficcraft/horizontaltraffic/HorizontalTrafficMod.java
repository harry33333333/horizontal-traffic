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

        LOGGER.info("Horizontal Traffic successfully initialized!");
    }
}
