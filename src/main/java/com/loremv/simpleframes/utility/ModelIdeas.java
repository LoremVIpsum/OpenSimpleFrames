package com.loremv.simpleframes.utility;

import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashMap;

/**
 * unfortunately, modelIdeas require a HashMap<Direction,HashMap<Integer,float[]>> to be used properly
 * as such, we set up each idea at runtime since making them final static would hurt my soul.
 */
public class ModelIdeas {
    public static HashMap<String,ModelIdea> IDEAS = new HashMap<>();
    //a square based triangular prism, used to create outward corner ramps by moving the peak
    public static ModelIdea SPIKE4;
    //a triangular based slanted prism, take a SPIKE4 and cut it in half diagonally downwards
    public static ModelIdea SPIKE3;

    public static ModelIdea DIAGONALCUT;

    public static ModelIdea CUBE;

    public static ModelIdea CUBE4x4;

    public static void init()
    {
        HashMap<Direction,HashMap<Integer,float[]>> quads = new HashMap<>();

        HashMap<Integer,float[]> corner = new HashMap<>();
        corner.put(0,new float[]{0.5f,1,0.5f});
        corner.put(1,new float[]{0,0,1});
        corner.put(2,new float[]{1,0,1});
        corner.put(3,new float[]{0.5f,1,0.5f});
        quads.put(Direction.SOUTH,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{0.5f,1,0.5f});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{0,0,1});
        corner.put(3,new float[]{0.5f,1,0.5f});
        quads.put(Direction.WEST,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{0.5f,1,0.5f});
        corner.put(1,new float[]{1,0,1});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{0.5f,1,0.5f});
        quads.put(Direction.EAST,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0.5f,1,0.5f});
        corner.put(1,new float[]{1,0,0});
        corner.put(2,new float[]{0,0,0});
        corner.put(3,new float[]{0.5f,1,0.5f});
        quads.put(Direction.NORTH,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,1});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,0,1});
        quads.put(Direction.DOWN,corner);

        SPIKE4 = new ModelIdea(quads, Arrays.asList(Direction.SOUTH,Direction.WEST,Direction.NORTH,Direction.EAST,Direction.DOWN));
        IDEAS.put("SPIKE4",SPIKE4);


        quads = new HashMap<>();

        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,1});
        corner.put(1,new float[]{0,0,1});
        corner.put(2,new float[]{1,0,1});
        corner.put(3,new float[]{1,1,1});
        quads.put(Direction.SOUTH,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,1});
        corner.put(1,new float[]{1,0,0});
        corner.put(2,new float[]{0,0,1});
        corner.put(3,new float[]{1,1,1});
        quads.put(Direction.WEST,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,1});
        corner.put(1,new float[]{1,0,1});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,1,1});
        quads.put(Direction.EAST,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,1});
        corner.put(1,new float[]{1,0,0});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,0,1});
        quads.put(Direction.DOWN,corner);



        SPIKE3 = new ModelIdea(quads, Arrays.asList(Direction.SOUTH,Direction.WEST,Direction.EAST,Direction.DOWN));
        IDEAS.put("SPIKE3",SPIKE3);

        quads = new HashMap<>();

        corner = new HashMap<>();
        corner.put(0,new float[]{0,1,1});
        corner.put(1,new float[]{0,0,1});
        corner.put(2,new float[]{1,0,1});
        corner.put(3,new float[]{1,1,1});
        quads.put(Direction.SOUTH,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{0,1,1});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{0,0,1});
        corner.put(3,new float[]{0,1,1});
        quads.put(Direction.WEST,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,1});
        corner.put(1,new float[]{1,0,1});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,1,0});
        quads.put(Direction.EAST,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,0});
        corner.put(1,new float[]{1,0,0});
        corner.put(2,new float[]{0,0,0});
        corner.put(3,new float[]{1,1,0});
        quads.put(Direction.NORTH,corner);


        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,0});
        corner.put(1,new float[]{0,1,1});
        corner.put(2,new float[]{1,1,1});
        corner.put(3,new float[]{1,1,0});
        quads.put(Direction.UP,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,1});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,0,1});
        quads.put(Direction.DOWN,corner);



        DIAGONALCUT = new ModelIdea(quads, Arrays.asList(Direction.SOUTH,Direction.WEST,Direction.EAST,Direction.DOWN,Direction.UP,Direction.NORTH));
        IDEAS.put("DIAGONALCUT",DIAGONALCUT);

        quads = new HashMap<>();
        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,4});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{4,0,0});
        corner.put(3,new float[]{4,0,4});
        quads.put(Direction.DOWN,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,4,0});
        corner.put(1,new float[]{0,4,4});
        corner.put(2,new float[]{4,4,4});
        corner.put(3,new float[]{4,4,0});
        quads.put(Direction.UP,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{4,4,0});
        corner.put(1,new float[]{4,0,0});
        corner.put(2,new float[]{0,0,0});
        corner.put(3,new float[]{0,4,0});
        quads.put(Direction.NORTH,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,4,4});
        corner.put(1,new float[]{0,0,4});
        corner.put(2,new float[]{4,0,4});
        corner.put(3,new float[]{4,4,4});
        quads.put(Direction.SOUTH,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,4,0});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{0,0,4});
        corner.put(3,new float[]{0,4,4});
        quads.put(Direction.WEST,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{4,4,4});
        corner.put(1,new float[]{4,0,4});
        corner.put(2,new float[]{4,0,0});
        corner.put(3,new float[]{4,4,0});
        quads.put(Direction.EAST,corner);


        CUBE4x4 = new ModelIdea(quads, Arrays.asList(Direction.SOUTH,Direction.WEST,Direction.EAST,Direction.DOWN,Direction.UP,Direction.NORTH));
        IDEAS.put("CUBE4x4",CUBE4x4);


        quads = new HashMap<>();
        corner = new HashMap<>();
        corner.put(0,new float[]{0,0,1});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,0,1});
        quads.put(Direction.DOWN,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,1,0});
        corner.put(1,new float[]{0,1,1});
        corner.put(2,new float[]{1,1,1});
        corner.put(3,new float[]{1,1,0});
        quads.put(Direction.UP,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,0});
        corner.put(1,new float[]{1,0,0});
        corner.put(2,new float[]{0,0,0});
        corner.put(3,new float[]{0,1,0});
        quads.put(Direction.NORTH,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,1,1});
        corner.put(1,new float[]{0,0,1});
        corner.put(2,new float[]{1,0,1});
        corner.put(3,new float[]{1,1,1});
        quads.put(Direction.SOUTH,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{0,1,0});
        corner.put(1,new float[]{0,0,0});
        corner.put(2,new float[]{0,0,1});
        corner.put(3,new float[]{0,1,1});
        quads.put(Direction.WEST,corner);

        corner = new HashMap<>();
        corner.put(0,new float[]{1,1,1});
        corner.put(1,new float[]{1,0,1});
        corner.put(2,new float[]{1,0,0});
        corner.put(3,new float[]{1,1,0});
        quads.put(Direction.EAST,corner);


        CUBE = new ModelIdea(quads, Arrays.asList(Direction.SOUTH,Direction.WEST,Direction.EAST,Direction.DOWN,Direction.UP,Direction.NORTH));
        IDEAS.put("CUBE",CUBE);



    }
}
