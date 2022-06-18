package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

    public HudRenderSystem(Assets assets) {
        this.assets = assets;
        stage = new Stage();
        table = new Table();
        camera = new OrthographicCamera();

        table.setFillParent(true);


        health = new Image(assets.getManager().get("hud/health.png", Texture.class));
        shield = new Image(assets.getManager().get("hud/shield.png", Texture.class));
        shield.setScale(4f);
        health.setScale(4f);



        table.top().left();
        table.add(health).pad(50f);
        table.add(shield).pad(10f);


        stage.addActor(table);
    }

    public void update(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();


    }
}
