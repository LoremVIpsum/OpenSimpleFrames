package com.loremv.simpleframes.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FramePanelBlock extends FrameBlock {
    private static final VoxelShape[] BLOCK_SHAPES = {
            /* DOWN */    VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
            /* UP */  VoxelShapes.cuboid(0.0, 0.75, 0.0, 1.0, 1.0, 1.0),
            /* NORTH */ VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 0.25),
            /* SOUTH */ VoxelShapes.cuboid(0.0, 0.0, 0.75, 1.0, 1.0, 1.0),
            /* WEST */  VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.25, 1.0, 1.0),
            /* EAST */  VoxelShapes.cuboid(0.75, 0.0, 0.0, 1.0, 1.0, 1.0),
    };

    public FramePanelBlock(final String capture) {
        super(capture);
    }

    @Override
    protected void appendProperties(final StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FacingBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FacingBlock.FACING,ctx.getPlayerLookDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context) {
        return FramePanelBlock.BLOCK_SHAPES[state.get(FacingBlock.FACING).getId()];
    }
}
