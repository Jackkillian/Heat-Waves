package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Villager extends NPC {
    private boolean movingRight = true;
    private boolean movingLeft;
    private final Animation<TextureRegion> animation;
    private float stateTime;

    public Villager(float x, float y) {
        super(new Sprite(new Texture("player/villager.png")), new Texture("player/villagerDead.png"), x, y);

        Texture runSheet = new Texture("player/villagerRunning.png");
        TextureRegion[] runFrames = TextureRegion.split(runSheet, 16, 16)[0];
        animation = new Animation<>(0.1f, runFrames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        if (health > 0) {
            int random = MathUtils.random(0, 100);
            int speed = MathUtils.random(1, 50);

            if (movingRight) {
                if (random > 95) {
                    movingRight = false;
                }
            }
            if (movingLeft) {
                if (random > 95) {
                    movingRight = true;
                }
            }
            movingLeft = !movingRight;

            if (movingRight) {
                body.setLinearVelocity(speed, body.getLinearVelocity().y);
                sprite.setFlip(false, false);
            } else {
                body.setLinearVelocity(-speed, body.getLinearVelocity().y);
                sprite.setFlip(true, false);
            }

            sprite.setRegion(animation.getKeyFrame(stateTime, true));
        } else {
            sprite.setRegion(deathTexture);
        }
        drawSprite();
        super.update(delta);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        GameData.getInstance().setScore(GameData.getInstance().getScore() - 10);
    }
}
