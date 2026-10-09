package com.trafficcraft.horizontaltraffic.client.model;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SmartPillarBakedModel extends ForwardingBakedModel {

    private final List<BakedQuad> compositeQuads;

    public SmartPillarBakedModel(BakedModel original, BakedModel barModel, BlockState state) {
        this.wrapped = original;

        List<BakedQuad> quads = new ArrayList<>();
        RandomSource random = RandomSource.create(42L);

        // Include all quads from the original smart pillar model (center + active cardinal bars)
        quads.addAll(original.getQuads(state, null, random));
        for (Direction side : Direction.values()) {
            quads.addAll(original.getQuads(state, side, random));
        }

        // Gather base bar quads from the bar model
        List<BakedQuad> barQuads = new ArrayList<>();
        if (barModel != null) {
            barQuads.addAll(barModel.getQuads(null, null, random));
            for (Direction side : Direction.values()) {
                barQuads.addAll(barModel.getQuads(null, side, random));
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

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side == null) {
            return this.compositeQuads;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter emitter = context.getEmitter();
        for (BakedQuad quad : this.compositeQuads) {
            emitter.fromVanilla(quad, null, null);
            emitter.emit();
        }
    }
}
