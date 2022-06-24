package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jackkillian.heatwaves.Assets;
import com.jackkillian.heatwaves.EventHandler;
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

    private Label heatWavesTimer;
    private Label required;


    private Image hotbar;
    private Image activeItem;

    private GameData gameData;

    public HudRenderSystem(GameData gameData) {
        this.gameData = gameData;
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

        hotbar = new Image(assets.getManager().get("hud/inventory.png", Texture.class));
        hotbar.setScale(5f);

        activeItem = new Image();
        activeItem.setScale(5f);


        healthLabel = new Label("100", gameData.getSkin());
        shieldLabel = new Label("100", gameData.getSkin());
        heatWavesTimer = new Label("", gameData.getSkin());
        required = new Label("", gameData.getSkin());
        required.setFontScale(2f);
        heatWavesTimer.setFontScale(2.5f);

        healthLabel.setFontScale(2f);
        shieldLabel.setFontScale(2f);


        table.add(health).pad(30f);
        table.add(healthLabel).pad(10f);
        table.add(shield).pad(30f);
        table.add(shieldLabel).pad(10f);
        table.row();

        Stack stack = new Stack();
        stack.add(hotbar);
        stack.add(activeItem);
        table.add(stack).padTop(70f);

        Table tableRight = new Table();
        tableRight.setFillParent(true);
        tableRight.top().right();
        tableRight.add(heatWavesTimer).pad(10f);
        tableRight.row();
        tableRight.add(required);
        stage.addActor(tableRight);

        Table bottomRight = new Table();
        bottomRight.setFillParent(true);
        bottomRight.bottom().right();
        stage.addActor(bottomRight);
    }

    public void update(float deltaTime) {
        if (gameData.getEventHandler().isEventActive()) {
            required.setText("Kills Required: " + gameData.getEventHandler().getRequiredKills());
        } else {
            required.setText("");
        }

        heatWavesTimer.setText(gameData.getEventHandler().getEventString() + (int) gameData.getEventHandler().getCountdown());
        healthLabel.setText(GameData.getInstance().getPlayerHealth());
        shieldLabel.setText(GameData.getInstance().getPlayerShield());
        stage.act(deltaTime);
        stage.draw();
    }

    public void setActiveItem(Texture texture) {
        if (texture == null) {
            activeItem.setDrawable(null);
            return;
        }
        activeItem.setDrawable(new TextureRegionDrawable(texture));
    }

}
