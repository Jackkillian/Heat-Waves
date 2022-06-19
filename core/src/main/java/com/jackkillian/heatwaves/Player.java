package com.jackkillian.heatwaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class Player {
    private Body body;
    private Sprite sprite;
    private SpriteBatch batch;

    public Player(World world, SpriteBatch batch, int x, int y) {
        PhysicsShapeCache cache = new PhysicsShapeCache("physics.xml");
        this.batch = batch;

        body = cache.createBody("player", world, 1, 1);
        body.setTransform(x, y, 0);
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setFixedRotation(true);

        sprite = new Sprite(new Texture("player.png"));
        sprite.setScale(5); // TODO: David do you magic viewport thingy and make this scale based on the viewport or something idk how the scaling works lol
    }

    public void update() {
        batch.begin();
        sprite.setPosition(body.getPosition().x, body.getPosition().y);
        sprite.draw(batch);
        batch.end();

        // TODO: make this a better implementation like that from WoD
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.setLinearVelocity(-1, body.getLinearVelocity().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(1, body.getLinearVelocity().y);
        } else {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
    }
}
