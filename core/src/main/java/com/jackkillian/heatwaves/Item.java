package com.jackkillian.heatwaves;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

// TODO: Give different guns different stats (reload time, damage, etc.)

public class Item {
    public enum ItemType {
        HANDGUN,
        GRAPPLER,
        SHOTGUN,
//        PISTOL, pistol is basically a handgun
        MEDKIT
    }
    private int ammo;
    private Sprite sprite;
    private Body body;
    private ItemType itemType;

    private float countDown = 15f;

    private static Assets assets;



    public Item(ItemType type, float x, float y) {
        if (type == ItemType.SHOTGUN) {
            ammo = 10;
        } else {
            //basically infinity lolz
            ammo = 1000;
        }
        itemType = type;
        assets = GameData.getInstance().getAssets();
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
        fdef.filter.maskBits = Constants.WALL_BIT | Constants.PLAYER_BIT;
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
                texture = assets.getManager().get("items/handgun.png", Texture.class);
                break;
            case GRAPPLER:
                texture = assets.getManager().get("items/grapplerGun.png", Texture.class);
                break;
            case SHOTGUN:
                texture = assets.getManager().get("items/shotgun.png", Texture.class);
                break;
//            case PISTOL:
//                texture = new Texture("items/pistol.png");
//                break;
            case MEDKIT:
                texture = assets.getManager().get("items/medkit.png", Texture.class);
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
//            case PISTOL:
//                texture = new Texture("items/pistolHeld.png");
//                break;
            case MEDKIT:
                texture = new Texture("items/medkitHeld.png");
                break;
        }
        return texture;
    }

    public void update(float delta) {
        countDown -= delta;
        if (countDown <= 0) {
            GameData.getInstance().getItemSystem().removeItem(this, body);
            countDown = 15f;
        }
    }
    public int getAmmo() {
        return ammo;
    }
}
