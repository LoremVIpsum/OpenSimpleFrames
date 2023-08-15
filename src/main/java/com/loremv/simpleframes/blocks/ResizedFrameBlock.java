package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ResizedFrameBlock extends FrameBlock{
    VoxelShape shape;
    public ResizedFrameBlock(String capture, VoxelShape shape) {
        super(capture);
        this.shape=shape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shape;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(state.getBlock()== SimpleFrames.FRAME_BIG_BLOCK)
        {
            world.setBlockState(pos.add(1,0,0), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(1,1,0), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(1,1,1), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(0,1,1), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(0,1,0), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(0,0,1), Blocks.BARRIER.getDefaultState());
            world.setBlockState(pos.add(1,0,1), Blocks.BARRIER.getDefaultState());
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        if(state.getBlock()== SimpleFrames.FRAME_BIG_BLOCK)
        {
            world.setBlockState(pos.add(1,0,0), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(1,1,0), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(1,1,1), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(0,1,1), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(0,1,0), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(0,0,1), Blocks.AIR.getDefaultState(),3);
            world.setBlockState(pos.add(1,0,1), Blocks.AIR.getDefaultState(),3);
        }
    }
}
