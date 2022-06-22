package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable {
    public Sprite sprite;
    public Body body;
    public Vector2 position;
    public Vector2 velocity;
    public boolean alive;
    public boolean grapplingHook;
    public float bulletLifetime;

    /**
     * Bullet constructor. Just initialize variables.
     */
    public Bullet() {
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.alive = false;
        this.sprite = new Sprite(new Texture("bullet.png"));

        //create bullet body
        BodyDef bodyDef = new BodyDef();
//        bodyDef.bullet = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / Constants.PPM, position.y / Constants.PPM);
        body = GameData.getInstance().getWorld().createBody(bodyDef);

        //create bullet fixture
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / Constants.PPM);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.ITEM_BIT;
        fdef.filter.maskBits = Constants.WALL_BIT;
        body.createFixture(fdef);
        body.setGravityScale(0f);
        body.setUserData("bullet");


//        BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM);
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        this.body = GameData.getInstance().getWorld().createBody(bodyDef);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(3f / Constants.PPM, 3f / Constants.PPM);
//        fixtureDef.shape = shape;
//
//        fixtureDef.friction = 100f;
//        fixtureDef.restitution = 0.09f; // 0.09f
////        fixtureDef.filter.categoryBits = Constants.PLAYER_BIT;
////        fixtureDef.filter.maskBits = Constants.WALL_BIT;
//
//        body.createFixture(fixtureDef);
    }

    /**
     * Initialize the bullet. Call this method after getting a bullet from the pool.
     */
    public void init(float posX, float posY, float velX, float velY) {
        position.set(posX,  posY);
        velocity.set(velX, velY);
        alive = true;
        body.setUserData("bullet");
    }

    /**
     * Initialize the bullet. Call this method after getting a bullet from the pool.
     */
    public void init(float posX, float posY, float velX, float velY, boolean isGrapplingHook) {
        init(posX, posY, velX, velY);
        grapplingHook = isGrapplingHook;
        if (grapplingHook) {
            body.setUserData("grapplingHook");
            sprite.setTexture(new Texture("items/grapplingHook.png"));
        }
    }


    /**
     * Callback method when the object is freed. It is automatically called by Pool.free()
     * Must reset every meaningful field of this bullet.
     */
    @Override
    public void reset() {
        position.set(0, 0);
        alive = false;
        sprite = new Sprite(new Texture("bullet.png"));
        grapplingHook = false;
        bulletLifetime = 0;
        body.setTransform(position, 0);
    }

    /**
     * Method called each frame, which updates the bullet.
     */
    public void update (float delta) {
        bulletLifetime += delta;

        position.add(velocity.cpy().scl(delta));
        if (position.x != 0 && position.y != 0) body.setTransform(position.x, position.y, 0);
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);

        // calculate the angle with trigonometry! thanks geometry class :D
        float angle = (float) Math.atan(velocity.y/ velocity.x) * MathUtils.radiansToDegrees;
        if (velocity.x < 0) {
            angle += 180;
        }
        sprite.setRotation(angle);

        // Check bullet lifetime
        if (grapplingHook ? bulletLifetime > 1.5f : bulletLifetime > 0.5f) {
            alive = false;
            bulletLifetime = 0;
        }

        // Check if grappling hook hit a wall
        if (grapplingHook) {
            GameData.getInstance().setGrapplingPosition(body.getPosition());
            if (GameData.getInstance().isGrapplingHit()) {
                GameData.getInstance().setGrapplingPulling(true);
                alive = false;
            }
        }

//        // if bullet is out of screen, set it to dead
//        if (isOutOfScreen()) alive = false;

        // render sprite
        if (alive) {
            sprite.draw(GameData.getInstance().getBatch());
        } else {
            if (grapplingHook) {
                GameData.getInstance().setGrapplingHit(false);
                GameData.getInstance().setGrapplingShot(false);
            }
        }
    }

    private boolean isOutOfScreen() {
        float WIDTH = GameData.getInstance().getViewport().getWorldWidth();
        float HEIGHT = GameData.getInstance().getViewport().getWorldHeight();
        return position.x > WIDTH || position.x < 0 || position.y > HEIGHT || position.y < 0;
    }
}
