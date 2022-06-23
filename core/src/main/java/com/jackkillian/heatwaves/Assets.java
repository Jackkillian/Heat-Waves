package com.jackkillian.heatwaves;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private final AssetManager manager;

    public Assets() {
        manager = new AssetManager();
        manager.load("hud/health.png", Texture.class);
        manager.load("hud/shield.png", Texture.class);
        manager.load("player/player1.png", Texture.class);
        manager.load("hud/inventory.png", Texture.class);
        manager.load("items/handgun.png", Texture.class);
        manager.load("items/shotgun.png", Texture.class);
        manager.load("items/grapplerGun.png", Texture.class);
        manager.load("items/medkit.png", Texture.class);
        manager.load("hud/locked.png", Texture.class);
        manager.load("hud/unlocked.png", Texture.class);
    }

    public AssetManager getManager() {
        return manager;
    }

}
