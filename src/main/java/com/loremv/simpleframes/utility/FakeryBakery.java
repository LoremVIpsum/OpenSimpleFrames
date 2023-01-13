package com.loremv.simpleframes.utility;

import com.loremv.simpleframes.blocks.FrameCoverBlock;
import com.loremv.simpleframes.blocks.FramePartitionBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A utility class that can fake block models, and they can be used for framed blocks.
 * we do actually need a model to work from, so we know how to reconstruct the models later with a new texture
 */
public class FakeryBakery {

    public static HashMap<BlockState,BasicBakedModel> BLOCK_CACHE = new HashMap<>();
    private static final Random random = new Random();
    /**
     * create a stair block on the fly
     * because stairs are wierd, this can't be generified much further
     * @param basicBakedModel a model that was created properly with json, to "borrow" data from
     * @param originalState the state of the block we are borrowing from
     * @return a block that renders the same way the input would, but with a new texture
     */
    public static BasicBakedModel createFakeStair(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture)
    {
        return createFakeStair(basicBakedModel, originalState, stateForTexture,null);
    }

    public static BasicBakedModel createFakeStair(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture,ModelIdea idea)
    {

        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }

        //Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).getSprite(new Identifier("block/amethyst_block"));


        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();

        //this bit is basically because stairs have different models for corners
        HashMap<Direction,List<QuadIngredients>> stair = CapturedBlockStorage.find("mossy_cobblestone_stairs").getCapture();
        if(originalState.get(StairsBlock.SHAPE)==StairShape.INNER_RIGHT || originalState.get(StairsBlock.SHAPE)==StairShape.INNER_LEFT)
        {
            stair=CapturedBlockStorage.find("block/mossy_cobblestone_stairs_inner").getCapture();
        }
        if(originalState.get(StairsBlock.SHAPE)==StairShape.OUTER_RIGHT || originalState.get(StairsBlock.SHAPE)==StairShape.OUTER_LEFT)
        {
            stair=CapturedBlockStorage.find("block/mossy_cobblestone_stairs_outer").getCapture();
        }

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(stair,direction,ame,processStairs(originalState),idea));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(stair,ame,processStairs(originalState),idea);

        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());
        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }

    public static BasicBakedModel createFakeRamp(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture)
    {

        //TODO: we need to find a way to recalculate light on ramps - something to do with normals? (or make a model idea for the straight ramp)

        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }

        //Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).getSprite(new Identifier("block/amethyst_block"));


        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();

        ModelIdea idea = null;
        //this bit is basically because stairs have different models for corners
        HashMap<Direction,List<QuadIngredients>> stair = CapturedBlockStorage.find("block/framed_ramp").getCapture();
        if(originalState.get(StairsBlock.SHAPE)==StairShape.INNER_RIGHT || originalState.get(StairsBlock.SHAPE)==StairShape.INNER_LEFT)
        {
            idea=ModelIdeas.DIAGONALCUT;
            stair=CapturedBlockStorage.find("cobblestone").getCapture();
        }
        if(originalState.get(StairsBlock.SHAPE)==StairShape.OUTER_RIGHT || originalState.get(StairsBlock.SHAPE)==StairShape.OUTER_LEFT)
        {
            idea=ModelIdeas.SPIKE4;
            stair=CapturedBlockStorage.find("cobblestone").getCapture();
        }

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();

        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(stair,direction,ame,processStairs(originalState),processRamp(originalState,idea)));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(stair,ame,processStairs(originalState),processRamp(originalState,idea));


        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());
        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }



    public static BasicBakedModel createFakeStaticBlock(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture,HashMap<Direction,List<QuadIngredients>> block)
    {
        return createFakeStaticBlock(basicBakedModel, originalState, stateForTexture, block,null);
    }

    public static BasicBakedModel createFakeStaticBlock(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture,HashMap<Direction,List<QuadIngredients>> block,ModelIdea idea)
    {
        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }
        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();

        ModelRotation rotation = ModelRotation.X0_Y0;
        if(originalState.contains(FramePartitionBlock.ON_X))
        {
            if(originalState.get(FramePartitionBlock.ON_X))
            {
                rotation = ModelRotation.X0_Y90;
            }
        }
        if(originalState.contains(HorizontalFacingBlock.FACING))
        {
            switch (originalState.get(HorizontalFacingBlock.FACING))
            {
                case EAST -> rotation = ModelRotation.X0_Y90;
                case SOUTH -> rotation = ModelRotation.X0_Y180;
                case WEST -> rotation = ModelRotation.X0_Y270;
                case NORTH -> rotation = ModelRotation.X0_Y0;

            }
        }
        if(originalState.contains(FrameCoverBlock.DIRECITON))
        {
            switch (originalState.get(FrameCoverBlock.DIRECITON))
            {
                case EAST -> rotation = ModelRotation.X0_Y90;
                case SOUTH -> rotation = ModelRotation.X0_Y180;
                case WEST -> rotation = ModelRotation.X0_Y270;
                case NORTH -> rotation = ModelRotation.X0_Y0;
                case UP -> rotation = ModelRotation.X270_Y0;
                case DOWN -> rotation = ModelRotation.X90_Y0;

            }
        }

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(block,direction,ame,rotation,idea));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(block,ame,rotation,idea);


        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());

        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }
    public static BasicBakedModel createFakeDoor(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture)
    {
        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }
        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();


        HashMap<Direction,List<QuadIngredients>> door = CapturedBlockStorage.find("block/framed_door").getCapture();
        ModelRotation rotation = ModelRotation.X0_Y0;
        if(originalState.contains(HorizontalFacingBlock.FACING))
        {
            switch (originalState.get(HorizontalFacingBlock.FACING))
            {
                case EAST -> rotation = ModelRotation.X0_Y90;
                case SOUTH -> rotation = ModelRotation.X0_Y180;
                case WEST -> rotation = ModelRotation.X0_Y270;
                case NORTH -> rotation = ModelRotation.X0_Y0;

            }
        }
        if(originalState.get(DoorBlock.OPEN))
        {
            door = CapturedBlockStorage.find("block/framed_door_open").getCapture();
        }
       

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(door,direction,ame,rotation,null));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(door,ame,rotation,null);


        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());

        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }

    public static BasicBakedModel createFakeChest(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture)
    {
        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }
        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();


        HashMap<Direction,List<QuadIngredients>> chest = CapturedBlockStorage.find("block/framed_chest").getCapture();
        ModelRotation rotation = ModelRotation.X0_Y0;
        if(originalState.contains(HorizontalFacingBlock.FACING))
        {
            switch (originalState.get(HorizontalFacingBlock.FACING))
            {
                case EAST -> rotation = ModelRotation.X0_Y90;
                case SOUTH -> rotation = ModelRotation.X0_Y180;
                case WEST -> rotation = ModelRotation.X0_Y270;
                case NORTH -> rotation = ModelRotation.X0_Y0;

            }
        }
        switch (originalState.get(ChestBlock.CHEST_TYPE))
        {
            case LEFT -> chest = CapturedBlockStorage.find("block/framed_chest_left").getCapture();
            case RIGHT -> chest = CapturedBlockStorage.find("block/framed_chest_right").getCapture();
        }

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(chest,direction,ame,rotation,null));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(chest,ame,rotation,null);


        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());

        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }

    public static BasicBakedModel createFakeFence(BakedModel basicBakedModel, BlockState originalState, BlockState stateForTexture)
    {
        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }
        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();


        HashMap<Direction,List<QuadIngredients>> fence = CapturedBlockStorage.find("block/framed_fence_post").getCapture();
        ModelRotation rotation = ModelRotation.X0_Y0;
        boolean[] nesw = new boolean[]{originalState.get(FenceBlock.NORTH),originalState.get(FenceBlock.EAST),originalState.get(FenceBlock.SOUTH),originalState.get(FenceBlock.WEST)};
        if(nesw[0])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_n").getCapture();
        }
        if(nesw[1])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_n").getCapture();
            rotation = ModelRotation.X0_Y90;
        }
        if(nesw[2])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_n").getCapture();
            rotation = ModelRotation.X0_Y180;
        }
        if(nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_n").getCapture();
            rotation = ModelRotation.X0_Y270;
        }
        if(nesw[0] && nesw[1])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ne").getCapture();
            rotation=ModelRotation.X0_Y0;
        }
        if(nesw[1] && nesw[2])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ne").getCapture();
            rotation=ModelRotation.X0_Y90;
        }
        if(nesw[2] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ne").getCapture();
            rotation=ModelRotation.X0_Y180;
        }
        if(nesw[0] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ne").getCapture();
            rotation=ModelRotation.X0_Y270;
        }


        if(nesw[0] && nesw[2])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ns").getCapture();
            rotation=ModelRotation.X0_Y0;
        }
        if(nesw[1] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_ns").getCapture();
            rotation=ModelRotation.X0_Y90;
        }


        if(nesw[0] && nesw[1] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_new").getCapture();
            rotation=ModelRotation.X0_Y0;
        }
        if(nesw[0] && nesw[1] && nesw[2])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_new").getCapture();
            rotation=ModelRotation.X0_Y90;
        }
        if(nesw[1] && nesw[2] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_new").getCapture();
            rotation=ModelRotation.X0_Y180;
        }
        if(nesw[0] && nesw[2] && nesw[3])
        {
            fence=CapturedBlockStorage.find("block/framed_fence_new").getCapture();
            rotation=ModelRotation.X0_Y270;
        }


        if(nesw[0] && nesw[1] && nesw[2] && nesw[3])
        {
            rotation=ModelRotation.X0_Y0;
            fence=CapturedBlockStorage.find("block/framed_fence_news").getCapture();
        }

        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(fence,direction,ame,rotation,null));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(fence,ame,rotation,null);


        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());

        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }
    public static BasicBakedModel createFakeSlab(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture)
    {
        return createFakeSlab(basicBakedModel,originalState,stateForTexture,null);
    }

    public static BasicBakedModel createFakeSlab(BakedModel basicBakedModel, BlockState originalState,BlockState stateForTexture, ModelIdea idea)
    {

        //original state should a framed block, specifically with a texture_id property
        if(BLOCK_CACHE.containsKey(originalState))
        {
            return BLOCK_CACHE.get(originalState);
        }
        Sprite ame = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(stateForTexture).getQuads(stateForTexture,Direction.DOWN,random).get(0).getSprite();

        HashMap<Direction,List<QuadIngredients>> block;
        block = switch (originalState.get(SlabBlock.TYPE))
            {
                case BOTTOM -> CapturedBlockStorage.find("framed_slab_block").getCapture();
                case TOP -> CapturedBlockStorage.find("block/framed_slab_top").getCapture();
                case DOUBLE -> CapturedBlockStorage.find("cobblestone").getCapture();
            };


        //this bit barely changes for other block types, we add additional processing for stair rotation however
        HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        for(Direction direction: Direction.values())
        {
            faceQuads.put(direction,fakeDirectionalQuadsUsingSprite(block,direction,ame,ModelRotation.X0_Y0,idea));
        }

        List<BakedQuad> bakedQuads = fakeQuadsUsingSprite(block,ame,ModelRotation.X0_Y0,idea);

        BasicBakedModel nmodel = new BasicBakedModel(bakedQuads,faceQuads,basicBakedModel.useAmbientOcclusion(),basicBakedModel.isSideLit(),basicBakedModel.hasDepth(),ame, basicBakedModel.getTransformation(), basicBakedModel.getOverrides());
        BLOCK_CACHE.put(originalState,nmodel);

        return nmodel;
    }

    /**
     * take a stair blockstate, then jank our captured model to be correctly orientated.
     * @param state a stair
     * @return the correct way to render the stair based on rotations
     */
    private static ModelRotation processStairs(BlockState state)
    {
        int x = 0;
        int y = 0;
        Direction direction = state.get(StairsBlock.FACING);
        BlockHalf half = state.get(StairsBlock.HALF);
        StairShape stairShape = state.get(StairsBlock.SHAPE);

        switch (direction)
        {
            //east is 0
            case SOUTH -> y=90;
            case WEST -> y=180;
            case NORTH -> y=270;
        }
        if(half==BlockHalf.TOP)
        {
            x=180;
        }
        if(stairShape==StairShape.INNER_LEFT || stairShape==StairShape.OUTER_LEFT)
        {

            if(half==BlockHalf.BOTTOM)
            {
                y-=90;
            }
        }
        if(stairShape==StairShape.INNER_RIGHT || stairShape==StairShape.OUTER_RIGHT)
        {
            if(half==BlockHalf.TOP)
            {
                y+=90;
            }
        }

        return ModelRotation.get(x,y);
    }

    private static ModelIdea processRamp(BlockState state,ModelIdea idea)
    {
        if(idea==null)
        {
            return null;
        }

        ModelIdea out = idea.deepCopy();

        Direction dirFacing = state.get(StairsBlock.FACING);
        if(state.get(StairsBlock.SHAPE)==StairShape.INNER_LEFT || state.get(StairsBlock.SHAPE)==StairShape.INNER_RIGHT)
        {
            switch (state.get(StairsBlock.FACING))
            {
                case WEST -> out.rotateYClockwise();
                case NORTH -> out.rotateYClockwise(2);
                case EAST -> out.rotateYClockwise(3);
            }
            if(state.get(StairsBlock.HALF)==BlockHalf.TOP)
            {
                if(state.get(StairsBlock.SHAPE)==StairShape.INNER_LEFT)
                {
                    out.rotateYClockwise(1);
                }
                switch (state.get(StairsBlock.FACING))
                {
                    case NORTH, SOUTH -> out.rotateYClockwise(2);
                }
            }
            else if(state.get(StairsBlock.SHAPE)==StairShape.INNER_RIGHT)
            {
                out.rotateYClockwise(1);
            }
        }
        if(state.get(StairsBlock.SHAPE)==StairShape.OUTER_LEFT || state.get(StairsBlock.SHAPE)==StairShape.OUTER_RIGHT)
        {

            if(state.get(StairsBlock.SHAPE)==StairShape.OUTER_RIGHT)
            {
                dirFacing = dirFacing.rotateYClockwise();
            }
            switch (dirFacing)
            {
                case SOUTH -> out.moveOnePoint(new float[]{0.5f,1,0.5f},new float[]{1,1,1});
                case EAST -> out.moveOnePoint(new float[]{0.5f,1,0.5f},new float[]{1,1,0});
                case NORTH -> out.moveOnePoint(new float[]{0.5f,1,0.5f},new float[]{0,1,0});
                case WEST -> out.moveOnePoint(new float[]{0.5f,1,0.5f},new float[]{0,1,1});

            }
            if(state.get(StairsBlock.HALF)==BlockHalf.TOP)
            {
                switch (state.get(StairsBlock.FACING)) {
                    case NORTH, SOUTH -> out.rotateYClockwise(1);
                    case EAST, WEST -> out.rotateYClockwise(3);
                }
                if(state.get(StairsBlock.SHAPE)==StairShape.OUTER_LEFT)
                {
                    out.rotateYClockwise(2);
                }
            }

        }

        if(state.get(StairsBlock.HALF)==BlockHalf.TOP)
        {
            out.rotateXClockwise(2);

        }
        return out;
    }

    /**
     * generate (and bake) quads on the fly that have a groovy new texture, but respect the captured model inputted.
     * @param model the captured model to use as a base model
     * @param face the face of the block this quad is on
     * @param sprite the fancy new texture
     * @param rotation used for blocks that care about direction
     * @return a list of baked quads for the given face
     */
    private static List<BakedQuad> fakeDirectionalQuadsUsingSprite(HashMap<Direction,List<QuadIngredients>> model, Direction face,Sprite sprite, ModelRotation rotation, ModelIdea idea)
    {
        List<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < model.get(face).size(); i++) {
            if(idea!=null)
            {
                if(idea.getDirections().contains(face))
                {
                    quads.add(model.get(face).get(i).thenRebake(sprite,idea));
                }


            }
            else
            {
                quads.add(model.get(face).get(i).toQuad(sprite,rotation));
            }
        }



        return quads;
    }

    /**
     * the same thing as above, but we don't actually care about the face this time
     * we need this because minecraft
     * @param model the captured model to use as a base model
     * @param sprite the fancy new texture
     * @param rotation used for blocks that care about direction
     * @return a list of baked quads
     */
    private static List<BakedQuad> fakeQuadsUsingSprite(HashMap<Direction,List<QuadIngredients>> model,Sprite sprite, ModelRotation rotation,ModelIdea idea)
    {
        List<BakedQuad> quads = new ArrayList<>();
        for(Direction direction: model.keySet())
        {
            for (int i = 0; i < model.get(direction).size(); i++) {
                if(idea!=null)
                {
                    if(idea.getDirections().contains(direction))
                    {
                        quads.add(model.get(direction).get(i).thenRebake(sprite,idea));
                    }
                }
                else
                {
                    quads.add(model.get(direction).get(i).toQuad(sprite,rotation));
                }


            }
        }
        return quads;
    }





}
