package com.jackkillian.heatwaves;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.jackkillian.heatwaves.screens.GameScreen;

import static com.jackkillian.heatwaves.Constants.*;

//TODO: Fix the bug where the player moves slower in fullscreen mode

public class Player {
    private final Body body;
    private final SpriteBatch batch;
    private final Sprite idleSprite;
    private final Sprite jumpSprite;
    private final Animation<TextureRegion> runningAnimation;
    private final TextureRegion idle;
    private float stateTime = 0f;
    private final Body itemBody;
    private Item.ItemType itemType;
    private final Sprite itemSprite;


    //mouse vectors set here to improve performance
    private final Vector2 direction = new Vector2();
    private final Vector3 worldCoordinates = new Vector3();
    private final Vector2 centerPosition = new Vector2();
    private final Vector2 mouseLoc = new Vector2();
    private float angle;

    // Keys
    private boolean keyLeftPressed = false;
    private boolean keyRightPressed = false;
    private boolean keyUpPressed = false;
    private boolean keyDownPressed = false;

    boolean isFlipped = false;

    private boolean shouldRespawn = false;

    //hit damage marker
    private boolean isHit;
    private float tempTimer;
    private int damage;
    private float offset = 20f;
    private Color color = Color.BLUE;


    public Player(WorldManager worldManager, SpriteBatch batch) {
        World world = worldManager.getWorld();

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM);
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
        fdef.restitution = 0.09f; // 0.09f
        fdef.filter.categoryBits = Constants.PLAYER_BIT;
        fdef.filter.maskBits = WALL_BIT | ITEM_BIT | BULLET_BIT;
        body.createFixture(fdef);
        body.setUserData(this);

        // CREATE ACTIVE HAND FOR PLAYER TO USE ITEMS
        shape.setAsBox(15f / Constants.PPM, 22.5f / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM);
        itemBody = world.createBody(bodyDef);

        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.PLAYER_BIT;
        fdef.filter.maskBits = Constants.ITEM_BIT;
        itemBody.createFixture(fdef);
        itemBody.setUserData(this);

        itemBody.getWorldCenter().set(body.getWorldCenter());

        idle = new TextureRegion(new Texture("player/player1.png"));
        idleSprite = new Sprite(new Texture("player/player1.png"));
        idleSprite.setOrigin(idleSprite.getWidth() / 2, idleSprite.getHeight() / 2);
        jumpSprite = new Sprite(new Texture("player/jump.png"));
        itemSprite = new Sprite();

        Texture runSheet = new Texture("player/running.png");
        TextureRegion[] runFrames = TextureRegion.split(runSheet, 16, 16)[0];
        runningAnimation = new Animation<>(0.1f, runFrames);
        runningAnimation.setPlayMode(Animation.PlayMode.LOOP);


    }

    public void update(float delta) {
        stateTime += delta;

        boolean isJumping = body.getLinearVelocity().y > 0.26; // When on ground, the velocity is still 0.11...
        boolean isFalling = body.getLinearVelocity().y < 0;
        boolean isRunning = !isFalling && !isJumping && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0);
        isFlipped = angle > 100 && angle < 266;
        boolean canJump = GameData.getInstance().isTouchingPlatform();

        if (itemType != GameData.getInstance().getHeldItemType() || itemSprite.getTexture() != Item.getTexture(itemType, true)) {
            itemType = GameData.getInstance().getHeldItemType();

            if (itemType == null) {
                itemSprite.setTexture(null);
                return;
            }

            itemSprite.set(new Sprite(Item.getTexture(itemType, true)));
            itemSprite.setScale(0.8f);
            itemSprite.setOrigin(idleSprite.getOriginX(), idleSprite.getOriginY());
        }

        if (keyLeftPressed) {
            if (canJump) {
                body.setLinearVelocity(-80, body.getLinearVelocity().y);
            } else {
                body.setLinearVelocity(-60, body.getLinearVelocity().y);
            }
        }
        if (keyRightPressed) {
            if (canJump) {
                body.setLinearVelocity(80, body.getLinearVelocity().y);
            } else {
                body.setLinearVelocity(60, body.getLinearVelocity().y);
            }
        }
        if (keyUpPressed && canJump) {
            body.setLinearVelocity(body.getLinearVelocity().x, 60);
        }

