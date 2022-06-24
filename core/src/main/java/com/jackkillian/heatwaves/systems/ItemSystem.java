package com.jackkillian.heatwaves.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        if (items.size() < 15) {
            // generate random number between 100 and 1450
            //don't go over 1.5k
            int x = (int) (Math.random() * (1450 - 100) + 100);
            int y = 900;
            // choose random item from enum Item.ItemType
            Item.ItemType itemType = Item.ItemType.values()[(int) (Math.random() * Item.ItemType.values().length)];
            if (GameData.getInstance().getWorld().isLocked() == false) {
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
    }

    public void removeItem(Item item, Body body) {
        itemsToRemove.add(item);
        bodieToRemove.add(body);
    }
    public void removeBody(Body body) {
        bodieToRemove.add(body);
    }
}

