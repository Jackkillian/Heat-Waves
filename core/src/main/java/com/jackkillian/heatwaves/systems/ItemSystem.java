package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.Item;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ItemSystem extends EntitySystem {
    public ArrayList<Item> items = new ArrayList<Item>();
    public ArrayList<Body> bodieToRemove = new ArrayList<Body>();


    private SpriteBatch batch;


    public ItemSystem(GameData gameData, SpriteBatch batch) {
        this.batch = batch;

    }

    public void update(float deltaTime) {
        if (items.size() < 15) {
            int x = ThreadLocalRandom.current().nextInt(100, 1500 + 1);
            items.add(new Item(Item.ItemType.HANDGUN, x, 700));
        }

        for (Body body : bodieToRemove) {
            GameData.getInstance().getWorld().destroyBody(body);
        }
        bodieToRemove.clear();


        batch.begin();
        for (Item item : items) {
            if (item.getBody().getPosition().y < 55) {
                item.getBody().setGravityScale(0f);
                item.getBody().setLinearVelocity(0, 0);
            }
            item.getSprite().setPosition(item.getBody().getPosition().x - item.getSprite().getWidth() / 2, item.getBody().getPosition().y - item.getSprite().getHeight() / 2);
            item.getSprite().draw(batch);
        }
        batch.end();
    }

    public void removeItem(Item item, Body body) {
        items.remove(item);
        bodieToRemove.add(body);
    }
}

