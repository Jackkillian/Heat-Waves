package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

public class NPC {
    protected Sprite sprite;
    protected TextureRegion deathTexture;
    protected Body body;
    protected float deathTimer = 2f;
    public boolean alive;
    public int health;

    // Hit damage marker
    private boolean isHit;
    private float tempTimer;
    private int damage;
    private float offset = 20f;

    public NPC(Sprite sprite, Texture deathTexture, float x, float y) {
        this.health = 150;
        this.alive = true;
        this.sprite = sprite;
        this.deathTexture = new TextureRegion(deathTexture);

        World world = GameData.getInstance().getWorld();
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PPM, 15f / Constants.PPM);
        fdef.shape = shape;
        fdef.friction = 100f;
        fdef.restitution = 0.09f; // 0.09f
        fdef.filter.categoryBits = Constants.PLAYER_BIT;
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.BULLET_BIT;
        body.createFixture(fdef);
        body.setUserData(this);
    }

    public void update(float delta) {
        if (isHit) {
            offset += 0.2f;
            tempTimer += delta;
            Constants.font.draw(GameData.getInstance().getBatch(), "-" + damage, body.getPosition().x, body.getPosition().y + offset);

            if (tempTimer > 0.5f) {
                offset = 20;
                tempTimer = 0;
                isHit = false;
            }
        }

        if (health <= 0) {
            deathTimer -= delta;
            sprite.setRegion(deathTexture);
            if (deathTimer < 0) {
                alive = false;
            }
        }
    };

    protected void drawSprite() {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(GameData.getInstance().getBatch());
    }

    public void destroy() {
        deathTexture.getTexture().dispose();
        sprite.getTexture().dispose();
        body.setUserData(null);
        GameData.getInstance().getItemSystem().removeBody(body);
    }

    public void hit(int damage) {
        if (isHit) {
            offset = 20;
            tempTimer = 0;
        }
        isHit = true;
        this.damage = damage;
        health -= damage;
    }
}
