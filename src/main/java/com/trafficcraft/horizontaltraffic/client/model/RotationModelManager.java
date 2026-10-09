package com.trafficcraft.horizontaltraffic.client.model;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import io.github.aftersans53228.aft_fabroads.block.pillarblock.SmartPillar;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RotationModelManager {

    private static final Map<BlockState, BakedModel> CACHE = new ConcurrentHashMap<>();

    public static void init() {
        // No ModelLoadingPlugin or addModels needed! Clean and zero texture/atlas impact.
    }

    public static void clearCache() {
        CACHE.clear();
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

            return CACHE.computeIfAbsent(state, s -> new SmartPillarBakedModel(original, s));
        }

        if (state.hasProperty(Rotation45Helper.DIAGONAL) && state.getValue(Rotation45Helper.DIAGONAL)) {
            return CACHE.computeIfAbsent(state, s -> new RotatedBakedModel(original, s, 45.0));
        }

        return null;
    }
}
