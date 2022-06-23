package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.*;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

public class GameScreen implements Screen, InputProcessor {
    private Engine engine;
    private GameData gameData;
    private SpriteBatch batch;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private FitViewport viewport;
    private WorldManager world;
    private Player player;
    private Sound shootSound;
    private Music music;
    public static Sound hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
    private Sprite grapplingHookRope;

    public GameScreen() {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        gameData = GameData.getInstance();
        world = gameData.getWorldManager();
        batch = new SpriteBatch();
        player = new Player(world, batch);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;
        viewport = new FitViewport(Gdx.graphics.getWidth() / Constants.PPM, Gdx.graphics.getHeight() / Constants.PPM, camera);
        gameData.setViewport(viewport);

        engine = new Engine();

        gameData.setPlayerHealth(100);
        gameData.setPlayerShield(100);
        gameData.setPlayer(player);
        gameData.setBatch(batch);
        gameData.setMapRenderSystem(new MapRenderSystem(gameData, camera));
        gameData.setItemSystem(new ItemSystem(gameData, batch));
        gameData.setHudRenderSystem(new HudRenderSystem(gameData));
        engine.addSystem(gameData.getMapRenderSystem());
        engine.addSystem(gameData.getItemSystem());
        engine.addSystem(gameData.getHudRenderSystem());

        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));

        music = Gdx.audio.newMusic(Gdx.files.internal("themeSong.wav"));
        music.setLooping(true);
        music.play();

        grapplingHookRope = new Sprite(new Texture("items/grapplingHookRope.png"));
//        grapplingHookRope.setFlip(false, true); // inverted for some reason...
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
        camera.position.lerp(new Vector3(player.getPosition().x, player.getPosition().y, 0), 0.1f);

        //hey you're clogging up the main render method
        if (gameData.isGrapplingShot()) {
            // draw a line from the player to the grappling hook
            Vector2 playerPos = player.getItemPosition();
            Vector2 hookPos = gameData.getGrapplingPosition();

            // a**2 == b**2 + c**2
            float b = hookPos.y - playerPos.y;
            float c = playerPos.x - hookPos.x;
            float a = (float) Math.sqrt(Math.pow(c, 2) + Math.pow(b, 2));

            // looks like we gotta use trig again :D
            float angle = (float) Math.atan(b / c) * MathUtils.radiansToDegrees;
            if (hookPos.x < playerPos.x) {
                angle += 180;
            }
            angle *= -1;

            // draw a line from the player to the grappling hook
            batch.begin();
            grapplingHookRope.setSize(a, grapplingHookRope.getHeight());
            grapplingHookRope.setPosition(playerPos.x, playerPos.y);
            grapplingHookRope.setRotation(angle);
            grapplingHookRope.draw(batch);
            batch.end();
        }

        if (gameData.isGrapplingPulling()) {
            Vector2 playerPos = player.getPosition();
            Vector2 hookPos = gameData.getGrapplingPosition();

            // a**2 == b**2 + c**2
            float b = hookPos.y - playerPos.y;
            float c = playerPos.x - hookPos.x;
            float a = (float) Math.sqrt(Math.pow(c, 2) + Math.pow(b, 2));

            // move the player closer to the grappling hook
            player.setPosition(playerPos.x + (hookPos.x - playerPos.x) / 10, playerPos.y + (hookPos.y - playerPos.y) / 10);

            // if the player is close enough to the grappling hook, destroy the grappling hook
            if (a < 20) {
                gameData.setGrapplingPulling(false);
                gameData.setGrapplingPosition(null);
            }
        }

        player.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.dispose();
        GameData.getInstance().getAssets().getManager().dispose();
        GameData.getInstance().getWorld().dispose();
        GameData.getInstance().getBatch().dispose();
        GameData.getInstance().getMapRenderSystem().dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        player.onKeyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        player.onKeyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        gameData.getWorldManager().createNPC(NPC.NPCType.MCMUFFIN_HENCHMAN, Constants.SPAWN_X, Constants.SPAWN_Y);

        if (gameData.getHeldItemType() == null) return false;

        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        camera.unproject(worldCoordinates);
        Vector2 mousePos = new Vector2(worldCoordinates.x, worldCoordinates.y);

        float speed = 300f;  // set the speed of the bullet
        float shooterX = player.getItemPosition().x; // get player location
        float shooterY = player.getItemPosition().y; // get player location
        float velx = mousePos.x - shooterX; // get distance from shooter to target on x plain
        float vely = mousePos.y - shooterY; // get distance from shooter to target on y plain
        float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
        if (length != 0) {
            velx = velx / length * 1.5f;  // get required x velocity to aim at target
            vely = vely / length * 1.5f;  // get required y velocity to aim at target
        }

        if (gameData.getHeldItemType() == Item.ItemType.HANDGUN
                || gameData.getHeldItemType() == Item.ItemType.SHOTGUN
                ) {

            shootSound.play();
            gameData.getWorldManager().createBullet(shooterX, shooterY, velx * speed, vely * speed, Bullet.Origin.PLAYER);
        }

        if (gameData.getHeldItemType() == Item.ItemType.GRAPPLER && !gameData.isGrapplingShot() && !gameData.isGrapplingPulling()) {
            speed /= 0.5f;
            shootSound.play();
            gameData.setGrapplingShot(true);
            gameData.getWorldManager().createGrapplingHook(shooterX, shooterY, velx * speed, vely * speed);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        player.onMouseMoved(screenX, screenY, camera);

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
