package com.trafficcraft.horizontaltraffic.block;

import com.trafficcraft.horizontaltraffic.data.HorizontalLightPosition;
import com.trafficcraft.horizontaltraffic.init.ModBlockEntities;
import de.mrjulsen.trafficcraft.block.TrafficLightBlock;
import de.mrjulsen.trafficcraft.block.data.TrafficLightModel;
import de.mrjulsen.trafficcraft.client.ClientWrapper;
import de.mrjulsen.mcdragonlib.data.WorldLocation;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightControllerBlockEntity;
import de.mrjulsen.trafficcraft.item.BrushItem;
import de.mrjulsen.trafficcraft.item.TrafficLightLinkerItem;
import de.mrjulsen.trafficcraft.item.WrenchItem;
import de.mrjulsen.trafficcraft.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class HorizontalTrafficLightBlock extends TrafficLightBlock {

    public static final EnumProperty<HorizontalLightPosition> POSITION =
            EnumProperty.create("position", HorizontalLightPosition.class);

    // Bounding box map: [Model][Position][Facing] -> VoxelShape
    private static final Map<TrafficLightModel, Map<HorizontalLightPosition, Map<Direction, VoxelShape>>> SHAPES =
            new EnumMap<>(TrafficLightModel.class);

    private final TrafficLightModel defaultModel;

    static {
        VoxelShape horizontalPole = Block.box(0.0, 7.0, 7.0, 16.0, 9.0, 9.0);

        for (TrafficLightModel model : TrafficLightModel.values()) {
            Map<HorizontalLightPosition, Map<Direction, VoxelShape>> posMap = new EnumMap<>(HorizontalLightPosition.class);
            for (HorizontalLightPosition pos : HorizontalLightPosition.values()) {
                Map<Direction, VoxelShape> dirMap = new EnumMap<>(Direction.class);

                float minY = pos.getHitboxMinY();
                float maxY = pos.getHitboxMaxY();

                VoxelShape body;
                if (model == TrafficLightModel.THREE_LIGHTS) {
                    body = Block.box(0.5, minY, 0.5, 15.5, maxY, 5.0);
                } else if (model == TrafficLightModel.TWO_LIGHTS) {
                    body = Block.box(3.0, minY, 0.5, 13.0, maxY, 5.0);
                } else {
                    body = Block.box(4.0, minY, 0.5, 12.0, maxY, 5.0);
                }

                VoxelShape bracket;
                if (pos == HorizontalLightPosition.BOTTOM) {
                    bracket = Block.box(6.0, 6.0, 5.0, 10.0, 9.0, 7.0);
                } else if (pos == HorizontalLightPosition.TOP) {
                    bracket = Block.box(6.0, 7.0, 5.0, 10.0, 10.0, 7.0);
                } else {
                    bracket = Block.box(6.0, 7.0, 5.0, 10.0, 9.0, 7.0);
                }

                VoxelShape baseNorthShape = Shapes.or(horizontalPole, body, bracket);

                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    VoxelShape rotated = rotateShape(dir, baseNorthShape);
                    dirMap.put(dir, rotated);
                }
                posMap.put(pos, dirMap);
            }
            SHAPES.put(model, posMap);
        }
    }

    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    public HorizontalTrafficLightBlock() {
        this(TrafficLightModel.THREE_LIGHTS);
    }

    public HorizontalTrafficLightBlock(TrafficLightModel defaultModel) {
        super();
        this.defaultModel = defaultModel;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODEL, defaultModel)
                .setValue(POSITION, HorizontalLightPosition.CENTER)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        TrafficLightModel model = pState.getValue(MODEL);
        HorizontalLightPosition pos = pState.getValue(POSITION);
        Direction facing = pState.getValue(FACING);

        Map<HorizontalLightPosition, Map<Direction, VoxelShape>> posMap = SHAPES.get(model);
        if (posMap != null) {
            Map<Direction, VoxelShape> dirMap = posMap.get(pos);
            if (dirMap != null) {
                VoxelShape shape = dirMap.get(facing);
                if (shape != null) {
                    return shape;
                }
            }
        }
        return rotateShape(facing, Block.box(0.0, 7.0, 7.0, 16.0, 9.0, 9.0));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        Direction facing = pContext.getHorizontalDirection().getOpposite();

        return this.defaultBlockState()
                .setValue(WATERLOGGED, flag)
                .setValue(FACING, facing)
                .setValue(MODEL, this.defaultModel)
                .setValue(POSITION, HorizontalLightPosition.CENTER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POSITION);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        Item item = stack.getItem();

        if (item instanceof BrushItem) {
            return InteractionResult.FAIL;
        }

        // Handle Traffic Light Linker
        if (!pPlayer.isShiftKeyDown() && item instanceof TrafficLightLinkerItem linker) {
            CompoundTag nbt = linker.doesContainValidLinkData(stack);
            if (nbt == null) {
                // Link as source
                if (!pLevel.isClientSide) {
                    CompoundTag compound = stack.getOrCreateTag();
                    compound.put(TrafficLightLinkerItem.NBT_LINK_TARGET, new WorldLocation(pPos.getX(), pPos.getY(), pPos.getZ(), pLevel.dimension().location()).toNbt());
                    compound.putString(TrafficLightLinkerItem.NBT_BLOCK, BuiltInRegistries.BLOCK.getKey(this).toString());
                    pPlayer.displayClientMessage(TextUtils.translate("item.trafficcraft.traffic_light_linker.use.set", pPos.toShortString(), pLevel.dimension().location()).withStyle(ChatFormatting.AQUA), true);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                if (pLevel.isClientSide) {
                    return InteractionResult.sidedSuccess(true);
                }

                WorldLocation linkLoc = WorldLocation.loadFromNbt(nbt.getCompound(TrafficLightLinkerItem.NBT_LINK_TARGET));
                TrafficLightLinkerItem.LinkerMode mode = TrafficLightLinkerItem.LinkerMode.getByIndex(nbt.getInt(TrafficLightLinkerItem.NBT_MODE));

                if (linkLoc.dimension != null && !pLevel.dimension().location().equals(linkLoc.dimension)) {
                    pPlayer.displayClientMessage(TextUtils.translate("item.trafficcraft.traffic_light_linker.use.wrong_dimension").withStyle(ChatFormatting.RED), true);
                    return InteractionResult.CONSUME;
                }

                BlockPos targetControllerPos = linkLoc.getLocationBlockPos();
                if (pLevel.isLoaded(targetControllerPos)) {
                    BlockEntity targetBE = pLevel.getBlockEntity(targetControllerPos);
                    if (targetBE instanceof TrafficLightControllerBlockEntity controllerBE) {
                        ResourceLocation dim = pLevel.dimension().location();
                        WorldLocation thisLightLoc = new WorldLocation(pPos.getX(), pPos.getY(), pPos.getZ(), dim);

                        switch (mode) {
                            case UNLINK -> {
                                controllerBE.removeTrafficLightLocation(thisLightLoc);
                                pPlayer.displayClientMessage(TextUtils.translate("item.trafficcraft.traffic_light_linker.use.unlink", targetControllerPos.toShortString(), dim).withStyle(ChatFormatting.RED), true);
                            }
                            case LINK -> {
                                controllerBE.addTrafficLightLocation(thisLightLoc);
                                pPlayer.displayClientMessage(TextUtils.translate("item.trafficcraft.traffic_light_linker.use.link", targetControllerPos.toShortString(), dim).withStyle(ChatFormatting.GREEN), true);
                            }
                        }
                        return InteractionResult.CONSUME;
                    }
                }

                pPlayer.displayClientMessage(TextUtils.translate("item.trafficcraft.traffic_light_linker.use.target_not_loaded").withStyle(ChatFormatting.RED), true);
                return InteractionResult.CONSUME;
            }
        }

        if (stack.is(ModTags.WRENCHES) || item instanceof WrenchItem) {
            if (pPlayer.isShiftKeyDown()) {
                if (!pLevel.isClientSide) {
                    HorizontalLightPosition currentPos = pState.getValue(POSITION);
                    HorizontalLightPosition nextPos = currentPos.next();
                    BlockState newState = pState.setValue(POSITION, nextPos);
                    pLevel.setBlock(pPos, newState, Block.UPDATE_ALL);
                    pLevel.playSound(null, pPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.4f, 1.4f);
                    pPlayer.displayClientMessage(
                            Component.translatable("message.horizontal_traffic.position_changed",
                                    Component.translatable("gui.horizontal_traffic.position." + nextPos.getSerializedName())),
                            true
                    );
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                if (pLevel.isClientSide) {
                    ClientWrapper.showTrafficLightConfigScreen(pLevel, pPos);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY.create(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY, HorizontalTrafficLightBlock::tickEntity);
    }

    private static void tickEntity(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        if (entity instanceof de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity tl) {
            de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity.tick(level, pos, state, tl);
        }
    }
}
