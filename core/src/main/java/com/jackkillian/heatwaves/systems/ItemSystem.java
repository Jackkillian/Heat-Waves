package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.jackkillian.heatwaves.GameData;
import com.jackkillian.heatwaves.Item;

import java.util.ArrayList;

public class ItemSystem extends EntitySystem {
    public ArrayList<Item> items = new ArrayList<Item>();
    public ArrayList<Body> bodieToRemove = new ArrayList<Body>();

    private ArrayList<Item> itemsToRemove = new ArrayList<Item>();


    private SpriteBatch batch;


    public ItemSystem(GameData gameData, SpriteBatch batch) {
        this.batch = batch;

    }

    public void update(float deltaTime) {
        System.out.println("debug item start");
        if (items.size() < 9) {
            // generate random number between 100 and 1450
            //actually it's fine to go over 1.5k
            int x = MathUtils.random(50, 2850);
            int y = 700;
            // choose random item from enum Item.ItemType
            Item.ItemType itemType = Item.ItemType.values()[(int) (Math.random() * Item.ItemType.values().length)];
            if (!GameData.getInstance().getWorld().isLocked()) {
                Item item = new Item(itemType, x, y);
                items.add(item);

            }
        }

        boolean canClear = true;
        for (Body body : bodieToRemove) {
            if (GameData.getInstance().getWorld().isLocked()) {
                canClear = false;
                System.out.println("cannot clear");

            } else {
                GameData.getInstance().getWorld().destroyBody(body);

            }

        }
        if (canClear) bodieToRemove.clear();

        for (Item item : itemsToRemove) {
            items.remove(item);
        }
        itemsToRemove.clear();



        batch.begin();
        for (Item item : items) {
            item.update(deltaTime);
            item.getSprite().setPosition(item.getBody().getPosition().x - item.getSprite().getWidth() / 2, item.getBody().getPosition().y - item.getSprite().getHeight() / 2);
            item.getSprite().draw(batch);
        }
        batch.end();

        System.out.println("debug item end");
    }

    public void removeItem(Item item, Body body) {
        itemsToRemove.add(item);
        bodieToRemove.add(body);
    }
    public void removeBody(Body body) {
        bodieToRemove.add(body);
    }
}

