package com.loremv.simpleframes.mixin;

import com.loremv.simpleframes.SimpleFrames;
import com.loremv.simpleframes.utility.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * in this class we tell minecraft we don't like its models, and substitute our own
 */
@Mixin(BlockModels.class)
public class BlockModelsMixin {


    @Inject(at = @At("RETURN"), method = "getModel", cancellable = true)
    private void getModel(BlockState state, CallbackInfoReturnable<BakedModel> cir)
    {
        //NON_JSON_FRAME_BLOCK is special, its serves as a reminder to how this use to work and also to do quad modeling with
        if(state.getBlock()== SimpleFrames.NON_JSON_FRAME_BLOCK)
        {
            if(FrameBlockUtils.USED_STATES.size() > state.get(FrameBlockUtils.TEXTURE_ID))
            {
                cir.setReturnValue(FakeryBakery.createFakeStaticBlock((BasicBakedModel) cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID)), CapturedBlockStorage.NON_JSON, ModelIdeas.CUBE));
            }
        }

        if(state.contains(FrameBlockUtils.TEXTURE_ID) && FrameBlockUtils.USED_STATES.size() > state.get(FrameBlockUtils.TEXTURE_ID))
        {
            for(BlockCapture capture: CapturedBlockStorage.REGISTRY)
            {
                if(state.getBlock()==capture.of())
                {
                    switch (capture.getMethod())
                    {
                        case "static" -> cir.setReturnValue(FakeryBakery.createFakeStaticBlock(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID)), capture.getCapture()));
                        case "static-idea" -> cir.setReturnValue(FakeryBakery.createFakeStaticBlock(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID)), capture.getCapture(),ModelIdeas.IDEAS.get(capture.getIdea())));
                        case "ramp" -> cir.setReturnValue(FakeryBakery.createFakeRamp(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));
                        case "stair" -> cir.setReturnValue(FakeryBakery.createFakeStair(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));
                        case "slab" ->  cir.setReturnValue(FakeryBakery.createFakeSlab(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));
                        case "chest" -> cir.setReturnValue(FakeryBakery.createFakeChest(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));
                        case "fence" -> cir.setReturnValue(FakeryBakery.createFakeFence(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));
                        case "door" -> cir.setReturnValue(FakeryBakery.createFakeDoor(cir.getReturnValue(),state, FrameBlockUtils.USED_STATES.get(state.get(FrameBlockUtils.TEXTURE_ID))));

                    }
                }

            }
        }
    }
}
