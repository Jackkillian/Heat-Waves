package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Item {
    public enum ItemType {
        HANDGUN,
        GRAPPLER,
        SHOTGUN,
        PISTOL
    }

    private Sprite sprite;
    private Body body;
    private ItemType itemType;

    public Item(ItemType type, float x, float y) {
        itemType = type;
        sprite = new Sprite(getTexture(type));

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = GameData.getInstance().getWorld().createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PPM, 10f / Constants.PPM);
        fdef.shape = shape;

//        CircleShape circleShape = new CircleShape();
//        circleShape.setRadius(11 / Constants.PPM);
//        fdef.shape = circleShape;

        fdef.friction = 100f;
        fdef.restitution = 0.09f;
        fdef.isSensor = false;
        fdef.filter.categoryBits = Constants.ITEM_BIT;
        fdef.filter.maskBits = Constants.WALL_BIT;
        body.createFixture(fdef);
        body.setUserData(this);

        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }

    public ItemType getType() {
        return itemType;
    }

    public static Texture getTexture(ItemType type) {
        Texture texture = null;
        switch (type) { // Can't use enhanced switch statements because of the HTML plugin...
            case HANDGUN:
                texture = new Texture("items/handgun.png");
                break;
            case GRAPPLER:
                texture = new Texture("items/grapplerGun.png");
                break;
            case SHOTGUN:
                texture = new Texture("items/shotgun.png");
                break;
            case PISTOL:
                texture = new Texture("items/pistol.png");
                break;
        }
        return texture;
    }

    public static Texture getTexture(ItemType type, boolean held) {
        if (type == null) return null;
        if (!held) {
            return getTexture(type);
        }
        Texture texture = null;
        switch (type) { // Can't use enhanced switch statements because of the HTML plugin...
            case HANDGUN:
                texture = new Texture("items/handgunHeld.png");
                break;
            case GRAPPLER:
                if (GameData.getInstance().isGrapplingShot()) {
                    texture = new Texture("items/grapplerGunHeldShot.png");
                } else {
                    texture = new Texture("items/grapplerGunHeld.png");
                }
                break;
            case SHOTGUN:
                texture = new Texture("items/shotgunHeld.png");
                break;
            case PISTOL:
                texture = new Texture("items/pistolHeld.png");
                break;
        }
        return texture;
    }
}
