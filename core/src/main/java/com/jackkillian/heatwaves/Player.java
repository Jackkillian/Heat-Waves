package com.jackkillian.heatwaves;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.jackkillian.heatwaves.Constants.SPAWN_X;
import static com.jackkillian.heatwaves.Constants.SPAWN_Y;

//TODO: Make the player's right hand move with the mouse.
//TODO: also add shooting and item generation :D

public class Player {
    private final Body body;
    private final SpriteBatch batch;
    private final Sprite sprite;
    private final Sprite jumpSprite;
    private final Animation<TextureRegion> runningAnimation;
    private float stateTime = 0f;

    // Keys
    private boolean keyLeftPressed = false;
    private boolean keyRightPressed = false;
    private boolean keyUpPressed = false;
    private boolean keyDownPressed = false;

    boolean isFlipped = false;
    private boolean isJumping = false;
    private boolean isFalling = false;
    private boolean isRunning = true;
    private boolean canJump = true;

    public Player(World world, SpriteBatch batch) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(SPAWN_X / Constants.PPM , SPAWN_Y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        this.batch = batch;

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PPM, 15f / Constants.PPM);
        fdef.shape = shape;

//        CircleShape circleShape = new CircleShape();
//        circleShape.setRadius(11 / Constants.PPM);
//        fdef.shape = circleShape;

        fdef.friction = 100f;
        fdef.restitution = 0.09f;
        body.createFixture(fdef);
        body.setUserData(this);

        //maybe we don't need asset manager for this game
        sprite = new Sprite(new Texture("player/player1.png"));
        jumpSprite = new Sprite(new Texture("player/jump.png"));

        Texture runSheet = new Texture("player/running.png");
        TextureRegion[] runFrames = TextureRegion.split(runSheet, 16, 16)[0];
        runningAnimation = new Animation<>(0.1f, runFrames);
        runningAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;

        isJumping = body.getLinearVelocity().y > 0.15; // When on ground, the velocity is still 0.11...
        isFalling = body.getLinearVelocity().y < 0;
        isRunning = !isFalling && !isJumping && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0);
        isFlipped = body.getLinearVelocity().x < 0;
        canJump = GameData.getInstance().isTouchingPlatform() || (!isJumping && !isFalling);

        if (keyLeftPressed) {
            body.setLinearVelocity(-70, body.getLinearVelocity().y);
        }
        if (keyRightPressed) {
            body.setLinearVelocity(70, body.getLinearVelocity().y);
        }
        if (keyUpPressed && canJump) {
            body.setLinearVelocity(body.getLinearVelocity().x, 70);
        }

//        if (!keyLeftPressed && !keyRightPressed && keyUpPressed) {
//            body.setLinearVelocity(0, body.getLinearVelocity().y);
//        }

        batch.begin();
        if (isRunning) {
            TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime, true);
            if (isFlipped && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else {
                currentFrame.flip(!isFlipped && currentFrame.isFlipX(), false);
            }
            batch.draw(currentFrame, body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2 + 1);
        } else if (isJumping || isFalling) {
            jumpSprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2 + 1);
            if (isFlipped && !jumpSprite.isFlipX()) {
                jumpSprite.flip(true, false);
            } else {
                jumpSprite.flip(!isFlipped && jumpSprite.isFlipX(), false);
            }
            jumpSprite.draw(batch);
        } else {
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2 + 1);
            if (isFlipped && !sprite.isFlipX()) {
                sprite.flip(true, false);
            } else {
                sprite.flip(!isFlipped && sprite.isFlipX(), false);
            }
            sprite.draw(batch);
        }
        batch.end();

        if (body.getPosition().y < -100) {
            body.setTransform(SPAWN_X / Constants.PPM , SPAWN_Y / Constants.PPM, 50 * MathUtils.degreesToRadians);
        }
    }

    public void onKeyDown(int key) {
        if (key == Input.Keys.LEFT) {
            keyLeftPressed = true;
        }
        if (key == Input.Keys.RIGHT) {
            keyRightPressed = true;
        }
        if (key == Input.Keys.UP) {
            keyUpPressed = true;
        }
        if (key == Input.Keys.DOWN) {
            keyDownPressed = true;
        }
    }

    public void onKeyUp(int key) {
        if (key == Input.Keys.LEFT) {
            keyLeftPressed = false;
        }
        if (key == Input.Keys.RIGHT) {
            keyRightPressed = false;
        }
        if (key == Input.Keys.UP) {
            keyUpPressed = false;
        }
        if (key == Input.Keys.DOWN) {
            keyDownPressed = false;
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
