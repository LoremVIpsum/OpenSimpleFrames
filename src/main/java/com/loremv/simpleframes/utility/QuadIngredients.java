package com.loremv.simpleframes.utility;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.Arrays;

/**
 * Effectively identical to a BakedQuad, but we can get all the data from it, and convert it back using inputs.
 */
public class QuadIngredients {
    private static final BakedQuadFactory bakedQuadFactory = new BakedQuadFactory();
    Vector3f from;
    Vector3f _to;
    ModelElementFace face;
    Sprite texture;
    Direction side;
    ModelBakeSettings settings;
    ModelRotation rotation;
    boolean shade;
    Identifier modelId;

    public QuadIngredients(Vector3f from, Vector3f _to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, ModelRotation rotation, boolean shade, Identifier modelId) {
        this.from = from;
        this._to = _to;
        this.face = face;
        this.texture = texture;
        this.side = side;
        this.settings = settings;
        this.rotation = rotation;
        this.shade = shade;
        this.modelId = modelId;
    }

    public BakedQuad toQuad(Sprite altSprite)
    {
        return bakedQuadFactory.bake(from,_to,face,altSprite,side,settings,rotation,shade,modelId);
    }
    public BakedQuad toQuad(Sprite altSprite, net.minecraft.client.render.model.ModelRotation altRotation)
    {
        return bakedQuadFactory.bake(from,_to,face,altSprite,side,altRotation,rotation,shade,modelId);
    }

    /**
     * in this method we can reconstruct quads to use "illegal" block models.
     * e.g. you cannot pass ramp corner shapes into the bakedQuadFactory because from -> _to always draws a square and can only have
     * 1 rotational axis, meaning we can't draw complex shapes that require folds or triangles
     * we are still technically drawing shapes with 4 corners, but you can fold them
     * @param altSprite the texture to use when the model is complete
     * @param idea a model idea to rebake with, so we can have "illegal" block models
     * @return an "illegal" baked quad.
     */
    public BakedQuad thenRebake(Sprite altSprite,ModelIdea idea)
    {
        BakedQuad bakedQuad = toQuad(altSprite, net.minecraft.client.render.model.ModelRotation.X0_Y0);

        //vertex data isn't actually that scary when you get down to it see the packVertexData on line 139 of BakedQuadFactory
        int[] bakedVertices = bakedQuad.getVertexData();

        float[][] posMatrix = new float[4][3];
        for (int i = 0; i < 4; i++) {

            posMatrix[i][0]=Float.intBitsToFloat(bakedVertices[i*8    ]);
            posMatrix[i][1]=Float.intBitsToFloat(bakedVertices[i*8 + 1]);
            posMatrix[i][2]=Float.intBitsToFloat(bakedVertices[i*8 + 2]);
        }
        //now we have unpacked the data, we can do stuff to our pos matrix before recompiling it

        for(Direction direction: idea.getQuads().keySet())
        {
            if(side==direction)
            {
                for(int corner: idea.getQuads().get(side).keySet())
                {
                    posMatrix[corner]=idea.getQuads().get(side).get(corner);
                }
            }
        }

        if(idea==ModelIdeas.CUBE)
        {
            SimpleFrames.LOGGER.info(this.side.name()+": "+Arrays.deepToString(posMatrix));
        }




        for (int i = 0; i < 4; i++) {
            bakedVertices[i*8    ]=Float.floatToRawIntBits(posMatrix[i][0]);
            bakedVertices[i*8 + 1]=Float.floatToRawIntBits(posMatrix[i][1]);
            bakedVertices[i*8 + 2]=Float.floatToRawIntBits(posMatrix[i][2]);

        }

        //since our quad is technically already baked, we just construct a new BakedQuad, rather than rebaking it
        //that does technically mean this method name is wrong, but it sounded cooler.
        return new BakedQuad(bakedVertices,bakedQuad.getColorIndex(),bakedQuad.getFace(),bakedQuad.getSprite(),false);
    }









}
