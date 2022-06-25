package com.jackkillian.heatwaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import org.w3c.dom.Text;

public class Bullet implements Pool.Poolable {
    public Sprite sprite;
    public Body body;
    public Vector2 position;
    public Vector2 velocity;
    public boolean alive;
    public boolean grapplingHook;
    public float bulletLifetime;
    public Origin origin;

    private Texture shotgunShell = new Texture("shotgunShell.png");
    private Texture bullet = new Texture("bullet.png");
    private Texture grapple = new Texture("items/grapplingHook.png");

    public enum Origin {
        PLAYER,
        NPC
    }

    /**
     * Bullet constructor. Just initialize variables.
     */
    public Bullet() {
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.alive = false;
        sprite = new Sprite(bullet);


        //create bullet body
        BodyDef bodyDef = new BodyDef();
//        bodyDef.bullet = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        bodyDef.position.set(position.x / Constants.PPM, position.y / Constants.PPM);
        body = GameData.getInstance().getWorld().createBody(bodyDef);

        //create bullet fixture
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / Constants.PPM);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.BULLET_BIT;
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.PLAYER_BIT;
        body.createFixture(fdef);
        body.setGravityScale(0f);
        body.setUserData(this);


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
        position.set(posX, posY);
        velocity.set(velX, velY);
        body.setTransform(posX, posY, 0);
        alive = true;
        body.setUserData(this);

        if (this.origin == Origin.NPC) {
            sprite.setTexture(bullet);
        } else if (GameData.getInstance().getHeldItemType() == Item.ItemType.SHOTGUN) {
            sprite.setTexture(shotgunShell);
        } else if (GameData.getInstance().getHeldItemType() == Item.ItemType.HANDGUN) {
            sprite.setTexture(bullet);
        }
    }

    public void init(float posX, float posY, float velX, float velY, Origin origin) {
        init(posX, posY, velX, velY);
        this.origin = origin;
    }

    /**
     * Initialize the bullet. Call this method after getting a bullet from the pool.
     */
    public void init(float posX, float posY, float velX, float velY, boolean isGrapplingHook) {
        init(posX, posY, velX, velY);
        grapplingHook = isGrapplingHook;
        if (grapplingHook) {
            body.setUserData("grapplingHook");
            sprite.setTexture(grapple);
        }
    }


    /**
     * Callback method when the object is freed. It is automatically called by Pool.free()
     * Must reset every meaningful field of this bullet.
     */
    @Override
    public void reset() {
        float randomX = MathUtils.random(500, 800);
        float randomY = MathUtils.random(500, 800);

        // multiple bodies at same position may cause slow performance
        position.set(randomX, randomY);
        alive = false;
        sprite.setRegion(bullet);
        grapplingHook = false;
        bulletLifetime = 0;
        body.setTransform(position, 0);
        body.setLinearVelocity(0, 0);
    }

    /**
     * Method called each frame, which updates the bullet.
     */
    public void update(float delta) {
        bulletLifetime += delta;

        position.add(velocity.cpy().scl(delta * 80f));
        body.applyForce(velocity.cpy().scl(delta * 80f), body.getWorldCenter(), true);

//        if (position.x != 0 && position.y != 0) body.setTransform(position.x, position.y, 0);
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);

        // calculate the angle with trigonometry! thanks geometry class :D - JackFromHappuPlanet
        float angle = (float) Math.atan(velocity.y / velocity.x) * MathUtils.radiansToDegrees;
        if (velocity.x < 0) {
            angle += 180;
        }
        sprite.setRotation(angle);

        // Check bullet lifetime
        if (grapplingHook ? bulletLifetime > 1.5f : bulletLifetime > 1f) {
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
