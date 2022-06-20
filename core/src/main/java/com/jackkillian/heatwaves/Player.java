package com.jackkillian.heatwaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.codeandweb.physicseditor.PhysicsShapeCache;

//TODO: Make the player's right hand move with the mouse. Also do the animations
//also add shooting and item generation :D

public class Player {
    private Body body;
    private Sprite sprite;
    private SpriteBatch batch;

    //physics shape cache is good but too complex for this game
    public Player(World world, SpriteBatch batch, int x, int y) {
//        PhysicsShapeCache cache = new PhysicsShapeCache("physics.xml");
//        body = cache.createBody("player", world, 3f, 3f);
//        body.setTransform(x, y, 0);
//        body.setType(BodyDef.BodyType.DynamicBody);
//        body.setFixedRotation(true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM ,y/ Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;



        body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(11 / Constants.PPM);
        fdef.shape = circleShape;
        fdef.friction = 5f;
        fdef.restitution = 0.09f;
        body.createFixture(fdef);
        
        
        this.batch = batch;

        //maybe we don't need asset manager for this game
        sprite = new Sprite(new Texture("player/player1.png"));
    }

    public void update() {

        // TODO: make this a better implementation like that from WoD
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.setLinearVelocity(-50, body.getLinearVelocity().y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(50, body.getLinearVelocity().y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 50);
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }



        batch.begin();
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, (body.getPosition().y - sprite.getWidth() / 2) + 3);
        sprite.draw(batch);
        batch.end();

        GameData.getInstance().getCamera().position.set(getPosition(), 0);


        if (body.getPosition().y < -100) {
            body.setTransform(100, 100, 0);
        }


    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
