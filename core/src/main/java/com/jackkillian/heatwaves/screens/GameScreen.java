package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;
import com.jackkillian.heatwaves.Player;
import com.jackkillian.heatwaves.systems.HudRenderSystem;

public class GameScreen implements Screen {
    private final Engine engine;
    private GameData gameData;

    public GameScreen(HeatWaves game, GameData gameData) {
        this.gameData = gameData;

        engine = new Engine();
        engine.addSystem(new HudRenderSystem(gameData.getAssets()));

        Player player = new Player();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(delta);

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
