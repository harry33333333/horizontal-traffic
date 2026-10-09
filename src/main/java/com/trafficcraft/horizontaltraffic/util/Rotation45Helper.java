package com.trafficcraft.horizontaltraffic.util;

import de.mrjulsen.trafficcraft.item.WrenchItem;
import de.mrjulsen.trafficcraft.registry.ModTags;
import io.github.aftersans53228.aft_fabroads.block.pillarblock.SmartPillar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.LinkedHashMap;
import java.util.Map;

public class Rotation45Helper {

    public static final BooleanProperty DIAGONAL = BooleanProperty.create("diagonal");

    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("north_east");
    public static final BooleanProperty SOUTH_EAST = BooleanProperty.create("south_east");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("south_west");
    public static final BooleanProperty NORTH_WEST = BooleanProperty.create("north_west");

    public record DirectionState(Direction facing, boolean diagonal) {}

    /**
     * Converts player Y rotation (yaw) to the 8-directional (facing, diagonal) pair.
     * In Minecraft, blocks typically face towards the player (opposite to player look direction).
     */
    public static DirectionState getPlacementDirection(float playerYaw) {
        float targetYaw = (playerYaw + 180.0f) % 360.0f;
        if (targetYaw < 0) targetYaw += 360.0f;
        int sector = Math.round(targetYaw / 45.0f) % 8;
        return fromSector(sector);
    }

    /**
     * Converts a sector index (0..7, where 0 is 0° South, 1 is 45° SW, etc.) to (facing, diagonal).
     */
    public static DirectionState fromSector(int sector) {
        return switch (sector) {
            case 0 -> new DirectionState(Direction.SOUTH, false); // 0° (South)
            case 1 -> new DirectionState(Direction.SOUTH, true);  // 45° (South-West)
            case 2 -> new DirectionState(Direction.WEST, false);  // 90° (West)
            case 3 -> new DirectionState(Direction.WEST, true);   // 135° (North-West)
            case 4 -> new DirectionState(Direction.NORTH, false); // 180° (North)
            case 5 -> new DirectionState(Direction.NORTH, true);  // 225° (North-East)
            case 6 -> new DirectionState(Direction.EAST, false);  // 270° (East)
            case 7 -> new DirectionState(Direction.EAST, true);   // 315° (South-East)
            default -> new DirectionState(Direction.NORTH, false);
        };
    }

    /**
     * Converts a (facing, diagonal) pair back to its 0..7 sector index.
     */
    public static int toSector(Direction facing, boolean diagonal) {
        return switch (facing) {
            case SOUTH -> diagonal ? 1 : 0;
            case WEST -> diagonal ? 3 : 2;
            case NORTH -> diagonal ? 5 : 4;
            case EAST -> diagonal ? 7 : 6;
            default -> 0;
        };
    }

    /**
     * Calculates the rotation angle in degrees (0..360) for a given (facing, diagonal) state.
     */
    public static float getAngleDegrees(Direction facing, boolean diagonal) {
        float base = facing.toYRot();
        return diagonal ? (base + 45.0f) % 360.0f : base;
    }

    /**
     * Cycles to the next 45-degree sector (clockwise).
     */
    public static DirectionState getNextDirection(Direction facing, boolean diagonal) {
        int currentSector = toSector(facing, diagonal);
        int nextSector = (currentSector + 1) % 8;
        return fromSector(nextSector);
    }

    public static Component getDirectionName(DirectionState ds) {
        int sector = toSector(ds.facing(), ds.diagonal());
        String key = switch (sector) {
            case 0 -> "direction.horizontal_traffic.south";
            case 1 -> "direction.horizontal_traffic.south_west";
            case 2 -> "direction.horizontal_traffic.west";
            case 3 -> "direction.horizontal_traffic.north_west";
            case 4 -> "direction.horizontal_traffic.north";
            case 5 -> "direction.horizontal_traffic.north_east";
            case 6 -> "direction.horizontal_traffic.east";
            case 7 -> "direction.horizontal_traffic.south_east";
            default -> "direction.horizontal_traffic.north";
        };
        return Component.translatable(key);
    }

    public static boolean hasCustomProperties(BlockState state) {
        if (state == null) return false;
        net.minecraft.world.level.block.Block block = state.getBlock();
        if (block instanceof com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock) {
            return state.hasProperty(DIAGONAL);
        }
        if (block.getClass().getName().startsWith("io.github.aftersans53228.aft_fabroads.")) {
            return state.hasProperty(DIAGONAL)
                    || state.hasProperty(NORTH_EAST)
                    || state.hasProperty(SOUTH_EAST)
                    || state.hasProperty(SOUTH_WEST)
                    || state.hasProperty(NORTH_WEST);
        }
        return false;
    }

