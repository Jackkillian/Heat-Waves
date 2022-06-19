package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.Constants;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;
import com.jackkillian.heatwaves.Player;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

import java.awt.*;

public class GameScreen implements Screen, InputProcessor {
    private final Engine engine;
    private GameData gameData;
    private SpriteBatch batch;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private FitViewport viewport;
    private World world;
    private Player player;

    public GameScreen(HeatWaves game, GameData gameData) {
        Gdx.input.setInputProcessor(this);
        this.gameData = gameData;
        world = gameData.getWorld();

        batch = new SpriteBatch();


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        viewport = new FitViewport(Gdx.graphics.getWidth() / Constants.PPM, Gdx.graphics.getHeight() / Constants.PPM, camera);
        gameData.setViewport(viewport);

        engine = new Engine();
        engine.addSystem(new MapRenderSystem(gameData, camera));
        engine.addSystem(new HudRenderSystem(gameData));
    }

    @Override
    public void show() {


        player = new Player(world, batch, 100, 100);

    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(48f/255f, 86f/255f, 99f/255f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        engine.update(delta);
        camera.position.set(player.getPosition(), 0);
        player.update();

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
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
