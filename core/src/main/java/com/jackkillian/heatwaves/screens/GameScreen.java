package com.jackkillian.heatwaves.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jackkillian.heatwaves.*;
import com.jackkillian.heatwaves.systems.HudRenderSystem;
import com.jackkillian.heatwaves.systems.ItemSystem;
import com.jackkillian.heatwaves.systems.MapRenderSystem;

public class GameScreen implements Screen, InputProcessor {
    private Engine engine;
    private GameData gameData;
    private SpriteBatch batch;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private FitViewport viewport;
    private WorldManager world;
    private Player player;
    private Sound shootSound;
    public Music music;
    private Sound shotgunSound;
    private Sound dryFireSound;
    public static Sound hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
    private Sprite grapplingHookRope;
    private EventHandler eventHandler;

    private Vector2 grapplerVeloctiy = new Vector2();

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        gameData = GameData.getInstance();
        World world = new World(new Vector2(0, -130), false);
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        int xHotspot = pixmap.getWidth() / 2;
        int yHotspot = pixmap.getHeight() / 2;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(cursor);
        pixmap.dispose();

        eventHandler = new EventHandler();
        gameData.setEventHandler(eventHandler);

        WorldManager worldManager = new WorldManager();
        worldManager.setWorld(world);
        gameData.setWorldManager(worldManager);

