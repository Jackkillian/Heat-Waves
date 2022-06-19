package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.jackkillian.heatwaves.HeatWaves;

public class CreditsScreen extends InputAdapter implements Screen {
    public HeatWaves game;
    private Stage stage;

    public CreditsScreen(HeatWaves game) {
        this.game = game;
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

        Label label = new Label("Created by", game.skin);
        label.scaleBy(2);
        label.setAlignment(Align.center);
        table.add(label).pad(10).row();

        Label dave = new Label("DaveH355 and", game.skin);
        dave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://github.com/DaveH355");
            }
        });
        dave.setAlignment(Align.center);
        table.add(dave).pad(10).row();

        Label jackkillian = new Label("Jackkillian", game.skin);
        jackkillian.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://github.com/Jackkillian");
            }
        });
        jackkillian.setAlignment(Align.center);
        table.add(jackkillian).padTop(10).padBottom(25).row();

        TextButton backButton = new TextButton("Back", game.skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        table.add(backButton).width(200).pad(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    }

}