    public static Map<Property<?>, Comparable<?>> filterProperties(BlockState state) {
        Map<Property<?>, Comparable<?>> map = new LinkedHashMap<>(state.getValues());
        map.remove(DIAGONAL);
        map.remove(NORTH_EAST);
        map.remove(SOUTH_EAST);
        map.remove(SOUTH_WEST);
        map.remove(NORTH_WEST);
        return map;
    }

    /**
     * Checks if the item stack is a wrench.
     */
    public static boolean isWrench(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.is(ModTags.WRENCHES)) return true;
        if (stack.getItem() instanceof WrenchItem) return true;
        String name = stack.getItem().getDescriptionId().toLowerCase();
        return name.contains("wrench");
    }

    /**
     * Checks if a block at pos can connect to a pillar (e.g. another pillar or traffic light).
     */
    public static boolean isConnectablePillar(BlockGetter world, BlockPos pos) {
        if (world == null || pos == null) return false;
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        try {
            if (io.github.aftersans53228.aft_fabroads.AFRoadsStatics.PILLAR_BLOCKS.contains(block)) {
                return true;
            }
            if (io.github.aftersans53228.aft_fabroads.AFRoadsStatics.CAN_PILLAR_CONNECT.contains(block)) {
                return true;
            }
            if (block instanceof SmartPillar) {
                return true;
            }
            if (block instanceof de.mrjulsen.trafficcraft.block.TrafficLightBlock) {
                return true;
            }
        } catch (Throwable ignored) {}

        return false;
    }

    /**
     * Checks if a SmartPillar can connect diagonally to the neighbor at pos.
     */
    public static boolean canSmartPillarConnectDiagonal(BlockGetter world, BlockPos pos) {
        if (world == null || pos == null) return false;
        BlockState state = world.getBlockState(pos);
        if (!isConnectablePillar(world, pos)) return false;
        if (state.hasProperty(DIAGONAL)) {
            return state.getValue(DIAGONAL);
        }
        return true; // SmartPillar, VerticalStraightPillar, etc.
    }

    /**
     * Notifies all diagonal SmartPillars surrounding pos to refresh their diagonal connection arms.
     */
    public static void updateDiagonalNeighbors(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        BlockPos[] diags = new BlockPos[] {
                pos.north().east(),
                pos.south().east(),
                pos.south().west(),
                pos.north().west()
        };
        for (BlockPos dPos : diags) {
            BlockState dState = level.getBlockState(dPos);
            if (dState.getBlock() instanceof SmartPillar) {
                BlockState updated = dState
                        .setValue(NORTH_EAST, canSmartPillarConnectDiagonal(level, dPos.north().east()))
                        .setValue(SOUTH_EAST, canSmartPillarConnectDiagonal(level, dPos.south().east()))
                        .setValue(SOUTH_WEST, canSmartPillarConnectDiagonal(level, dPos.south().west()))
                        .setValue(NORTH_WEST, canSmartPillarConnectDiagonal(level, dPos.north().west()));
                if (!updated.equals(dState)) {
                    level.setBlock(dPos, updated, Block.UPDATE_ALL);
                }
            }
        }
    }

    /**
     * Rotates any VoxelShape around the vertical axis by 45 degrees.
     */
    public static VoxelShape rotateShape45(VoxelShape shape) {
        VoxelShape[] result = new VoxelShape[]{Shapes.empty()};
        double rad = Math.toRadians(45.0);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double[][] corners = new double[][] {
                    {minX - 0.5, minZ - 0.5},
                    {minX - 0.5, maxZ - 0.5},
                    {maxX - 0.5, minZ - 0.5},
                    {maxX - 0.5, maxZ - 0.5}
            };
            double rMinX = Double.POSITIVE_INFINITY, rMaxX = Double.NEGATIVE_INFINITY;
            double rMinZ = Double.POSITIVE_INFINITY, rMaxZ = Double.NEGATIVE_INFINITY;
            for (double[] c : corners) {
                double nx = 0.5 + c[0] * cos - c[1] * sin;
                double nz = 0.5 + c[0] * sin + c[1] * cos;
                rMinX = Math.min(rMinX, nx);
                rMaxX = Math.max(rMaxX, nx);
                rMinZ = Math.min(rMinZ, nz);
                rMaxZ = Math.max(rMaxZ, nz);
            }
            rMinX = Math.max(0.0, Math.min(1.0, rMinX));
            rMaxX = Math.max(0.0, Math.min(1.0, rMaxX));
            rMinZ = Math.max(0.0, Math.min(1.0, rMinZ));
            rMaxZ = Math.max(0.0, Math.min(1.0, rMaxZ));
            result[0] = Shapes.or(result[0], Shapes.box(rMinX, minY, rMinZ, rMaxX, maxY, rMaxZ));
        });
        return result[0];
    }
}
