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

            // Vertical TrafficLightBlock: shift + right click with wrench rotates 45 degrees
            if (block instanceof de.mrjulsen.trafficcraft.block.TrafficLightBlock) {
                if (player.isShiftKeyDown()) {
                    if (!level.isClientSide()) {
                        net.minecraft.core.Direction facing = state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
                        boolean diag = state.hasProperty(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL)
                                && state.getValue(com.trafficcraft.horizontaltraffic.util.Rotation45Helper.DIAGONAL);
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
}
