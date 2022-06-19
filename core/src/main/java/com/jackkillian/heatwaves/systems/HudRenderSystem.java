package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jackkillian.heatwaves.Assets;
import com.jackkillian.heatwaves.GameData;

public class HudRenderSystem extends EntitySystem {
    private Stage stage;
    private Table table;
    private OrthographicCamera camera;
    private Assets assets;

    private Image health;
    private Image shield;

    private Label healthLabel;
    private Label shieldLabel;

    public HudRenderSystem(GameData gameData) {
        camera = new OrthographicCamera();
        this.assets = gameData.getAssets();
        stage = new Stage();
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.top().left();

        health = new Image(assets.getManager().get("hud/health.png", Texture.class));
        health.setScale(2.5f);


        shield = new Image(assets.getManager().get("hud/shield.png", Texture.class));
        shield.setScale(2.5f);

        healthLabel = new Label("100", gameData.getSkin());
        shieldLabel = new Label("100", gameData.getSkin());


        healthLabel.setFontScale(2f);
        shieldLabel.setFontScale(2f);


        table.add(health).pad(30f);
        table.add(healthLabel).pad(10f);
        table.add(shield).pad(30f);
        table.add(shieldLabel).pad(10f);

    }

    public void update(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();
    }
}
