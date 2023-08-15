package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FrameFenceBlock extends FenceBlock{

    public FrameFenceBlock() {
        super(AbstractBlock.Settings.of(Material.WOOD).strength(1.5f).nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_post",this, "fence"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_n",this, "fence"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_ns",this, "fence"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_ne",this, "fence"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_new",this, "fence"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_fence_news",this, "fence"));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FrameBlockUtils.TEXTURE_ID);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        super.onUse(state, world, pos, player, hand, hit);
        FrameBlockUtils.updateAndSync(world, player, pos, state);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        return neighborIsFullSquare || state.getBlock()== SimpleFrames.FRAME_FENCE_BLOCK;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        FrameBlockUtils.updateAndSync(world, player, pos, state);
    }
}
