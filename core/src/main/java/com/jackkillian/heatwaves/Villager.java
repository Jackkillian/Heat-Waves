package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Villager extends NPC {
    public Villager(float x, float y) {
        super(new Sprite(new Texture("player/villagerIdle1.png")), new Texture("player/henchmanDead.png"), x, y);
    }

    @Override
    public void update(float delta) {
        body.setLinearVelocity(new Vector2(MathUtils.random(-100, 100), 0));
        drawSprite();
        super.update(delta);
    }
}
