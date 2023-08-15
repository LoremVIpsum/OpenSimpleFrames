package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrameBigBlock extends Block{
    int factor;
    public FrameBigBlock(int factor,String idea) {
        super(AbstractBlock.Settings.of(Material.AMETHYST).nonOpaque().strength(1.5f));
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_big_block",this, "static-idea").withPromisedIdea(idea));
        this.factor=factor;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0,0,0,factor*16,factor*16,factor*16);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (int i = 0; i < factor; i++) {
            for (int j = 0; j < factor; j++) {
                for (int k = 0; k < factor; k++) {
                    if(i==0 && j==0 && k==0)
                    {
                        continue;
                    }
                    world.setBlockState(pos.add(i,j,k), SimpleFrames.CASCADING_FRAME_BLOCK.getDefaultState());
                }
            }
        }
    }
    public void doBreak(World world, BlockPos pos)
    {
        for (int i = 0; i < factor; i++) {
            for (int j = 0; j < factor; j++) {
                for (int k = 0; k < factor; k++) {
                    if(i==0 && j==0 && k==0)
                    {
                        continue;
                    }
                    world.setBlockState(pos.add(i,j,k), Blocks.AIR.getDefaultState(),3);
                }
            }
        }
        world.breakBlock(pos,true);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        doBreak(world, pos);

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FrameBlockUtils.TEXTURE_ID);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        FrameBlockUtils.updateAndSync(world, player, pos, state);
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        FrameBlockUtils.updateAndSync(world, player, pos, state);
    }
}
