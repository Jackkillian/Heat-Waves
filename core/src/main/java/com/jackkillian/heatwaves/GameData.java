package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private Skin skin;
    private FitViewport viewport;
    private boolean touchingPlatform = false;
    private Item.ItemType itemType;
    private WorldManager worldManager;
    private MapRenderSystem mapRenderSystem;
    private ItemSystem itemSystem;

    public void setItemSystem(ItemSystem itemSystem) {
        this.itemSystem = itemSystem;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }
    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
    public void setMapRenderSystem(MapRenderSystem mapRenderSystem) {
        this.mapRenderSystem = mapRenderSystem;
    }
    public void setSkin(Skin skin) {this.skin = skin;}
    public void setViewport(FitViewport viewport) {this.viewport = viewport;}
    public SpriteBatch getBatch() {
        return batch;
    }
    public Assets getAssets() {
        return assets;
    }
    public Skin getSkin() {return skin;}
    public FitViewport getViewport() {return viewport;}
    public MapRenderSystem getMapRenderSystem() {return mapRenderSystem;}

    public ItemSystem getItemSystem() {
        return itemSystem;
    }

    public void setTouchingPlatform(boolean b) {
        touchingPlatform = b;
    }

    public boolean isTouchingPlatform() {
        return touchingPlatform;
    }

    public Item.ItemType getHeldItemType() {
        return itemType;
    }

    public void setHeldItemType(Item.ItemType itemType) {
        this.itemType = itemType;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public World getWorld() {
        return worldManager.getWorld();
    }
}
