package com.loremv.simpleframes;

import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SimpleFrames.SYNC_TEXTURE_PACKET, (client, handler, byteBuf, responseHandler)->
        {

            boolean shouldPlace = byteBuf.readBoolean();
            NbtCompound compound = byteBuf.readNbt();

            NbtList list = (NbtList) compound.get("states");
            List<BlockState> states = new ArrayList<>();
            for(NbtElement element: list)
            {

                NbtCompound state = (NbtCompound) element;
                states.add(toBlockState(state));

            }
            if(shouldPlace)
            {
                BlockPos newPos = byteBuf.readBlockPos();
                NbtCompound newState = byteBuf.readNbt();
                NbtCompound invalidatedState = byteBuf.readNbt();
                client.execute(()-> {
                    client.world.setBlockState(newPos,toBlockState(newState));
                    if(invalidatedState!=null)
                    {
                        SimpleFrames.BAKERY.BLOCK_CACHE.remove(toBlockState(invalidatedState));
                    }

                });
            }

            client.execute(()->
            {
                FrameBlockUtils.USED_STATES = states;
                if(!shouldPlace)
                {
                    SimpleFrames.BAKERY.BLOCK_CACHE.clear();
                }
            });
        });

        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_STAIR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_BIG_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_CARPET_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_COVER_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAMED_CHEST, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAMED_DOOR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_FENCE_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_PANEL_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_PARTITION, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_SLAB_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_SPIKE_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_STAIR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_HUGE_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_RAMP_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_DOWN_EXTENDED, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_UP_EXTENDED, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_HFENCE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_COVER_INSET_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_COVER_CORNER_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_RAMP_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(SimpleFrames.FRAME_BLOCK, RenderLayer.getCutoutMipped());
    }


    //this is copied and fixed from the 1.19 version, I have no idea how the 1.20 version works.
    public static BlockState toBlockState(NbtCompound nbt) {
        if (!nbt.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        } else {
            Block block = (Block) Registries.BLOCK.get(new Identifier(nbt.getString("Name")));
            BlockState blockState = block.getDefaultState();
            if (nbt.contains("Properties", 10)) {
                NbtCompound nbtCompound = nbt.getCompound("Properties");
                StateManager<Block, BlockState> stateManager = block.getStateManager();
                Iterator var5 = nbtCompound.getKeys().iterator();

                while(var5.hasNext()) {
                    String string = (String)var5.next();
                    net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                    if (property != null) {
                        blockState = withProperty(blockState, property, string, nbtCompound, nbt);

                    }
                }
            }

            return blockState;
        }
    }
    //same as above.
    private static  BlockState withProperty(State state, Property property, String key, NbtCompound properties, NbtCompound root) {
        Optional<Property> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return (BlockState) state.with(property, (Comparable)optional.get());
        } else {
            SimpleFrames.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{key, properties.getString(key), root.toString()});
            return (BlockState) state;
        }
    }
}
