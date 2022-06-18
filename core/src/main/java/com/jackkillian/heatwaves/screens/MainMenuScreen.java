package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;

public class MainMenuScreen implements Screen {
    private final HeatWaves game;
    private final GameData gameData;
    private AssetManager assetManager;

    private SpriteBatch batch;
    private OrthographicCamera camera;



    public MainMenuScreen(HeatWaves game, GameData gameData) {
        this.game = game;
        this.gameData = gameData;
        batch = gameData.getBatch();

    }

    @Override
    public void show() {
        game.setScreen(new GameScreen(game, gameData));
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
}

