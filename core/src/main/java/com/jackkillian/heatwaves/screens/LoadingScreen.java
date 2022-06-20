package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.Assets;
import com.jackkillian.heatwaves.ContactListner;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;

public class LoadingScreen implements Screen {
    private final HeatWaves game;
    private final Assets assets;
    private Stage stage;
    private ProgressBar bar;
    private Label countLeftLabel;


    public LoadingScreen(HeatWaves game) {
        this.game = game;
        assets = new Assets();
        GameData gameData = GameData.getInstance();


        World world = new World(new Vector2(0, -80), false);
        world.setContactListener(new ContactListner());

        gameData.setWorld(world);

        gameData.setAssets(assets);
        gameData.setSkin(game.skin);
    }


    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // add widgets
        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
        logo.setScale(1.5f);
        logo.setAlign(Align.center);
        table.add(logo).padBottom(50).row();

        Label label = new Label("Loading resources...", game.skin);
        label.setAlignment(Align.center);
        table.add(label).pad(10).row();

        bar = new ProgressBar(0, 1, 0.01f, false, game.skin);
        bar.setValue(0);
        bar.setAnimateDuration(0.5f);
        table.add(bar).row();

        countLeftLabel = new Label("", game.skin);
        label.setAlignment(Align.center);
        table.add(countLeftLabel).pad(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (assets.getManager().update()) {
            game.setScreen(new MainMenuScreen(game));
        }

        countLeftLabel.setText((int) (assets.getManager().getProgress() * 100) + "%");
        bar.setValue(assets.getManager().getProgress());
        stage.act(delta);
        stage.draw();
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
