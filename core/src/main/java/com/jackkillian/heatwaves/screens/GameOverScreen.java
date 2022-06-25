package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.jackkillian.heatwaves.Constants;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.HeatWaves;

import javax.swing.text.TabableView;

public class GameOverScreen implements Screen {
    private SpriteBatch spriteBatch;
    private Stage stage;
    private boolean won;

    public GameOverScreen(boolean won) {
        spriteBatch = new SpriteBatch();
        this.won = won;
    }

    @Override
    public void show() {
        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        Label label;
        if (won) {
            label = new Label("[SCARLET]You beat the game!\nIt's late night in the middle of June", GameData.getInstance().getSkin());

        } else {
            label = new Label("[SCARLET]Game over\nCouldn't beat the heat?", GameData.getInstance().getSkin());
        }
        label.setFontScale(2f);
        label.setAlignment(Align.center);
        table.add(label).center().pad(10f).row();

        Label scoreLabel = new Label("Score: " + GameData.getInstance().getScore(), GameData.getInstance().getSkin());
        scoreLabel.setFontScale(2f);
        table.add(scoreLabel).center().pad(10f).row();

        TextButton button = new TextButton("Play Again", GameData.getInstance().getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameData.getInstance().reset();

                Game game = (Game) Gdx.app.getApplicationListener();
                game.setScreen(new LoadingScreen((HeatWaves) game));
            }
        });
        table.add(button).center().width(200).pad(10f).row();
    }

    //TODO: make prettier
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);
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
