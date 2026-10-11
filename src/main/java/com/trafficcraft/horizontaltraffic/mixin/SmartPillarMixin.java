package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import io.github.aftersans53228.aft_fabroads.block.pillarblock.SmartPillar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmartPillar.class)
public abstract class SmartPillarMixin extends PipeBlock {

    public SmartPillarMixin(float apothem, Properties properties) {
        super(apothem, properties);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onRegisterProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(
                Rotation45Helper.NORTH_EAST,
                Rotation45Helper.SOUTH_EAST,
                Rotation45Helper.SOUTH_WEST,
                Rotation45Helper.NORTH_WEST
        );
    }

    @Inject(method = "<init>()V", at = @At("RETURN"), remap = false)
    private void onConstruct(CallbackInfo ci) {
        if (this.defaultBlockState().hasProperty(Rotation45Helper.NORTH_EAST)) {
            this.registerDefaultState(this.defaultBlockState()
                    .setValue(Rotation45Helper.NORTH_EAST, false)
                    .setValue(Rotation45Helper.SOUTH_EAST, false)
                    .setValue(Rotation45Helper.SOUTH_WEST, false)
                    .setValue(Rotation45Helper.NORTH_WEST, false));
        }
    }

    @Inject(method = "withConnectionProperties", at = @At("RETURN"), cancellable = true, remap = false)
    private void onWithConnectionProperties(BlockGetter world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state != null && state.hasProperty(Rotation45Helper.NORTH_EAST)) {
            cir.setReturnValue(state
                    .setValue(Rotation45Helper.NORTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(world, pos.north().east()))
                    .setValue(Rotation45Helper.SOUTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(world, pos.south().east()))
                    .setValue(Rotation45Helper.SOUTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(world, pos.south().west()))
                    .setValue(Rotation45Helper.NORTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(world, pos.north().west())));
        }
    }

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        BlockState res = cir.getReturnValue();
        if (res != null && res.hasProperty(Rotation45Helper.NORTH_EAST)) {
            cir.setReturnValue(res
                    .setValue(Rotation45Helper.NORTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().east()))
                    .setValue(Rotation45Helper.SOUTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().east()))
                    .setValue(Rotation45Helper.SOUTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().west()))
                    .setValue(Rotation45Helper.NORTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().west())));
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, neighborPos, isMoving);
        if (!level.isClientSide()) {
            BlockState updated = state
                    .setValue(Rotation45Helper.NORTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().east()))
                    .setValue(Rotation45Helper.SOUTH_EAST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().east()))
                    .setValue(Rotation45Helper.SOUTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().west()))
                    .setValue(Rotation45Helper.NORTH_WEST, Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().west()));
            if (!updated.equals(state)) {
                level.setBlock(pos, updated, Block.UPDATE_ALL);
            }
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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = super.getShape(state, level, pos, context);
        if (state.hasProperty(Rotation45Helper.NORTH_EAST) && state.getValue(Rotation45Helper.NORTH_EAST)) {
            shape = Shapes.or(shape, Block.box(6.0, 6.0, 0.0, 16.0, 10.0, 10.0));
        }
        if (state.hasProperty(Rotation45Helper.SOUTH_EAST) && state.getValue(Rotation45Helper.SOUTH_EAST)) {
            shape = Shapes.or(shape, Block.box(6.0, 6.0, 6.0, 16.0, 10.0, 16.0));
        }
        if (state.hasProperty(Rotation45Helper.SOUTH_WEST) && state.getValue(Rotation45Helper.SOUTH_WEST)) {
            shape = Shapes.or(shape, Block.box(0.0, 6.0, 6.0, 10.0, 10.0, 16.0));
        }
        if (state.hasProperty(Rotation45Helper.NORTH_WEST) && state.getValue(Rotation45Helper.NORTH_WEST)) {
            shape = Shapes.or(shape, Block.box(0.0, 6.0, 0.0, 10.0, 10.0, 10.0));
        }
        return shape;
    }
}
