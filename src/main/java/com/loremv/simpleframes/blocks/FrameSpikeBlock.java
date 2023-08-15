package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrameSpikeBlock extends Block {


    public FrameSpikeBlock() {
        super(AbstractBlock.Settings.create().strength(1.5f).nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("cobblestone",this, "static-idea").withPromisedIdea("SPIKE4"));
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

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.handleFallDamage(fallDistance*2, 1.0f, world.getDamageSources().fall());
    }
}
