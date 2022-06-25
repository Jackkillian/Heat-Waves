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

public class Henchman extends NPC {
    private final Sprite gun;
    private float cooldownTimer;
    private final GameData gameData;

    public Henchman(float x, float y) {
        super(new Sprite(new Texture("player/henchman.png")), new Texture("player/henchmanDead.png"), x, y);

        health = 150;
        gameData = GameData.getInstance();
        cooldownTimer = 0f;

        gun = new Sprite(Item.getTexture(Item.ItemType.HANDGUN, true));
        gun.setPosition(body.getPosition().x - gun.getWidth() / 2, body.getPosition().y - gun.getHeight() / 2);
        gun.setScale(0.8f);
    }

    @Override
    public void update(float delta) {
        cooldownTimer += delta;
        Vector2 legs = gameData.getPlayer().getPosition().sub(body.getPosition());
        boolean isFlipped = legs.x < 0;

        drawSprite();

        // use trig again :D
        float angle = (float) Math.atan2(legs.y, legs.x);
        gun.setRotation(angle * MathUtils.radiansToDegrees);
        gun.setPosition(body.getPosition().x - gun.getWidth() / 2 + (sprite.isFlipX() ? -7 : 7), body.getPosition().y - gun.getHeight() / 2);
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
                velx = velx / length;  // get required x velocity to aim at target
                vely = vely / length;  // get required y velocity to aim at target
            }

            gameData.getWorldManager().createBullet(body.getPosition().x + 5, body.getPosition().y, velx * speed, vely * speed, Bullet.Origin.NPC);
        }

        super.update(delta);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        GameData.getInstance().setScore(GameData.getInstance().getScore() + 10);
    }

    @Override
    public void destroy() {
        super.destroy();
        gun.getTexture().dispose();
    }
}
