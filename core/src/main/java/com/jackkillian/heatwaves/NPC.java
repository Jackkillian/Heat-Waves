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
    public Sprite sprite;
    public Body body;
    private SpriteBatch batch;
    public boolean alive;
    public int health;
    private Sprite gun;
    private GameData gameData;
    private float cooldownTimer;
    private boolean isFlipped = false;

    private TextureRegion death;
    private float deathTimer = 2f;

    //hit damage marker
    private boolean isHit;
    private float tempTimer;
    private int damage;
    private float offset = 20f;


    public enum NPCType {
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
        gameData = GameData.getInstance();
        cooldownTimer = 0f;

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
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.BULLET_BIT;
        body.createFixture(fdef);

        sprite = new Sprite(getTexture(type));
        if (type == NPCType.MCMUFFIN_HENCHMAN) {
            gun = new Sprite(Item.getTexture(Item.ItemType.SHOTGUN, true));
            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        } else {
            gun = new Sprite(Item.getTexture(Item.ItemType.HANDGUN, true));
            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        }

        if (type == NPCType.VILLAGER) {
            death = new TextureRegion(new Texture("player/villagerDead.png"));
        } else {
            death = new TextureRegion(new Texture("player/henchmanDead.png"));
        }
        body.setUserData(this);
        gun.setScale(0.8f);
    }

    private Texture getTexture(NPCType type) {
        Texture texture = null;
        switch (type) { // Can't use enhanced switch statements because of the HTML plugin...
            case MCMUFFIN_HENCHMAN:
                texture = new Texture("player/henchman.png");
                break;
            default:
                texture = new Texture("player/villagerIdle1.png");
                break;
        }
        return texture;
    }

    public void update(float delta) {
        cooldownTimer += delta;

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

        Vector2 legs = gameData.getPlayer().getPosition().sub(body.getPosition());
        isFlipped = legs.x < 0;


        // use trig again :D
        float angle =  (float) Math.atan2(legs.y, legs.x);
        gun.setRotation(angle * MathUtils.radiansToDegrees);

        //my signature coding "(isFlipped? 7: -7)"
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        gun.setPosition(body.getPosition().x - gun.getWidth() / 2 + (sprite.isFlipX() ? -7 : 7), body.getPosition().y - gun.getHeight() / 2);
        sprite.draw(gameData.getBatch());
        if (health > 0) gun.draw(gameData.getBatch());


        // AI shoot player if in range
        float distance = legs.len();
        if (distance < 300f && health > 0) {
            //setting a body's transform may cause freezes
            //https://neutroniogames.blogspot.com/2016/05/things-that-make-your-box2d-world-freeze.html
            if (Math.abs(body.getLinearVelocity().x) < 15f) {
                body.setLinearVelocity(isFlipped ? -20f : 20f, 0);
            }

            if (gameData.getPlayer().getPosition().x > body.getPosition().x) {
                sprite.setFlip(false, false);
                gun.setFlip(false, false);
            } else {
                sprite.setFlip(true, false);
                if (gameData.getPlayer().getPosition().y > body.getPosition().y) {
                    gun.setFlip(false, true);
                } else {
                    if (!gun.isFlipY()) gun.setFlip(false, true);
                }
            }

        }

        if (distance < 100f && health > 0) {
            if (cooldownTimer > 0.6f) {
                cooldownTimer = 0f;
            } else {
                return;
            }
            float speed = 450f;  // set the speed of the bullet
            float shooterX = gameData.getPlayer().getPosition().x; // get player location
            float shooterY = gameData.getPlayer().getPosition().y; // get player location
            float velx = shooterX - body.getPosition().x; // get distance from shooter to target on x plain
            float vely = shooterY - body.getPosition().y; // get distance from shooter to target on y plain
            float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
            if (length != 0) {
                velx = velx / length ;  // get required x velocity to aim at target
                vely = vely / length ;  // get required y velocity to aim at target
            }

            gameData.getWorldManager().createBullet(body.getPosition().x + 5, body.getPosition().y, velx * speed, vely * speed, Bullet.Origin.NPC);
        }


        if (health <= 0) {
            deathTimer -= delta;
            sprite.setRegion(death);
            if (deathTimer < 0) {
                GameData.getInstance().getEventHandler().addKill();
                alive = false;
            }
        }
    }

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
