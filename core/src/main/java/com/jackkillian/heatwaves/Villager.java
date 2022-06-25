package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Villager extends NPC {
    public Villager(float x, float y) {
        super(new Sprite(new Texture("player/villagerIdle1.png")), new Texture("player/henchmanDead.png"), x, y);
    }

    @Override
    public void update(float delta) {
        body.setLinearVelocity(new Vector2(MathUtils.random(-100, 100), 0));
        super.update(delta);
    }
}
