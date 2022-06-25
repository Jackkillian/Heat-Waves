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
    protected Texture deathTexture;
    protected Body body;
    protected float deathTimer;
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
        this.deathTexture = deathTexture;


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

//        sprite = new Sprite(getTexture(type));
//        if (type == NPCType.MCMUFFIN_HENCHMAN) {
//            gun = new Sprite(Item.getTexture(Item.ItemType.SHOTGUN, true));
//            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
//        } else {
//            gun = new Sprite(Item.getTexture(Item.ItemType.HANDGUN, true));
//            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
//        }
//
//        if (type == NPCType.VILLAGER) {
//            death = new TextureRegion(new Texture("player/villagerDead.png"));
//        } else {
//            death =
//        }
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
            sprite.setTexture(deathTexture);
            if (deathTimer < 0) {
                GameData.getInstance().getEventHandler().addKill();
                alive = false;
            }
        }
    };

    public void destroy() {
//        death.getTexture().dispose();
//        sprite.getTexture().dispose();
//        gun.getTexture().dispose();
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
