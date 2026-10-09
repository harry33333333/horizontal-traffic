package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import io.github.aftersans53228.aft_fabroads.block.pillarblock.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
        HorizontalStraightPillar.class,
        HorizontalStraightPillarThin.class,
        HorizontalCornerPillar.class,
        HorizontalTshapedPillar.class,
        VerticalCornerPillar.class,
        VerticalCornerPillarThin.class,
        VerticalTshapedPillar.class,
        VerticalTshapedPillarType2.class,
        RoadMastPillar.class,
        RoadMastPillarBase.class
})
public abstract class AftDirectionalPillarsMixin extends HorizontalDirectionalBlock {

    public AftDirectionalPillarsMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onRegisterProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Rotation45Helper.DIAGONAL);
    }

    @Inject(method = "<init>()V", at = @At("RETURN"), remap = false)
    private void onConstruct(CallbackInfo ci) {
        if (this.defaultBlockState().hasProperty(Rotation45Helper.DIAGONAL)) {
            this.registerDefaultState(this.defaultBlockState().setValue(Rotation45Helper.DIAGONAL, false));
        }
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void onGetStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state != null && state.hasProperty(Rotation45Helper.DIAGONAL)) {
            Rotation45Helper.DirectionState ds = Rotation45Helper.getPlacementDirection(context.getRotation());
            cir.setReturnValue(state.setValue(HorizontalDirectionalBlock.FACING, ds.facing())
                    .setValue(Rotation45Helper.DIAGONAL, ds.diagonal()));
        }
    }

    @Inject(method = "getShape", at = @At("RETURN"), cancellable = true)
    private void onGetShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.hasProperty(Rotation45Helper.DIAGONAL) && state.getValue(Rotation45Helper.DIAGONAL)) {
            cir.setReturnValue(Rotation45Helper.rotateShape45(cir.getReturnValue()));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        Rotation45Helper.updateDiagonalNeighbors(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        Rotation45Helper.updateDiagonalNeighbors(level, pos);
    }
}
