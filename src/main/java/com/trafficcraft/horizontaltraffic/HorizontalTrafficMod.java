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

        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("aft_fabroads")) {
            try {
                Class<?> staticsClass = Class.forName("io.github.aftersans53228.aft_fabroads.AFRoadsStatics");
                java.lang.reflect.Field field = staticsClass.getField("CAN_PILLAR_CONNECT");
                @SuppressWarnings("unchecked")
                java.util.List<net.minecraft.world.level.block.Block> list = (java.util.List<net.minecraft.world.level.block.Block>) field.get(null);
                list.add(ModBlocks.HORIZONTAL_TRAFFIC_LIGHT);
                list.add(ModBlocks.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
                list.add(ModBlocks.HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
                LOGGER.info("Registered Horizontal Traffic lights to aft_fabroads pillar connect list.");
            } catch (Throwable t) {
                LOGGER.warn("Could not register to aft_fabroads CAN_PILLAR_CONNECT: {}", t.getMessage());
            }
        }

        registerWrenchCallback();
        registerTrafficLightPlacementCallback();

        LOGGER.info("Horizontal Traffic successfully initialized!");
    }

    private void registerWrenchCallback() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (hand != net.minecraft.world.InteractionHand.MAIN_HAND) {
                return net.minecraft.world.InteractionResult.PASS;
            }
            net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
            if (!com.trafficcraft.horizontaltraffic.util.Rotation45Helper.isWrench(stack)) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            net.minecraft.core.BlockPos pos = hitResult.getBlockPos();
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
            net.minecraft.world.level.block.Block block = state.getBlock();

            // Horizontal traffic light handles its own use() method
            if (block instanceof com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            // Directional pillars from aft_fabroads: right click with wrench rotates 45 degrees
            if (state.hasProperty(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL)
                    && state.hasProperty(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING)) {
                if (!level.isClientSide()) {
                    net.minecraft.core.Direction facing = state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
                    boolean diag = state.getValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL);
                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DirectionState next =
                            com.trafficcraft.horizontaltraffic.util.Rotation45Helper.getNextDirection(facing, diag);
                    net.minecraft.world.level.block.state.BlockState newState = state
                            .setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, next.facing())
                            .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL, next.diagonal());
                    level.setBlock(pos, newState, net.minecraft.world.level.block.Block.UPDATE_ALL);
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ANVIL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 0.4f, 1.4f);
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("message.horizontal_traffic.rotated_45",
                                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.getDirectionName(next)),
                            true
                    );
                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.updateDiagonalNeighbors(level, pos);
                }
                return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
            }

            // SmartPillar: right-click with wrench refreshes connections
            if (block instanceof io.github.aftersans53228.aft_fabroads.block.pillarblock.SmartPillar) {
                if (!level.isClientSide()) {
                    net.minecraft.world.level.block.state.BlockState updated = state
                            .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.NORTH_EAST,
                                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().east()))
                            .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.SOUTH_EAST,
                                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().east()))
                            .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.SOUTH_WEST,
                                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.south().west()))
                            .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.NORTH_WEST,
                                    com.trafficcraft.horizontaltraffic.util.Rotation45Helper.canSmartPillarConnectDiagonal(level, pos.north().west()));
                    level.setBlock(pos, updated, net.minecraft.world.level.block.Block.UPDATE_ALL);
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ANVIL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 0.4f, 1.4f);
                }
                return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
            }

            return net.minecraft.world.InteractionResult.PASS;
        });
    }

    private void registerTrafficLightPlacementCallback() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem)
                    || !(blockItem.getBlock() instanceof com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock lightBlock)) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            net.minecraft.core.BlockPos pos = hitResult.getBlockPos();
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);

            if (!com.trafficcraft.horizontaltraffic.util.Rotation45Helper.isTshapedPillar(state)) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            net.minecraft.core.BlockPos offset = com.trafficcraft.horizontaltraffic.util.Rotation45Helper.getTshapedPillarStemOffset(state);
            if (offset.equals(net.minecraft.core.BlockPos.ZERO)) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            net.minecraft.core.BlockPos targetPos = pos.offset(offset);
            net.minecraft.world.level.block.state.BlockState targetState = level.getBlockState(targetPos);
            if (!targetState.canBeReplaced()) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                net.minecraft.core.Direction pillarFacing = state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
                boolean pillarDiagonal = state.hasProperty(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL)
                        && state.getValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL);

                boolean waterlogged = level.getFluidState(targetPos).getType() == net.minecraft.world.level.material.Fluids.WATER;

                net.minecraft.world.level.block.state.BlockState placeState = lightBlock.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, pillarFacing.getOpposite())
                        .setValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL, pillarDiagonal)
                        .setValue(com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock.POSITION,
                                com.trafficcraft.horizontaltraffic.data.HorizontalLightPosition.CENTER)
                        .setValue(com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock.WATERLOGGED, waterlogged);

                level.setBlock(targetPos, placeState, net.minecraft.world.level.block.Block.UPDATE_ALL);
                net.minecraft.world.item.BlockItem.updateCustomBlockEntityTag(level, player, targetPos, stack);

                net.minecraft.world.level.block.SoundType soundType = placeState.getSoundType();
                level.playSound(null, targetPos, soundType.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));

                com.trafficcraft.horizontaltraffic.util.Rotation45Helper.updateDiagonalNeighbors(level, targetPos);
                com.trafficcraft.horizontaltraffic.util.Rotation45Helper.updateDiagonalNeighbors(level, pos);
            }

            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
        });
    }
}
