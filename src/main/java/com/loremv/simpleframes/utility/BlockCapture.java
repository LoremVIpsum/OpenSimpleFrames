package com.loremv.simpleframes.utility;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;

/**
 * A representation of a captured block, with a bit more information in it
 * a list of BlockCaptures is far less cursed than a HashMap<String,HashMap<Direction, List<QuadIngredients>>>
 */
public class BlockCapture {
    private HashMap<Direction, List<QuadIngredients>> capture;
    private Block of;
    private String base;

    private String method;

    private String idea;

    //TODO: it'd be really neat if we could pass in a function in (String method) instead to allow for easier external processing
    public BlockCapture(String baseModel, Block of, String method) {
        this.capture = new HashMap<>();
        this.of = of;
        this.base=baseModel;
        this.method=method;


    }

    public Block of() {
        return of;
    }

    public HashMap<Direction, List<QuadIngredients>> getCapture() {
        return capture;
    }

    public String getBase() {
        return base;
    }

    public String getMethod() {
        return method;
    }

    public String getIdea() {
        return idea;
    }

    /**
     * when this method is called, it is unlikely Model Ideas has been init-d yet, so we need to "promise" it will be
     * with a string
     * @param idea the name of the ModelIdea to use
     * @return this capture, so I don't have to do it on a separate line
     */
    public BlockCapture withPromisedIdea(String idea) {
        this.idea = idea;
        return this;
    }
}
