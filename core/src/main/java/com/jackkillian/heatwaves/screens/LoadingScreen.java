package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.Assets;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;

public class LoadingScreen implements Screen {
    private final HeatWaves game;
    private final Assets assets;
    private final GameData gameData;


    public LoadingScreen(HeatWaves game) {
        this.game = game;
        assets = new Assets();
        gameData = new GameData();

        SpriteBatch batch = new SpriteBatch();
        FitViewport viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        gameData.setBatch(batch);
        gameData.setViewport(viewport);
        gameData.setAssets(assets);


    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (assets.getManager().update()) {
            game.setScreen(new MainMenuScreen(game, gameData));
        }

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
        assets.getManager().dispose();
    }
}
