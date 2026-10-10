package com.trafficcraft.horizontaltraffic.client.model;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
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

public class SmartPillarBakedModel implements BakedModel {

    protected final BakedModel wrapped;
    private final List<BakedQuad> compositeQuads;

    public SmartPillarBakedModel(BakedModel original, BlockState state) {
        this.wrapped = original;

        List<BakedQuad> quads = new ArrayList<>();
        RandomSource random = RandomSource.create(42L);

        // Include all quads from the original smart pillar model (center + active cardinal bars)
        quads.addAll(original.getQuads(state, null, random));
        for (Direction side : Direction.values()) {
            quads.addAll(original.getQuads(state, side, random));
        }

        // To extract template bar quads, query the original multipart model with NORTH = true
        BlockState northState = state.setValue(net.minecraft.world.level.block.PipeBlock.NORTH, true);
        List<BakedQuad> allNorthQuads = new ArrayList<>();
        allNorthQuads.addAll(original.getQuads(northState, null, random));
        for (Direction side : Direction.values()) {
            allNorthQuads.addAll(original.getQuads(northState, side, random));
        }

        List<BakedQuad> barQuads = new ArrayList<>();
        for (BakedQuad q : allNorthQuads) {
            if (isNorthBarQuad(q)) {
                barQuads.add(q);
            }
        }

        double diagScale = Math.sqrt(2.0);

        if (state.hasProperty(Rotation45Helper.NORTH_EAST) && state.getValue(Rotation45Helper.NORTH_EAST)) {
            for (BakedQuad q : barQuads) {
                quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, diagScale, 45.0));
            }
        }
        if (state.hasProperty(Rotation45Helper.SOUTH_EAST) && state.getValue(Rotation45Helper.SOUTH_EAST)) {
            for (BakedQuad q : barQuads) {
                quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, diagScale, 135.0));
            }
        }
        if (state.hasProperty(Rotation45Helper.SOUTH_WEST) && state.getValue(Rotation45Helper.SOUTH_WEST)) {
            for (BakedQuad q : barQuads) {
                quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, diagScale, 225.0));
            }
        }
        if (state.hasProperty(Rotation45Helper.NORTH_WEST) && state.getValue(Rotation45Helper.NORTH_WEST)) {
            for (BakedQuad q : barQuads) {
                quads.add(QuadTransformHelper.transformQuad(q, 1.0, 1.0, diagScale, 315.0));
            }
        }

        this.compositeQuads = Collections.unmodifiableList(quads);
    }

    private static boolean isNorthBarQuad(BakedQuad q) {
        int[] v = q.getVertices();
        for (int i = 0; i < 4; i++) {
            float z = Float.intBitsToFloat(v[i * 8 + 2]);
            if (z < 0.375f - 1e-4f) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side == null) {
            return this.compositeQuads;
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
