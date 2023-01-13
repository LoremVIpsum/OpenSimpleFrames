package com.loremv.simpleframes.mixin;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * when a player connects we should send them the data on the server
 * gladly, this means we can populate USED_STATES on the client before any models are loaded
 */
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void onConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
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
        SimpleFrames.LOGGER.info("updated frames on client");
    }
}
