package com.trafficcraft.horizontaltraffic.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RotatedBakedModel implements BakedModel {

    protected final BakedModel wrapped;
    private final List<BakedQuad> rotatedQuads;

    public RotatedBakedModel(BakedModel original, @Nullable BlockState state, double angleDegrees) {
        this.wrapped = original;

        List<BakedQuad> quads = new ArrayList<>();
        RandomSource random = RandomSource.create(42L);

        boolean isTrafficLight = state != null && state.getBlock() instanceof com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock;

        // Gather unculled quads
        for (BakedQuad q : original.getQuads(state, null, random)) {
            quads.add(transformQuadWithBarScale(q, angleDegrees, isTrafficLight));
        }

        // Gather culled quads and also rotate them into unculled list
        for (Direction side : Direction.values()) {
            for (BakedQuad q : original.getQuads(state, side, random)) {
                quads.add(transformQuadWithBarScale(q, angleDegrees, isTrafficLight));
            }
        }

        this.rotatedQuads = Collections.unmodifiableList(quads);
    }

    private static BakedQuad transformQuadWithBarScale(BakedQuad quad, double angleDegrees, boolean isTrafficLight) {
        int[] v = quad.getVertices();
        float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            float x = Float.intBitsToFloat(v[i * 8]);
            float y = Float.intBitsToFloat(v[i * 8 + 1]);
            float z = Float.intBitsToFloat(v[i * 8 + 2]);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }

        float maxDevX = Math.max(Math.abs(minX - 0.5f), Math.abs(maxX - 0.5f));
        float maxDevZ = Math.max(Math.abs(minZ - 0.5f), Math.abs(maxZ - 0.5f));

        if (isTrafficLight) {
            boolean isPoleZ = maxDevZ >= 0.45f && maxDevX <= 0.12f && Math.abs(minY - 0.5f) <= 0.12f && Math.abs(maxY - 0.5f) <= 0.12f;
            boolean isPoleX = maxDevX >= 0.45f && maxDevZ <= 0.12f && Math.abs(minY - 0.5f) <= 0.12f && Math.abs(maxY - 0.5f) <= 0.12f;
            if (isPoleZ) {
                boolean isPositiveDir = maxZ >= 0.95f;
                return QuadTransformHelper.transformTrafficLightPoleQuad(quad, angleDegrees, true, isPositiveDir);
            } else if (isPoleX) {
                boolean isPositiveDir = maxX >= 0.95f;
                return QuadTransformHelper.transformTrafficLightPoleQuad(quad, angleDegrees, false, isPositiveDir);
            }
            return QuadTransformHelper.transformQuad(quad, 1.0, 1.0, 1.0, angleDegrees);
        }

        double scaleX = 1.0;
        double scaleZ = 1.0;
        double diagScale = Math.sqrt(2.0);

        if (maxDevX > 0.15f) {
            scaleX = diagScale;
        }
        if (maxDevZ > 0.15f) {
            scaleZ = diagScale;
        }

        return QuadTransformHelper.transformQuad(quad, scaleX, 1.0, scaleZ, angleDegrees);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side == null) {
            return this.rotatedQuads;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.wrapped.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.wrapped.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.wrapped.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.wrapped.getOverrides();
    }
}
