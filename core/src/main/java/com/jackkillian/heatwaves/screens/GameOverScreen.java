package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jackkillian.heatwaves.Constants;
import com.jackkillian.heatwaves.GameData;

public class GameOverScreen implements Screen {
    private SpriteBatch spriteBatch;

    public GameOverScreen(boolean won) {
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {

    }
    //TODO: make prettier
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        Constants.font.draw(spriteBatch, "Game Over", 400, 400);
        spriteBatch.end();

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
