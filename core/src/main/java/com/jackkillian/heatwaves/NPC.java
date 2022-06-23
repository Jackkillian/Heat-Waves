package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.ITEM_BIT;
        body.createFixture(fdef);

        sprite = new Sprite(getTexture(type));
        if (type == NPCType.MCMUFFIN_HENCHMAN) {
            gun = new Sprite(Item.getTexture(Item.ItemType.HANDGUN, true));
            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        } else {
            gun = new Sprite(Item.getTexture(Item.ItemType.PISTOL, true));
            gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        }
        body.setUserData(this);
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

        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        sprite.draw(GameData.getInstance().getBatch());
        gun.draw(GameData.getInstance().getBatch());

        // AI shoot player if in range
        float distance = GameData.getInstance().getPlayer().getPosition().sub(body.getPosition()).len();
        if (distance < 300f) {
            if (GameData.getInstance().getPlayer().getPosition().x > body.getPosition().x) {
                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                    gun.flip(true, false);
                }
                body.setTransform(body.getPosition().x + 10 * delta, body.getPosition().y, 0);
            } else {
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                    gun.flip(true, false);
                }
                body.setTransform(body.getPosition().x - 10 * delta, body.getPosition().y, 0);
            }
            if (GameData.getInstance().getPlayer().getPosition().y > body.getPosition().y) {
                body.setTransform(body.getPosition().x, body.getPosition().y + 10 * delta, 0);
            } else {
                body.setTransform(body.getPosition().x, body.getPosition().y - 10 * delta, 0);
            }
        }
        if (distance < 100f) {
            if (cooldownTimer > 0.7f) {
                cooldownTimer = 0f;
            } else {
                return;
            }
            float speed = 300f;  // set the speed of the bullet
            float shooterX = gameData.getPlayer().getPosition().x; // get player location
            float shooterY = gameData.getPlayer().getPosition().y; // get player location
            float velx = shooterX - sprite.getX(); // get distance from shooter to target on x plain
            float vely = shooterY - sprite.getY(); // get distance from shooter to target on y plain
            float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
            if (length != 0) {
                velx = velx / length * 1.5f;  // get required x velocity to aim at target
                vely = vely / length * 1.5f;  // get required y velocity to aim at target
            }

            gameData.getWorldManager().createBullet(sprite.getX(), sprite.getY(), velx * speed, vely * speed, Bullet.Origin.NPC);
        }


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

        if (health <= 0) {
            alive = false;
            GameData.getInstance().getWorld().destroyBody(body);
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
