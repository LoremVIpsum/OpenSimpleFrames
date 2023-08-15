package com.loremv.simpleframes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FramePartitionBlock extends FrameBlock{

    public static final BooleanProperty ON_X = BooleanProperty.of("on_x");

    public FramePartitionBlock(String capture) {
        super(capture);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.get(ON_X))
        {
            return createCuboidShape(6,0,0,10,16,16);

        }
        return createCuboidShape(0,0,6,16,16,10);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ON_X);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(ON_X,ctx.getPlayerLookDirection()== Direction.EAST||ctx.getPlayerLookDirection()== Direction.WEST);
    }
}