//        if (!keyLeftPressed && !keyRightPressed && keyUpPressed) {
//            body.setLinearVelocity(0, body.getLinearVelocity().y);
//        }

        batch.begin();

        // Items
        itemBody.setTransform(body.getPosition().x + (!isFlipped? 7: -7) , body.getPosition().y, angle * MathUtils.degreesToRadians);

        if (itemSprite.getTexture() != null) {
            itemSprite.setPosition((itemBody.getPosition().x - itemSprite.getWidth() / 2), itemBody.getPosition().y - itemSprite.getHeight() / 2);
            itemSprite.setRotation(angle);
            itemSprite.setFlip(false, isFlipped);

            itemSprite.draw(batch);
        }

        // Player
        if (isRunning && (keyLeftPressed || keyRightPressed)) {
            TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime, true);
            if (isFlipped && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else {
                currentFrame.flip(!isFlipped && currentFrame.isFlipX(), false);
            }
            idleSprite.setPosition(body.getPosition().x - idleSprite.getWidth() / 2, body.getPosition().y - idleSprite.getHeight() / 2);
            idleSprite.setRegion(currentFrame);
            idleSprite.draw(batch);
        } else if (isJumping || isFalling) {
            jumpSprite.setPosition(body.getPosition().x - idleSprite.getWidth() / 2, body.getPosition().y - idleSprite.getHeight() / 2 + 1);
            if (isFlipped && !jumpSprite.isFlipX()) {
                jumpSprite.flip(true, false);
            } else {
                jumpSprite.flip(!isFlipped && jumpSprite.isFlipX(), false);
            }
            jumpSprite.draw(batch);
        } else {
            idleSprite.setRegion(idle);
            idleSprite.setPosition(body.getPosition().x - idleSprite.getWidth() / 2, body.getPosition().y - idleSprite.getHeight() / 2 + 1);
            if (isFlipped && !idleSprite.isFlipX()) {
                idleSprite.flip(true, false);
            } else {
                idleSprite.flip(!isFlipped && idleSprite.isFlipX(), false);
            }
            idleSprite.draw(batch);
        }
        System.out.println(body.getPosition().x + "/" + body.getPosition().y);

        //draw player was hit font
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

        batch.end();

        //player has respawned
        if (body.getPosition().y < -250) {
            body.setTransform(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM, 0);
            GameData.getInstance().setHeldItemType(null);
            GameData.getInstance().getHudRenderSystem().setActiveItem(null);
        }
        if (shouldRespawn) {
            body.setTransform(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM, 0);
            GameData.getInstance().setHeldItemType(null);
            GameData.getInstance().getHudRenderSystem().setActiveItem(null);
            shouldRespawn = false;

        }
    }

    public void onKeyDown(int key) {
        if (key == Input.Keys.A) {
            keyLeftPressed = true;
        }
        if (key == Input.Keys.D) {
            keyRightPressed = true;
        }
        if (key == Input.Keys.W) {
            keyUpPressed = true;
        }
        if (key == Input.Keys.S) {
            keyDownPressed = true;
        }
    }

    public void onKeyUp(int key) {
        if (key == Input.Keys.A) {
            keyLeftPressed = false;
        }
        if (key == Input.Keys.D) {
            keyRightPressed = false;
        }
        if (key == Input.Keys.W) {
            keyUpPressed = false;
        }
        if (key == Input.Keys.W) {
            keyDownPressed = false;
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getItemPosition() {
        return itemBody.getPosition(); // This is more accurate
//        return new Vector2(itemBody.getPosition().x + (!isFlipped? 7: -7), itemBody.getPosition().y);
    }

    public void onMouseMoved(int screenX, int screenY, OrthographicCamera camera) {
        centerPosition.set(itemBody.getPosition().x, itemBody.getPosition().y);

        worldCoordinates.set(screenX, screenY, 0);
        camera.unproject(worldCoordinates);

        mouseLoc.set(worldCoordinates.x, worldCoordinates.y);

        Vector2 direction = mouseLoc.sub(centerPosition);
        angle = direction.angleDeg();
    }

    public void setPosition(float v, float v1) {
        body.setTransform(v, v1, 0);
    }

    public void respawn() {
//        shouldRespawn = true;
    }

    public void hit(int damage, Color color) {
        Constants.font.setColor(color);
        if (isHit) {
            offset = 20;
            tempTimer = 0;
        }
        isHit = true;
        this.damage = damage;


    }

    public void dispose() {
        itemSprite.getTexture().dispose();
        jumpSprite.getTexture().dispose();
        idleSprite.getTexture().dispose();
    }
}
