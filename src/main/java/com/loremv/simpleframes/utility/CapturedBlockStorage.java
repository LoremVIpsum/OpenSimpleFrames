package com.loremv.simpleframes.utility;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Each captured block is stored here
 * we have to get them each boot anyway, so it's as good a place as any
 * and doesn't clutter up the main class.
 * Any mod can add blocks to the registry for processing, just do it before models get baked.
 */
//@Environment(EnvType.CLIENT)
public class CapturedBlockStorage {
    public static HashMap<Direction,List<QuadIngredients>> NON_JSON = new HashMap<>();


    public static List<BlockCapture> REGISTRY = new ArrayList<>();

    public static BlockCapture find(String base)
    {
        for(BlockCapture capture: REGISTRY)
        {
            if(capture.getBase().equals(base))
            {
                return capture;
            }
        }
        SimpleFrames.LOGGER.error("didn't find "+base+" in registry, this is not good, using entry 0");
        return REGISTRY.get(0);
    }


}
