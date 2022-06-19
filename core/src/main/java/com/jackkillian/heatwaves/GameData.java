package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameData {
    private static GameData instance;
    private GameData() {}
    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    private FitViewport viewport;
    private SpriteBatch batch;
    private Assets assets;

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public void setViewport(FitViewport viewport) {
        this.viewport = viewport;
    }
    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
    public SpriteBatch getBatch() {
        return batch;
    }
    public FitViewport getViewport() {
        return viewport;
    }
    public Assets getAssets() {
        return assets;
    }
}
