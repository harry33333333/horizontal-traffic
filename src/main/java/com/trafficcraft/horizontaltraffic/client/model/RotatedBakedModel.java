package com.trafficcraft.horizontaltraffic.client.model;

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

public class RotatedBakedModel extends ForwardingBakedModel {

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
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter emitter = context.getEmitter();
        for (BakedQuad quad : this.rotatedQuads) {
            emitter.fromVanilla(quad, null, null);
            emitter.emit();
        }
    }
}
