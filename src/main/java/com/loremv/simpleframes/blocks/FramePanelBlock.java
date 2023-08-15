package com.loremv.simpleframes.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FramePanelBlock extends FrameBlock{
    public FramePanelBlock(String capture) {
        super(capture);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(StairsBlock.FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(StairsBlock.FACING,ctx.getPlayerLookDirection());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(StairsBlock.FACING))
        {
            default -> super.getOutlineShape(state,world,pos,context);
            case NORTH -> createCuboidShape(0,0,0,16,16,4);
            case SOUTH -> createCuboidShape(0,0,12,16,16,16);
            case WEST -> createCuboidShape(0,0,0,4,16,16);
            case EAST -> createCuboidShape(12,0,0,16,16,16);
        };
    }
}
