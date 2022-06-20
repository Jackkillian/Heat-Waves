package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

public class GameData {
    private static GameData instance;
    private GameData() {}
    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }


    private SpriteBatch batch;
    private Assets assets;
    private World world;
    private Skin skin;
    private FitViewport viewport;
    private OrthographicCamera camera;

    private MapRenderSystem mapRenderSystem;
    private HudRenderSystem hudRenderSystem;
    private ItemSystem itemSystem;

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void setMapRenderSystem(MapRenderSystem mapRenderSystem) {
        this.mapRenderSystem = mapRenderSystem;
    }
    public void setHudRenderSystem(HudRenderSystem hudRenderSystem) {
        this.hudRenderSystem = hudRenderSystem;
    }
    public void setItemSystem(ItemSystem itemSystem) {
        this.itemSystem = itemSystem;
    }
    public void setAssets(Assets assets) {
        this.assets = assets;
    }
    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
    public void setWorld(World world) {this.world = world;}
    public void setSkin(Skin skin) {this.skin = skin;}
    public void setViewport(FitViewport viewport) {this.viewport = viewport;}

    public World getWorld() {return world;}
    public SpriteBatch getBatch() {
        return batch;
    }
    public Assets getAssets() {
        return assets;
    }
    public Skin getSkin() {return skin;}
    public FitViewport getViewport() {return viewport;}

    public ItemSystem getItemSystem() {
        return itemSystem;
    }
    public MapRenderSystem getMapRenderSystem() {
        return mapRenderSystem;
    }
    public HudRenderSystem getHudRenderSystem() {
        return hudRenderSystem;
    }
    public OrthographicCamera getCamera() {
        return camera;
    }


}
