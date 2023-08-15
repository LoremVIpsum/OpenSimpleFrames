package com.loremv.simpleframes.blocks;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FramedChest extends ChestBlock {
    public FramedChest() {
        super(AbstractBlock.Settings.of(Material.WOOD).strength(1.5f).nonOpaque(),()->BlockEntityType.CHEST);
        setDefaultState(getStateManager().getDefaultState().with(FrameBlockUtils.TEXTURE_ID,0));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_chest",this, "chest"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_chest_left",this, "chest"));
        SimpleFrames.STORAGE.REGISTRY.add(new BlockCapture("block/framed_chest_right",this, "chest"));
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FrameBlockUtils.TEXTURE_ID);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
        FrameBlockUtils.updateAndSync(world, player, pos, state);
    }


}
