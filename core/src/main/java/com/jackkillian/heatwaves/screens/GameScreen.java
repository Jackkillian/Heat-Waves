package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.*;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
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
        GameData.getInstance().setBatch(batch);
        GameData.getInstance().setMapRenderSystem(new MapRenderSystem(gameData, camera));
        GameData.getInstance().setItemSystem(new ItemSystem(gameData, batch));
        GameData.getInstance().setHudRenderSystem(new HudRenderSystem(gameData));
        engine.addSystem(GameData.getInstance().getMapRenderSystem());
        engine.addSystem(GameData.getInstance().getHudRenderSystem());
        engine.addSystem(GameData.getInstance().getItemSystem());
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
        camera.position.lerp(new Vector3(player.getPosition().x, player.getPosition().y, 0), 0.1f);
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
        if (gameData.getHeldItemType() == Item.ItemType.HANDGUN) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoordinates);
            Vector2 mousePos = new Vector2(worldCoordinates.x, worldCoordinates.y);

            float speed = 250f;  // set the speed of the bullet
            float shooterX = player.getItemPosition().x; // get player location
            float shooterY = player.getItemPosition().y; // get player location
            float velx = mousePos.x - shooterX; // get distance from shooter to target on x plain
            float vely = mousePos.y  - shooterY; // get distance from shooter to target on y plain
            float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
            if (length != 0) {
                velx = velx / length;  // get required x velocity to aim at target
                vely = vely / length;  // get required y velocity to aim at target
            }

            gameData.getWorldManager().createBullet(shooterX, shooterY, velx*speed, vely*speed);
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
