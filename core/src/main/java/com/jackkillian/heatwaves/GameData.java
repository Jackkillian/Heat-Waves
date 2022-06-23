package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

public class GameData {
    private static GameData instance;

    private GameData() {
    }

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
    private HudRenderSystem hudRenderSystem;
    private ItemSystem itemSystem;
    private boolean grapplingShot = false;
    private boolean grapplingHit = false;
    private boolean grapplingPulling = false;
    private Vector2 grapplingPosition;
    private Player player;
    private int health;
    private int shield;

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

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public void setViewport(FitViewport viewport) {
        this.viewport = viewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() {
        return assets;
    }

    public Skin getSkin() {
        return skin;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public MapRenderSystem getMapRenderSystem() {
        return mapRenderSystem;
    }

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

    public void setHudRenderSystem(HudRenderSystem hudRenderSystem) {
        this.hudRenderSystem = hudRenderSystem;
    }

    public HudRenderSystem getHudRenderSystem() {
        return hudRenderSystem;
    }

    public World getWorld() {
        return worldManager.getWorld();
    }

    public void setGrapplingShot(boolean b) {
        grapplingShot = b;
    }

    public boolean isGrapplingShot() {
        return grapplingShot;
    }


    public void setGrapplingHit(boolean b) {
        grapplingHit = b;
    }

    public boolean isGrapplingHit() {
        return grapplingHit;
    }

    public void setGrapplingPulling(boolean b) {
        grapplingPulling = b;
    }

    public boolean isGrapplingPulling() {
        return grapplingPulling;
    }

    public Vector2 getGrapplingPosition() {
        return grapplingPosition;
    }

    public void setGrapplingPosition(Vector2 grapplingPosition) {
        this.grapplingPosition = grapplingPosition;
    }

    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPlayerShield() {
        return shield;
    }

    public int getPlayerHealth() {
        return health;
    }

    public void setPlayerHealth(int health) {
        this.health = health;
    }

    public void setPlayerShield(int shield) {
        this.shield = shield;
    }
}
