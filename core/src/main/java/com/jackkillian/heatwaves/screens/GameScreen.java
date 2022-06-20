package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.Constants;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;
import com.jackkillian.heatwaves.Player;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

public class GameScreen implements Screen, InputProcessor {
    private Engine engine;
    private HeatWaves game;
    private GameData gameData;
    private SpriteBatch batch;

    private OrthographicCamera camera;

    private FitViewport viewport;
    private World world;
    private Player player;

    public GameScreen(HeatWaves game) {
        this.game = game;

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);


        world = GameData.getInstance().getWorld();
        batch = GameData.getInstance().getBatch();
        viewport = GameData.getInstance().getViewport();
        camera = GameData.getInstance().getCamera();
        player = new Player(world, batch, 100, 100);


        engine = new Engine();
        MapRenderSystem mapRenderSystem = new MapRenderSystem();
        HudRenderSystem hudRenderSystem = new HudRenderSystem();
        ItemSystem itemSystem = new ItemSystem();
        engine.addSystem(mapRenderSystem);
        engine.addSystem(hudRenderSystem);
        engine.addSystem(itemSystem);
        GameData.getInstance().setMapRenderSystem(mapRenderSystem);
        GameData.getInstance().setHudRenderSystem(hudRenderSystem);
        GameData.getInstance().setItemSystem(itemSystem);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(48f / 255f, 86f / 255f, 99f / 255f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);


        engine.update(delta);
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
