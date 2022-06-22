package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

public class NPC {
    public Sprite sprite;
    public Body body;
    private SpriteBatch batch;
    public boolean alive;
    public int health;

    //hit damage marker
    private boolean isHit;
    private float tempTimer;
    private int damage;
    private float offset = 20f;



    public enum NPCType{
        MCMUFFIN_HENCHMAN,
        VILLAGER
    }

    public NPC(NPCType type, float x, float y) {
        health = 150;
        alive = true;
        World world = GameData.getInstance().getWorld();
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        batch = GameData.getInstance().getBatch();

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PPM, 15f / Constants.PPM);
        fdef.shape = shape;

//        CircleShape circleShape = new CircleShape();
//        circleShape.setRadius(11 / Constants.PPM);
//        fdef.shape = circleShape;

        fdef.friction = 100f;
        fdef.restitution = 0.09f; // 0.09f
        fdef.filter.categoryBits = Constants.PLAYER_BIT;
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.ITEM_BIT;
        body.createFixture(fdef);

        sprite = new Sprite(getTexture(type));
        body.setUserData(this);


    }

    private Texture getTexture(NPCType type) {
        Texture texture = null;
        switch (type) { // Can't use enhanced switch statements because of the HTML plugin...
            case MCMUFFIN_HENCHMAN:
                texture =  new Texture("player/henchman.png");
                break;
            default: texture = new Texture("player/villagerIdle1.png");

        }
        return texture;
    }

    public void update(float delta) {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y  - sprite.getHeight() / 2);
        sprite.draw(GameData.getInstance().getBatch());

        if (isHit) {
            offset += 0.2f;
            tempTimer += delta;
            Constants.font.draw(GameData.getInstance().getBatch(), "-" + damage, body.getPosition().x, body.getPosition().y + offset);

            if (tempTimer > 0.6f) {
                offset = 20;
                tempTimer = 0;
                isHit = false;
            }

        }
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
