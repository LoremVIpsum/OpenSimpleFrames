package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrameCarpetBlock extends CarpetBlock {
    public FrameCarpetBlock() {
        super(AbstractBlock.Settings.create().strength(1.5f).nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("framed_carpet_block",this,"static"));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FrameBlockUtils.TEXTURE_ID,0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
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