        //this is fine - dave
        world.setContactListener(new ContactListner() {
            @Override
            public void beginContact(Contact contact) {
                super.beginContact(contact);


                Body playerBody;
                Body otherBody;
                if (contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null) return;
                if (contact.getFixtureA().getBody().getUserData() == null || contact.getFixtureB().getBody().getUserData() == null)
                    return;


                Body bulletBody;
                //check if bullet hit wall
                if (contact.getFixtureA().getBody().getUserData() instanceof Bullet) {
                    bulletBody = contact.getFixtureA().getBody();
                    otherBody = contact.getFixtureB().getBody();
                    if (otherBody.getUserData().equals("wall")) {
                        Bullet bullet = (Bullet) bulletBody.getUserData();
                        bullet.alive = false;
                        return;
                    }
                } else if (contact.getFixtureB().getBody().getUserData() instanceof Bullet) {
                    bulletBody = contact.getFixtureB().getBody();
                    otherBody = contact.getFixtureA().getBody();
                    if (otherBody.getUserData().equals("wall")) {
                        Bullet bullet = (Bullet) bulletBody.getUserData();
                        bullet.alive = false;
                        return;
                    }
                }

                if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
                    playerBody = contact.getFixtureA().getBody();
                    otherBody = contact.getFixtureB().getBody();
                } else if (contact.getFixtureB().getBody().getUserData() instanceof Player) {
                    playerBody = contact.getFixtureB().getBody();
                    otherBody = contact.getFixtureA().getBody();
                } else {
                    if (contact.getFixtureA().getBody().getUserData().equals("grapplingHook") || contact.getFixtureB().getBody().getUserData().equals("grapplingHook")) {
                        if (contact.getFixtureA().getBody().getUserData().equals("wall") || contact.getFixtureB().getBody().getUserData().equals("wall")) {
                            gameData.setGrapplingHit(true);
                        }
                    }

                    Body npcBody;
                    Body otherBody2;
                    //only npcs expected down here lol
                    if (contact.getFixtureA().getBody().getUserData() instanceof NPC) {
                        npcBody = contact.getFixtureA().getBody();
                        otherBody2 = contact.getFixtureB().getBody();
                    } else if (contact.getFixtureB().getBody().getUserData() instanceof NPC) {
                        npcBody = contact.getFixtureB().getBody();
                        otherBody2 = contact.getFixtureA().getBody();
                    } else {
                        return;
                    }

                    //bullet has collided with npc
                    if (otherBody2.getUserData() instanceof Bullet) {
                        Bullet bullet = (Bullet) otherBody2.getUserData();
                        if (bullet.origin != Bullet.Origin.NPC) {
                            int damage = 10;
                            if (GameData.getInstance().getHeldItemType() == Item.ItemType.SHOTGUN) {
                                damage = MathUtils.random(50, 70);
                            } else if (GameData.getInstance().getHeldItemType() == Item.ItemType.HANDGUN) {
                                //random number between 15 and 25
                                damage = (int) (Math.random() * (25 - 15 + 1)) + 15;
                            }

                            NPC npc = (NPC) npcBody.getUserData();
                            if (!(npc.health > 0)) {
                                return;
                            }
                            npc.hit(damage);
                            bullet.alive = false;
                        }
                    }

                    return;
                }
                // below are all collisions involving a player

                boolean hacky = otherBody.getUserData().equals("wall");
                gameData.setTouchingPlatform(hacky);

                if (otherBody.getUserData() instanceof Item) {
                    Item item = (Item) otherBody.getUserData();

                    if (item.getType() == Item.ItemType.MEDKIT) {
                        gameData.healHealth(25);
                    } else {
                        gameData.setAmmo(item.getAmmo());
                        gameData.getHudRenderSystem().setActiveItem(item.getSprite().getTexture());
                        gameData.setHeldItemType(item.getType());
                    }
                    otherBody.setUserData(null);
                    gameData.getItemSystem().removeItem(item, otherBody);
                }


                //player bullet hit handler
                if (otherBody.getUserData() instanceof Bullet) {
                    Bullet bullet = (Bullet) otherBody.getUserData();
                    if (bullet.origin != Bullet.Origin.PLAYER) {
                        //stop grapple if shot
                        if (GameData.getInstance().isGrapplingPulling()) {
                            gameData.resetGrappler();
                        }
                        //random number between 20 and 30
                        int damage = (int) (Math.random() * (50 - 20 + 1)) + 20;

                        bullet.alive = false;
                        // damaged player to shield?
                        boolean isBlueHit;
                        if (gameData.getPlayerShield() > 0) {
                            gameData.damageShield(damage);
                            isBlueHit = true;
                        } else {
                            isBlueHit = false;
                            gameData.damageHealth(damage);
                        }
                        GameScreen.hitSound.play();
                        gameData.getPlayer().hit(damage, (isBlueHit ? Color.CYAN : Color.GOLD));
                        if (gameData.getPlayerHealth() <= 0) {
                            // respawn the player
                            gameData.resetGrappler();
                            gameData.healHealth(100);
                            gameData.healShield(100);
                            gameData.getEventHandler().playerOnDeath();
                            gameData.getPlayer().respawn();
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                super.endContact(contact);

                if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
                    // player left the platform
                    gameData.setTouchingPlatform(false);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                super.preSolve(contact, oldManifold);

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                super.postSolve(contact, impulse);
            }
        });

        gameData.setGame(((HeatWaves) Gdx.app.getApplicationListener()));
        gameData.setAssets(((HeatWaves) gameData.getGame()).assets);
        gameData.setSkin(((HeatWaves) gameData.getGame()).skin);

        batch = new SpriteBatch();
        player = new Player(gameData.getWorldManager(), batch);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.zoom = 0.5f;
        viewport = new FitViewport(1280 / Constants.PPM, 720/ Constants.PPM, camera);
        gameData.setViewport(viewport);

        engine = new Engine();
        gameData.setEngine(engine);

        gameData.healHealth(100);
        gameData.healShield(100);
        gameData.setPlayer(player);
        gameData.setBatch(batch);
        gameData.setMapRenderSystem(new MapRenderSystem(gameData, camera));
        gameData.setItemSystem(new ItemSystem(gameData, batch));
        gameData.setHudRenderSystem(new HudRenderSystem(gameData));
        engine.addSystem(gameData.getMapRenderSystem());
        engine.addSystem(gameData.getItemSystem());
        engine.addSystem(gameData.getHudRenderSystem());

        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
        shotgunSound = Gdx.audio.newSound(Gdx.files.internal("shotgun.wav"));
        dryFireSound = Gdx.audio.newSound(Gdx.files.internal("dryfire.wav"));

        music = Gdx.audio.newMusic(Gdx.files.internal("themeSong.wav"));
        music.setLooping(true);
        music.play();

        grapplingHookRope = new Sprite(new Texture("items/grapplingHookRope.png"));
//        grapplingHookRope.setFlip(false, true); // inverted for some reason...
    }

    @Override
    public void render(float delta) {
        GameData.getInstance().getWorld().step(delta * 1.2f, 6, 2);
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(eventHandler.getR() / 255f, eventHandler.getG() / 255f, eventHandler.getB() / 255f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameData.getEventHandler().update(delta);

        engine.update(delta);
        gameData.getWorldManager().update(delta);

        camera.position.lerp(new Vector3(player.getPosition().x, player.getPosition().y, 0), 0.1f);

        // hey you're clogging up the main render method
        if (gameData.isGrapplingShot()) {
            // draw a line from the player to the grappling hook
            Vector2 playerPos = player.getItemPosition();
            Vector2 hookPos = gameData.getGrapplingPosition();

            // a**2 == b**2 + c**2
            float b = hookPos.y - playerPos.y;
            float c = playerPos.x - hookPos.x;
            float a = (float) Math.sqrt(Math.pow(c, 2) + Math.pow(b, 2));

            // looks like we gotta use trig again :D
            float angle = (float) Math.atan(b / c) * MathUtils.radiansToDegrees;
            if (hookPos.x < playerPos.x) {
                angle += 180;
            }
            angle *= -1;

            // draw a line from the player to the grappling hook
            batch.begin();
            grapplingHookRope.setSize(a, grapplingHookRope.getHeight());
            grapplingHookRope.setPosition(playerPos.x, playerPos.y);
            grapplingHookRope.setRotation(angle);
            grapplingHookRope.draw(batch);
            batch.end();
        }

        if (gameData.isGrapplingPulling()) {
            //if pull for more than x seconds, release the hook
            GameData.getInstance().setGrapplerTimer(GameData.getInstance().getGrapplerTimer() + delta);
            if (GameData.getInstance().getGrapplerTimer() > 5f) {
                gameData.resetGrappler();
                return;
            }

            Vector2 playerPos = player.getPosition();
            Vector2 hookPos = gameData.getGrapplingPosition();

            // a**2 == b**2 + c**2
            float b = hookPos.y - playerPos.y;
            float c = playerPos.x - hookPos.x;
            float a = (float) Math.sqrt(Math.pow(c, 2) + Math.pow(b, 2));

            //calculate velocity
            if (!gameData.isGrappleCalculated()) {
                float speed = 450f;
                float shooterX = player.getItemPosition().x; // get player location
                float shooterY = player.getItemPosition().y; // get player location
                float velx = hookPos.x - shooterX; // get distance from shooter to target on x plain
                float vely = hookPos.y - shooterY; // get distance from shooter to target on y plain
                float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
                if (length != 0) {
                    velx = velx / length * 1.5f;  // get required x velocity to aim at target
                    vely = vely / length * 1.5f;  // get required y velocity to aim at target
                }
                grapplerVeloctiy.set(velx * speed, vely * speed);
                gameData.setGrappleCalculated(true);
            }

            // move the player closer to the grappling hook
//            player.setPosition(playerPos.x + (hookPos.x - playerPos.x) / 5, playerPos.y + (hookPos.y - playerPos.y) / 5);
//            Vector2 newPos = player.getPosition().cpy().lerp(new Vector2(hookPos.x, hookPos.y), 0.1f);
            player.applyForce(grapplerVeloctiy.cpy().scl(delta * 100f));

            // if the player is close enough to the grappling hook, destroy the grappling hook
            if (a < 25) {
                gameData.resetGrappler();
                player.setVelocity(0, 100f);
            }
        }

        player.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.dispose();
        music.dispose();
        shootSound.dispose();
        batch.dispose();
        gameData.getWorld().dispose();
        gameData.getEngine().removeAllEntities();
        gameData.getEngine().removeAllSystems();
        gameData.getMapRenderSystem().dispose();
//        GameData.getInstance().getAssets().getManager().dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        player.onKeyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        player.onKeyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameData.getHeldItemType() == null) return false;

        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        camera.unproject(worldCoordinates);
        Vector2 mousePos = new Vector2(worldCoordinates.x, worldCoordinates.y);

        float speed = 450f;  // set the speed of the bullet
        float shooterX = player.getItemPosition().x; // get player location
        float shooterY = player.getItemPosition().y; // get player location
        float velx = mousePos.x - shooterX; // get distance from shooter to target on x plain
        float vely = mousePos.y - shooterY; // get distance from shooter to target on y plain
        float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
        if (length != 0) {
            velx = velx / length * 1.5f;  // get required x velocity to aim at target
            vely = vely / length * 1.5f;  // get required y velocity to aim at target
        }

        //really hacky way
        if (gameData.getHeldItemType() == Item.ItemType.MEDKIT) {return false;}

        if (gameData.getHeldItemType() == Item.ItemType.HANDGUN) {
            if (gameData.getAmmo() <= 0) {
                dryFireSound.play();
                return false;
            }
            gameData.setAmmo(gameData.getAmmo() - 1);
            shootSound.play();
            gameData.addShot();
            gameData.getWorldManager().createBullet(shooterX, shooterY, velx * speed, vely * speed, Bullet.Origin.PLAYER);
        } else if (gameData.getHeldItemType() == Item.ItemType.SHOTGUN) {
            if (gameData.getAmmo() <= 0) {
                dryFireSound.play();
                return false;
            }
            gameData.addShot();
            gameData.setAmmo(gameData.getAmmo() - 1);
            shotgunSound.play();
            gameData.getWorldManager().createBullet(shooterX, shooterY, velx * speed, vely * speed, Bullet.Origin.PLAYER);
        }

        if (gameData.getHeldItemType() == Item.ItemType.GRAPPLER && !gameData.isGrapplingShot() && !gameData.isGrapplingPulling()) {
            speed /= 0.5f;
            shootSound.play();
            gameData.setGrapplingShot(true);
            gameData.getWorldManager().createGrapplingHook(shooterX, shooterY, velx * speed, vely * speed);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        player.onMouseMoved(screenX, screenY, camera);

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
