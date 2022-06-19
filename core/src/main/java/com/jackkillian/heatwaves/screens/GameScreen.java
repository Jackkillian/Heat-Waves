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
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;
import com.jackkillian.heatwaves.Player;
import com.jackkillian.heatwaves.systems.HudRenderSystem;

public class GameScreen implements Screen, InputProcessor {
    private final Engine engine;
    private GameData gameData;
    private SpriteBatch batch;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Player player;

    public GameScreen(HeatWaves game, GameData gameData) {
        Gdx.input.setInputProcessor(this);
        this.gameData = gameData;

        engine = new Engine();
        engine.addSystem(new HudRenderSystem(gameData.getAssets()));
    }

    @Override
    public void show() {
        Box2D.init();
        map = new TmxMapLoader().load("gameMap.tmx");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        batch = new SpriteBatch();
        renderer = new OrthogonalTiledMapRenderer(map);
        world = new World(new Vector2(0, -10), true);
        player = new Player(world, batch, 100, 100);

        for (MapObject object : map.getLayers().get("building").getObjects()) {
            if (object instanceof RectangleMapObject rectangleObject) {
                Rectangle rectangle = rectangleObject.getRectangle();

                // Create the body, shape, and fixture
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                float x = rectangle.getX() + rectangle.getWidth() / 2;
                float y = rectangle.getY() + rectangle.getHeight() / 2;
                bodyDef.position.set(x, y);
                Body body = world.createBody(bodyDef);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rectangle.width / 2, rectangle.height / 2);
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);
        engine.update(delta);
        player.update();

        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {

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
