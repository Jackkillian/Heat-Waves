package com.jackkillian.heatwaves;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private final AssetManager manager;

    public Assets() {
        manager = new AssetManager();
        manager.load("hud/health.png", Texture.class);
        manager.load("hud/shield.png", Texture.class);
    }


    public AssetManager getManager() {
        return manager;
    }

}
