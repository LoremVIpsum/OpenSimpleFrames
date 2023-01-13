package com.loremv.simpleframes.blocks;

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

public class FrameStairBlock extends StairsBlock {

    public FrameStairBlock() {
        super(Blocks.BARRIER.getDefaultState(), AbstractBlock.Settings.of(Material.WOOD).luminance(a->1).strength(1.5f));
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FrameBlockUtils.TEXTURE_ID);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FrameBlockUtils.TEXTURE_ID,0);

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

    //TODO: this method should probably exist elsewhere for blocks that use them same Blockstate data, but need different processing

    /**
     * this method is here so that we can register frames at runtime
     * ramps and stairs functionally the same, but require slightly different processing
     * @param captures a 2 long string array of {base name,processing method}
     * @return this block, so I don't have to do this on separate line in a method.
     */
    public FrameStairBlock thenRegister(String[]... captures)
    {
        for(String[] capture: captures)
        {
            CapturedBlockStorage.REGISTRY.add(new BlockCapture(capture[0],this,capture[1]));
        }

        return this;
    }
}
