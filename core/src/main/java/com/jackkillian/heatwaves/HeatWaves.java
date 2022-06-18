package com.jackkillian.heatwaves;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jackkillian.heatwaves.screens.LoadingScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class HeatWaves extends Game {

	@Override
	public void create() {
		setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {
		super.render();

	}

	@Override
	public void dispose() {

	}
}