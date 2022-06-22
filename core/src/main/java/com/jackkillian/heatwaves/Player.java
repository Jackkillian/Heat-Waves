package com.jackkillian.heatwaves;

import com.badlogic.gdx.Input;
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

import static com.jackkillian.heatwaves.Constants.SPAWN_X;
import static com.jackkillian.heatwaves.Constants.SPAWN_Y;

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

    private final Sprite gunSprite;
    private final Sprite grapplerSprite;

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
        fdef.filter.maskBits = Constants.WALL_BIT;
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

        //set gun sprites, so we don't create new ones in the update method
        //big performance improvements
        gunSprite = new Sprite(new Texture("items/handgunHeld.png"));
        grapplerSprite = new Sprite(new Texture("items/grapplerGunHeld.png"));
    }

    public void update(float delta) {
        stateTime += delta;

        boolean isJumping = body.getLinearVelocity().y > 0.15; // When on ground, the velocity is still 0.11...
        boolean isFalling = body.getLinearVelocity().y < 0;
        boolean isRunning = !isFalling && !isJumping && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0);
        isFlipped = angle > 100 && angle < 266;
        boolean canJump = GameData.getInstance().isTouchingPlatform() || (!isJumping && !isFalling);

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
                body.setLinearVelocity(-70, body.getLinearVelocity().y);
            } else {
                body.setLinearVelocity(-50, body.getLinearVelocity().y);
            }
        }
        if (keyRightPressed) {
            if (canJump) {
                body.setLinearVelocity(70, body.getLinearVelocity().y);
            } else {
                body.setLinearVelocity(50, body.getLinearVelocity().y);
            }
        }
        if (keyUpPressed && canJump) {
            body.setLinearVelocity(body.getLinearVelocity().x, 50);
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

        batch.end();

        //player has respawned
        if (body.getPosition().y < -250) {
            body.setTransform(SPAWN_X / Constants.PPM, SPAWN_Y / Constants.PPM, 0);
            GameData.getInstance().setHeldItemType(null);
            GameData.getInstance().getHudRenderSystem().setActiveItem(null);
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
        return new Vector2(itemBody.getPosition().x + (!isFlipped? 7: -7), itemBody.getPosition().y);

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
}
