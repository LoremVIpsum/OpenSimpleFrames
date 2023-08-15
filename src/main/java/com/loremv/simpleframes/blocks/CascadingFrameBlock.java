package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CascadingFrameBlock extends Block {
    public CascadingFrameBlock() {
        super(AbstractBlock.Settings.create().strength(1.5f).nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }



    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    if(world.getBlockState(pos.add(-i,-j,-k)).contains(FrameBlockUtils.TEXTURE_ID))
                    {
                        FrameBlockUtils.updateAndSync(world, player, pos.add(-i,-j,-k), world.getBlockState(pos.add(-i,-j,-k)));
                        return super.onUse(state, world, pos, player, hand, hit);
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    if(world.getBlockState(pos.add(-i,-j,-k)).contains(FrameBlockUtils.TEXTURE_ID))
                    {
                        FrameBlockUtils.updateAndSync(world, player, pos.add(-i,-j,-k), world.getBlockState(pos.add(-i,-j,-k)));
                    }
                }
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    if(world.getBlockState(pos.add(-i, -j, -k)).getBlock() instanceof FrameBigBlock block)
                    {
                        block.doBreak(world,pos.add(-i,-j,-k));
                    }
                }
            }
        }
    }

}
