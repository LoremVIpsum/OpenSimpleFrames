package com.loremv.simpleframes.utility;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * A ModelIdea is a model that minecraft would likely consider illegal.
 * model ideas are used to repack vertex data in QuadIngredients.thenRebake, this allows us to have freeform quads
 * notably, this allows corner ramps, from a json model point of view would require 2 axes of rotation, which the json
 * model format disallows.
 */
public class ModelIdea {

    private final HashMap<Direction,HashMap<Integer,float[]>> quads;
    private final List<Direction> directions;


    public ModelIdea(HashMap<Direction,HashMap<Integer,float[]>> a,List<Direction> b)
    {
        quads=a;
        directions=b;


    }

    /**
     * model ideas rely on side effects to do transformations, as such we need a way to create a copy
     * I don't know if I need to go this deep, but at least im fairly sure this map is cloned and all
     * references are different
     * @return a copy of the model idea
     */
    public ModelIdea deepCopy()
    {
        HashMap<Direction,HashMap<Integer,float[]>> nquads = new HashMap<>();


        for(Direction direction: quads.keySet())
        {
            HashMap<Integer,float[]> nVertex = new HashMap<>();
            for(Integer integer: quads.get(direction).keySet())
            {
                nVertex.put(integer,Arrays.copyOf(quads.get(direction).get(integer),quads.get(direction).get(integer).length));
            }
            nquads.put(direction,nVertex);
        }
        List<Direction> ndir = new ArrayList<>(directions);
        return new ModelIdea(nquads, ndir);
    }




    public HashMap<Direction, HashMap<Integer, float[]>> getQuads() {
        return quads;
    }

    public List<Direction> getDirections() {
        return directions;
    }


    /**
     * for each point in each quad in this idea, if it conforms to oldpoint, change it to newpoint
     * @param oldpoint a type of point that probably exists in this model
     * @param newpoint a type of point that will soon exist in this model. replacing an oldpoint
     *
     */
    public void moveOnePoint(float[] oldpoint, float[] newpoint)
    {

        for(Direction direction: quads.keySet())
        {
            for(Integer integer: quads.get(direction).keySet())
            {
                if(Arrays.equals(quads.get(direction).get(integer),oldpoint))
                {
                    quads.get(direction).put(integer,newpoint);

                }
            }
        }

    }

    public void printData()
    {
        SimpleFrames.LOGGER.info("==");
        for(Direction direction: quads.keySet())
        {
            for(Integer integer: quads.get(direction).keySet())
            {
                SimpleFrames.LOGGER.info(direction+":"+integer+"="+ Arrays.toString(quads.get(direction).get(integer)));
            }
        }
    }



    /**
     * this method is here so we can use -0.1 and -1 to represent modified quad points of 0 and 1
     * minus numbers should never be in a (conventional) quad
     * we do not want to modify already modified points, when we have finsihed all moves, we can call this
     * to reset those numbers
     */
    public void roundDown()
    {
        for(Direction direction: quads.keySet())
        {
            for(Integer integer: quads.get(direction).keySet())
            {
                for (int i = 0; i < 3; i++) {
                    if(quads.get(direction).get(integer)[i]==-0.1f)
                    {
                        quads.get(direction).get(integer)[i]=0;
                    }
                    if(quads.get(direction).get(integer)[i]==-1f)
                    {
                        quads.get(direction).get(integer)[i]=1;
                    }
                }


            }
        }


    }

    /**
     * rotating things is actually terrible
     * even though we can rotate Direction, and we can rotate ModelRotation, that doesn't change anything in this case
     * this is because no matter what face is technically doing it, each quad is represented in 3-space, so you actually
     * have to modify bits of the quad to make a proper rotation.
     * note: these methods specifically only rotate blocks that have points at corners
     */
    public void rotateYClockwise()
    {
        for (int i = 0; i < 2; i++) {

            moveOnePoint(new float[]{0,i,0},new float[]{-1,i,-0.1f});
            moveOnePoint(new float[]{0,i,1},new float[]{-0.1f,i,-0.1f});
            moveOnePoint(new float[]{1,i,1},new float[]{-0.1f,i,-1});
            moveOnePoint(new float[]{1,i,0},new float[]{-1,i,-1});


        }
        roundDown();
    }

    public void rotateYClockwise(int i)
    {
        for (int j = 0; j < i; j++) {
            rotateYClockwise();
        }
    }


    public void rotateXClockwise()
    {
        for (int i = 0; i < 2; i++) {

            moveOnePoint(new float[]{i,0,0},new float[]{i,-1,-0.1f});
            moveOnePoint(new float[]{i,0,1},new float[]{i,-0.1f,-0.1f});
            moveOnePoint(new float[]{i,1,1},new float[]{i,-0.1f,-1f});
            moveOnePoint(new float[]{i,1,0},new float[]{i,-1f,-1f});
        }
        roundDown();
    }

    public void rotateXClockwise(int i)
    {
        for (int j = 0; j < i; j++) {
            rotateXClockwise();
        }
    }


}
