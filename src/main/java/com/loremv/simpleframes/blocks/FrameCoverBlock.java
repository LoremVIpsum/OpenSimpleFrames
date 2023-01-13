package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrameCoverBlock extends FrameBlock{
    private static final VoxelShape EAST = createCuboidShape(15.9f,0,0,16,16,16);
    private static final VoxelShape WEST = createCuboidShape(0,0,0,0.1f,16,16);
    private static final VoxelShape SOUTH = createCuboidShape(0,0,15.9f,16,16,16);
    private static final VoxelShape NORTH = createCuboidShape(0,0,0,16,16,0.1f);
    private static final VoxelShape DOWN = createCuboidShape(0,0,0,16,0.1f,16);

    private static final VoxelShape UP = createCuboidShape(0,15.9f,0,16,16,16);
    public static final DirectionProperty DIRECITON = DirectionProperty.of("direction");
    public FrameCoverBlock(String capture) {
        super(capture);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(DIRECITON);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(DIRECITON,ctx.getPlayerLookDirection());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.getHorizontalFacing().rotateYClockwise()==state.get(DIRECITON))
        {
            if(state.getBlock()==SimpleFrames.FRAME_COVER_BLOCK)
            {
                player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE,1,1);
                world.setBlockState(pos,SimpleFrames.FRAME_COVER_CORNER_BLOCK.getDefaultState().with(DIRECITON,state.get(DIRECITON)));
                return ActionResult.CONSUME;
            }
        }
        if(player.getHorizontalFacing().rotateYCounterclockwise()==state.get(DIRECITON))
        {
            if(state.getBlock()==SimpleFrames.FRAME_COVER_CORNER_BLOCK)
            {
                player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE,1,1);
                world.setBlockState(pos,SimpleFrames.FRAME_COVER_INSET_BLOCK.getDefaultState().with(DIRECITON,state.get(DIRECITON)));
                return ActionResult.CONSUME;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.getBlock()== SimpleFrames.FRAME_COVER_INSET_BLOCK)
        {
            return switch (state.get(DIRECITON))
                    {
                        case WEST -> VoxelShapes.union(WEST,NORTH,SOUTH);
                        case EAST -> VoxelShapes.union(EAST,SOUTH,NORTH);
                        case SOUTH -> VoxelShapes.union(SOUTH,EAST,WEST);
                        case NORTH -> VoxelShapes.union(NORTH,WEST,EAST);
                        case DOWN -> DOWN;
                        case UP -> UP;
                    };
        }
        if(state.getBlock()== SimpleFrames.FRAME_COVER_CORNER_BLOCK)
        {
            return switch (state.get(DIRECITON))
                    {
                        case NORTH -> VoxelShapes.union(NORTH,WEST);
                        case EAST -> VoxelShapes.union(EAST,NORTH);
                        case SOUTH -> VoxelShapes.union(SOUTH,EAST);
                        case WEST -> VoxelShapes.union(WEST,SOUTH);
                        case DOWN -> DOWN;
                        case UP -> UP;
                    };
        }
        return switch (state.get(DIRECITON))
                {
                    case DOWN -> DOWN;
                    case UP -> UP;
                    case NORTH -> NORTH;
                    case SOUTH -> SOUTH;
                    case WEST -> WEST;
                    case EAST -> EAST;
                };
    }
}
