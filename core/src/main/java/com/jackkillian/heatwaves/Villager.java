package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Villager extends NPC {
    public Villager(float x, float y) {
        super(new Sprite(new Texture("player/villager.png")), new Texture("player/villagerDead.png"), x, y);
    }

    @Override
    public void update(float delta) {
        if (alive) body.setLinearVelocity(new Vector2(MathUtils.random(-100, 100), body.getLinearVelocity().y));
        drawSprite();
        super.update(delta);
    }
}
