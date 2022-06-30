package com.jackkillian.heatwaves;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
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

    private Game game;
    private EventHandler eventHandler;
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
    private int score;
    private boolean gameOver = false;
    private Engine engine;
    private int ammo;
    private float grapplerTimer = 0;

    public void setGrapplerTimer(float timer) {
    	this.grapplerTimer = timer;
    }

    public float getGrapplerTimer() {
    	return this.grapplerTimer;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public Game getGame() {
        return game;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
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

    public void healHealth(int health) {
        this.health += health;
        if (this.health > 100) {
            this.health = 100;
        }
    }

    public void damageHealth(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void healShield(int shield) {
        this.shield += shield;
        if (this.shield > 100) {
            this.shield = 100;
        }
    }

    public void damageShield(int damage) {
        if (damage > shield) {
            int leftover = damage - shield;
            shield = 0;
            damageHealth(leftover);
        } else {
            shield -= damage;
            if (shield < 0) {
                shield = 0;
            }
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        engine.removeAllSystems();
        engine.removeAllEntities();
        instance = null;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }
    public int getAmmo() {
        return ammo;
    }

    public Engine getEngine() {
        return engine;
    }
}
