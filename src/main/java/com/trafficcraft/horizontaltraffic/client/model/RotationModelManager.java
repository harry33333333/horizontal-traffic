package com.trafficcraft.horizontaltraffic.client.model;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import io.github.aftersans53228.aft_fabroads.block.pillarblock.SmartPillar;
import io.github.aftersans53228.aft_fabroads.regsitry.AFRoadsBlockRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RotationModelManager {

    public static final ResourceLocation SMART_PILLAR_BAR_ID =
            new ResourceLocation("aft_fabroads", "block/pillar/smart_pillar_bar");
    public static final ResourceLocation SMART_PILLAR_THIN_BAR_ID =
            new ResourceLocation("aft_fabroads", "block/pillar/smart_pillar_thin_bar");

    private static final Map<BlockState, BakedModel> CACHE = new ConcurrentHashMap<>();

    private static BakedModel cachedBarModel = null;
    private static BakedModel cachedThinBarModel = null;

    public static void init() {
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(SMART_PILLAR_BAR_ID, SMART_PILLAR_THIN_BAR_ID);
        });
    }

    public static void clearCache() {
        CACHE.clear();
        cachedBarModel = null;
        cachedThinBarModel = null;
    }

    private static BakedModel getBarModel(boolean isThin) {
        if (isThin) {
            if (cachedThinBarModel == null) {
                try {
                    FabricBakedModelManager manager = (FabricBakedModelManager) Minecraft.getInstance().getModelManager();
                    cachedThinBarModel = manager.getModel(SMART_PILLAR_THIN_BAR_ID);
                } catch (Throwable ignored) {}
            }
            return cachedThinBarModel;
        } else {
            if (cachedBarModel == null) {
                try {
                    FabricBakedModelManager manager = (FabricBakedModelManager) Minecraft.getInstance().getModelManager();
                    cachedBarModel = manager.getModel(SMART_PILLAR_BAR_ID);
                } catch (Throwable ignored) {}
            }
            return cachedBarModel;
        }
    }

    public static BakedModel getModel(BlockState state, BakedModel original) {
        if (state == null || original == null) return null;

        if (state.getBlock() instanceof SmartPillar) {
            boolean hasDiag = (state.hasProperty(Rotation45Helper.NORTH_EAST) && state.getValue(Rotation45Helper.NORTH_EAST))
                    || (state.hasProperty(Rotation45Helper.SOUTH_EAST) && state.getValue(Rotation45Helper.SOUTH_EAST))
                    || (state.hasProperty(Rotation45Helper.SOUTH_WEST) && state.getValue(Rotation45Helper.SOUTH_WEST))
                    || (state.hasProperty(Rotation45Helper.NORTH_WEST) && state.getValue(Rotation45Helper.NORTH_WEST));

            if (!hasDiag) {
                return null;
            }

            return CACHE.computeIfAbsent(state, s -> {
                boolean isThin = s.is(AFRoadsBlockRegistry.SmartPillarThin);
                BakedModel barModel = getBarModel(isThin);
                return new SmartPillarBakedModel(original, barModel, s);
            });
        }

        if (state.hasProperty(Rotation45Helper.DIAGONAL) && state.getValue(Rotation45Helper.DIAGONAL)) {
            return CACHE.computeIfAbsent(state, s -> new RotatedBakedModel(original, s, 45.0));
        }

        return null;
    }
}
