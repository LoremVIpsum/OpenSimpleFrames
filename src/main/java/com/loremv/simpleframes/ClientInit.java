package com.loremv.simpleframes;

import com.loremv.simpleframes.utility.FakeryBakery;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
                states.add(NbtHelper.toBlockState(state));
            }
            if(shouldPlace)
            {
                BlockPos newPos = byteBuf.readBlockPos();
                NbtCompound newState = byteBuf.readNbt();
                NbtCompound invalidatedState = byteBuf.readNbt();
                client.execute(()-> {
                    client.world.setBlockState(newPos,NbtHelper.toBlockState(newState));
                    if(invalidatedState!=null)
                    {
                        FakeryBakery.BLOCK_CACHE.remove(NbtHelper.toBlockState(invalidatedState));
                    }

                });
            }

            client.execute(()->
            {
                FrameBlockUtils.USED_STATES = states;
                if(!shouldPlace)
                {
                    FakeryBakery.BLOCK_CACHE.clear();
                }
            });
        });
    }
}
