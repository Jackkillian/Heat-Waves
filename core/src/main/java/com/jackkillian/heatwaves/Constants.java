package com.jackkillian.heatwaves;

public class Constants {
    //pixels per meter
    public static final float PPM = 2f;
    public static final int SPAWN_X = 200;
    public static final int SPAWN_Y = 200;

    //filter box2d collision bits
    //a fixture's category bits is basically who am I
    //a fixture's mask bits is who can I collide with
    public static final short WALL_BIT = 1;
    public static final short ITEM_BIT = 2;
    public static final short PLAYER_BIT = 4;

}
