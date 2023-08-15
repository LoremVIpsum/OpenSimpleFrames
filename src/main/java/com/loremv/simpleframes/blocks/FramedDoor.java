package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FramedDoor extends Block {
    VoxelShape NORTH = Block.createCuboidShape(0,0,0,16,32,2);
    VoxelShape EAST = Block.createCuboidShape(0,0,0,2,32,16);
    VoxelShape WEST = Block.createCuboidShape(14,0,0,16,32,16);
    VoxelShape SOUTH = Block.createCuboidShape(0,0,14,16,32,16);
    public FramedDoor() {
        super(AbstractBlock.Settings.create().strength(1.5f).nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(DoorBlock.OPEN,false).with(DoorBlock.FACING, Direction.EAST).with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_door",this, "door"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_door_open",this, "door"));
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.get(DoorBlock.OPEN))
        {
            return switch (state.get(DoorBlock.FACING))
            {
                default -> null;
                case NORTH -> EAST;
                case SOUTH -> WEST;
                case WEST -> SOUTH;
                case EAST -> NORTH;
            };
        }
        else
        {
            return switch (state.get(DoorBlock.FACING))
                    {
                        default -> null;
                        case NORTH -> NORTH;
                        case SOUTH -> SOUTH;
                        case WEST -> EAST;
                        case EAST -> WEST;
                    };
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FrameBlockUtils.TEXTURE_ID);
        builder.add(DoorBlock.OPEN);
        builder.add(DoorBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        System.out.println(ctx.getVerticalPlayerLookDirection());
        return getDefaultState().with(DoorBlock.FACING,ctx.getHorizontalPlayerFacing());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.isSneaky())
        {
            FrameBlockUtils.updateAndSync(world, player, pos, state);
            return ActionResult.CONSUME;
        }
        if(hand==Hand.MAIN_HAND)
        {
            if(state.get(DoorBlock.OPEN))
            {
                world.setBlockState(pos,state.with(DoorBlock.OPEN,false));
                player.playSound(SoundEvents.BLOCK_WOODEN_DOOR_OPEN,1,1);
            }
            else
            {
                world.setBlockState(pos,state.with(DoorBlock.OPEN,true));
                player.playSound(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,1,1);
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        FrameBlockUtils.updateAndSync(world, player, pos, state);
    }
}
