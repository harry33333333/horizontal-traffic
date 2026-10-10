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

        // Gather unculled quads
        for (BakedQuad q : original.getQuads(state, null, random)) {
            quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, 1.0, angleDegrees));
        }

        // Gather culled quads and also rotate them into unculled list
        for (Direction side : Direction.values()) {
            for (BakedQuad q : original.getQuads(state, side, random)) {
                quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, 1.0, angleDegrees));
            }
        }

        this.rotatedQuads = Collections.unmodifiableList(quads);
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
