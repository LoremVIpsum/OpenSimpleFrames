package com.loremv.simpleframes.mixin;

import com.loremv.simpleframes.utility.BlockCapture;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.QuadIngredients;
import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * making block models is hard, let's let minecraft do it for us
 * here we capture block models as they are assigned, to be used to fake models later.
 */
@Mixin(BakedQuadFactory.class)
public class BakedQuadFactoryMixin {


    @Inject(at = @At("HEAD"), method = "bake")
    private void baking(Vec3f from, Vec3f _to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, ModelRotation rotation, boolean shade, Identifier modelId, CallbackInfoReturnable<BakedQuad> cir)
    {
        capture(SimpleFrames.STORAGE.NON_JSON,"cobblestone",from, _to, face, texture, side, settings, rotation, shade, modelId);
        for(BlockCapture capture: SimpleFrames.STORAGE.REGISTRY)
        {
            capture(capture.getCapture(),capture.getBase(),from, _to, face, texture, side, settings, rotation, shade, modelId);
        }
    }

    public void capture(HashMap<Direction,List<QuadIngredients>> hashMap,String base, Vec3f from, Vec3f _to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, ModelRotation rotation, boolean shade, Identifier modelId)
    {

        if(modelId.getPath().equals(base))
        {
            if(SimpleFrames.STATE<2)
            {
                if(!hashMap.containsKey(side))
                {
                    hashMap.put(side,new ArrayList<>());
                }
                List<QuadIngredients> ingredients = hashMap.get(side);
                ingredients.add(new QuadIngredients(from, _to, face, texture, side, settings, rotation, shade, modelId));
                hashMap.put(side,ingredients);
            }
            else
            {
                if(!hashMap.containsKey(side))
                {
                    SimpleFrames.LOGGER.error("a capture hashmap was unpopulated after finishing initialisation, will populate it now");
                    hashMap.put(side,new ArrayList<>());
                    List<QuadIngredients> ingredients = hashMap.get(side);
                    ingredients.add(new QuadIngredients(from, _to, face, texture, side, settings, rotation, shade, modelId));
                    hashMap.put(side,ingredients);
                }

            }

        }
    }


}
