package com.loremv.simpleframes.utility;

import com.loremv.simpleframes.SimpleFrames;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FrameBlockUtils {
    //a blockstate property to be used by any framed block
    public static final IntProperty TEXTURE_ID = IntProperty.of("texture_id",0,100);
    //the list of blockstates currently being used as textures, this is global for all framed blocks
    public static List<BlockState> USED_STATES = new ArrayList<>();

    //the identifier used to store the states in command storage on the server
    public static final Identifier USED_STATES_STORAGE = new Identifier("simpleframes","used_states_storage");

    /**
     * intended to be called from an interactive method, since we do checking of server-sidedness and the players item.
     * (for instance, a blocks onUse and onStartBreaking)
     * no return value but its side effects can be summed up:
     * - retrieve all states from the server, or set the compound up
     * - set the block on the server side to be the reflected state
     * - save any new framed blocks to the server
     * - send this information to the client
     * - have the client set the block and update USED_STATES so that we can fake the block model
     *
     * @param world the world the framed block is in
     * @param player the player who has a hand that likely contains a block to put on the fram
     * @param pos the position of the framed block
     * @param state the current state of the framed block
     */
    public static void updateAndSync(World world, PlayerEntity player, Item item, BlockPos pos, BlockState state)
    {
        BlockItem blockItem;
        if(item instanceof BlockItem)
        {
            blockItem = (BlockItem) item;
        }
        else
        {
            blockItem = (BlockItem) SimpleFrames.FRAME_BASE.asItem();
        }
        //TODO: be cool to play a sound if this happens
        //player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE,1,1);
        if(!world.isClient)
        {

            if(USED_STATES.size()==0)
            {
                //the server doesn't have any stored states, so we add the basics
                if(world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE)==null || !world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE).contains("states"))
                {
                    //add default state, we cheat by registering a full block "frame block" that doesn't have model modifications
                    USED_STATES.add(SimpleFrames.FRAME_BASE.getDefaultState());
                    //then create the compound, we don't have to assign it, that is done as collateral later.
                    world.getServer().getDataCommandStorage().set(USED_STATES_STORAGE,new NbtCompound());
                }
                else
                {
                    //the server has stored states, decode them.
                    USED_STATES=new ArrayList<>();
                    NbtCompound compound = world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE);
                    if(compound.contains("states"))
                    {
                        for(NbtElement element: (NbtList)compound.get("states"))
                        {
                            NbtCompound s = (NbtCompound) element;
                            USED_STATES.add(NbtHelper.toBlockState(s));
                        }
                    }


                }


            }
            NbtCompound newState;
            NbtCompound invalidatedState = null;
            BlockState current = world.getBlockState(pos);

            //if the frame that was clicked's current texture_id is the same as the one expected of the item, return
            if(current.get(FrameBlockUtils.TEXTURE_ID)==USED_STATES.indexOf(blockItem.getBlock().getDefaultState()))
            {
                return;
            }
            //if USED_STATES already has an entry for the given Block item, then use that, otherwise, add an entry
            if(USED_STATES.contains(blockItem.getBlock().getDefaultState()))
            {
                NbtCompound compound = world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE);
                NbtCompound analysis = compound.getCompound("analysis");
                int index = USED_STATES.indexOf(blockItem.getBlock().getDefaultState());
                //if index = 0, either this block was just placed, or is in the process of being broken
                if(index==0)
                {

                    //if this block wasn't just placed
                    if(current.get(FrameBlockUtils.TEXTURE_ID)!=0)
                    {
                        NbtList unusedStates;
                        if(!compound.contains("unusedStates"))
                        {
                            unusedStates=new NbtList();
                        }
                        else
                        {
                            unusedStates = (NbtList) compound.get("unusedStates");
                        }
                        int oldTextureId = current.get(FrameBlockUtils.TEXTURE_ID);
                        int oldAmount = analysis.getInt(""+oldTextureId);
                        analysis.putInt(""+oldTextureId,oldAmount-1);
                        if(oldAmount-1<=0)
                        {
                            unusedStates.add(NbtInt.of(oldTextureId));
                            compound.put("unusedStates",unusedStates);
                        }
                    }
                }


                world.setBlockState(pos,state.with(TEXTURE_ID,index));
                newState = NbtHelper.fromBlockState(state.with(TEXTURE_ID,index));


                analysis.putInt(""+index,analysis.getInt(""+index)+1);
                compound.put("analysis",analysis);

                world.getServer().getDataCommandStorage().set(USED_STATES_STORAGE,compound);
            }
            else
            {
                if(!blockItem.getBlock().getDefaultState().contains(FrameBlockUtils.TEXTURE_ID))
                {
                    //where we will put the new texture
                    int index = -1;

                    NbtCompound compound = world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE);
                    NbtCompound analysis;
                    if(!compound.contains("analysis"))
                    {
                        analysis=new NbtCompound();
                    }
                    else
                    {
                        analysis = compound.getCompound("analysis");
                    }
                    NbtList unusedStates;
                    if(!compound.contains("unusedStates"))
                    {
                        unusedStates=new NbtList();
                    }
                    else
                    {
                        unusedStates = (NbtList) compound.get("unusedStates");
                    }

                    //reduce the world count of the old texture by 1.
                    int oldTextureId = current.get(FrameBlockUtils.TEXTURE_ID);
                    if(unusedStates.size()>0)
                    {
                        index = unusedStates.getInt(0);
                        unusedStates.remove(0);
                        compound.put("unusedStates",unusedStates);

                        invalidatedState=NbtHelper.fromBlockState(USED_STATES.get(index));
                    }
                    else if(analysis.contains(""+oldTextureId))
                    {
                        int oldAmount = analysis.getInt(""+oldTextureId);
                        analysis.putInt(""+oldTextureId,oldAmount-1);
                        if(oldAmount-1==0)
                        {
                            //if oldAmount would become 0 as of this transaction, put the new texture at OldTextureId
                            //then make it 1 instead.
                            index = oldTextureId;
                            analysis.putInt(""+oldTextureId,1);
                            invalidatedState=NbtHelper.fromBlockState(current);
                        }

                    }



                    if(index==-1)
                    {
                        USED_STATES.add(blockItem.getBlock().getDefaultState());
                        analysis.putInt(""+(USED_STATES.size()-1),1);
                        index=USED_STATES.size()-1;
                    }
                    else
                    {
                        USED_STATES.set(index,blockItem.getBlock().getDefaultState());


                    }

                    compound.put("analysis",analysis);
                    world.getServer().getDataCommandStorage().set(USED_STATES_STORAGE,compound);

                    world.setBlockState(pos,state.with(TEXTURE_ID,index));
                    newState = NbtHelper.fromBlockState(state.with(TEXTURE_ID,index));

                }
                else
                {
                    //just ignore the whole process if the texture would be a frame
                    return;
                }

            }


            NbtCompound compound = world.getServer().getDataCommandStorage().get(USED_STATES_STORAGE);
            NbtList list = new NbtList();

            for(BlockState saved: USED_STATES)
            {
                list.add(NbtHelper.fromBlockState(saved));
            }
            compound.put("states",list);

            //here's that collateral, since we encode the states into a compound anyway
            world.getServer().getDataCommandStorage().set(USED_STATES_STORAGE,compound);


            //we send the block that just updated on the server (in the form of its state and a pos) to the client
            //we also send an encoded USED_STATES, as seen above
            PacketByteBuf byteBuf = PacketByteBufs.create();
            byteBuf.writeBoolean(true);
            byteBuf.writeNbt(compound);
            byteBuf.writeBlockPos(pos);
            byteBuf.writeNbt(newState);
            byteBuf.writeNbt(invalidatedState);
            ServerPlayNetworking.send((ServerPlayerEntity) player,SimpleFrames.SYNC_TEXTURE_PACKET,byteBuf);

        }
    }
    public static void updateAndSync(World world, PlayerEntity player, BlockPos pos, BlockState state)
    {
        updateAndSync(world,player,player.getMainHandStack().getItem(),pos,state);
    }

    public static void forceUpdate(ServerPlayerEntity player)
    {
        NbtCompound compound = player.server.getDataCommandStorage().get(FrameBlockUtils.USED_STATES_STORAGE);
        if(compound==null || !compound.contains("states"))
        {
            compound=new NbtCompound();
            NbtList list = new NbtList();
            list.add(NbtHelper.fromBlockState(SimpleFrames.FRAME_BASE.getDefaultState()));
            compound.put("states",list);
            player.server.getDataCommandStorage().set(FrameBlockUtils.USED_STATES_STORAGE,compound);
        }
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeBoolean(false);
        packetByteBuf.writeNbt(compound);
        ServerPlayNetworking.send(player,SimpleFrames.SYNC_TEXTURE_PACKET,packetByteBuf);
        SimpleFrames.LOGGER.debug("updated frames on client");
    }
}
