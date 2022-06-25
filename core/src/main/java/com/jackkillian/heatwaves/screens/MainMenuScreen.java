package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;

public class MainMenuScreen implements Screen {
    private final HeatWaves game;
    private final GameData gameData;
    private AssetManager assetManager;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    public MainMenuScreen(HeatWaves game) {
        this.game = game;
        this.gameData = GameData.getInstance();
        batch = gameData.getBatch();
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

        Label instructions = new Label(
                "[GOLD]Objective[WHITE]\n\n" +

                "[SKY]Get as many points as you can.[WHITE]\n" +
                "Shooting henchman [LIME]gives[WHITE] you points,\n" +
                "and shooting innocent villagers [SCARLET]loses[WHITE] you points.\n\n\n" +


                "[GOLD]Controls[WHITE]\n\n" +

                "[CYAN]Move[WHITE] - [PINK]WAD\n" +
                "[CYAN]Aim[WHITE] - [PINK]Move cursor\n" +
                "[CYAN]Use item[WHITE] - [PINK]Click", game.skin);
        instructions.setAlignment(Align.center);
        table.add(instructions).pad(10).row();

        TextButton playButton = new TextButton("Play", game.skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen());
            }
        });
        table.add(playButton).width(200).pad(10).row();

        TextButton creditsButton = new TextButton("Credits", game.skin);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CreditsScreen(game));
            }
        });
        table.add(creditsButton).width(200).pad(10).row();

        TextButton quitButton = new TextButton("Quit", game.skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(quitButton).width(200).pad(10).row();
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
        stage.getViewport().update(width, height, true);
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

