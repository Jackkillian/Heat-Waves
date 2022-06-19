package com.jackkillian.heatwaves;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jackkillian.heatwaves.screens.LoadingScreen;
import com.ray3k.stripe.FreeTypeSkin;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class HeatWaves extends Game {
    public Skin skin;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("Pixeld16/Pixeld16.json"));
//		skin.addRegions(new TextureAtlas(Gdx.files.internal("Pixeld16/Pixeld16.atlas")));

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