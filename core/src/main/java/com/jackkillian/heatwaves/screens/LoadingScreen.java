package com.jackkillian.heatwaves.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.jackkillian.heatwaves.*;

public class LoadingScreen implements Screen {
    private final HeatWaves game;
    private final Assets assets;
    private Stage stage;
    private ProgressBar bar;
    private Label countLeftLabel;
    private int number = 0;


    public LoadingScreen(HeatWaves game) {
        this.game = game;
        assets = new Assets();
        GameData gameData = GameData.getInstance();

        World world = new World(new Vector2(0, -80), false);
        world.setContactFilter(new ContactFilter() {
            @Override
            public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
                Object object1 = fixtureA.getBody().getUserData();
                Object object2 = fixtureA.getBody().getUserData();
                if (object1 instanceof Bullet || object2 instanceof Bullet) {
                    System.out.println("overrode contact filter");
                    return true;
                }
                return (fixtureA.getFilterData().categoryBits == fixtureB.getFilterData().maskBits) || (fixtureB.getFilterData().categoryBits == fixtureA.getFilterData().maskBits);
            }
        });

        WorldManager worldManager = new WorldManager();
        worldManager.setWorld(world);
        GameData.getInstance().setWorldManager(worldManager);

        //this is fine - dave
        world.setContactListener(new ContactListner() {
            @Override
            public void beginContact(Contact contact) {
                super.beginContact(contact);


                Body playerBody;
                Body otherBody;
                if (contact.getFixtureA().getBody().getUserData() == null || contact.getFixtureB().getBody().getUserData() == null) return;



//                if ((contact.getFixtureA().getBody().getUserData().equals("wall") || contact.getFixtureB().getBody().getUserData().equals("wall")) && bulletBody != null) {
//                    //bullet hit wall
//                    System.out.println("wall");
//                    Bullet bullet = (Bullet) bulletBody.getUserData();
//                    bullet.alive = false;
//                }
                if (contact.getFixtureB().getBody().getUserData().equals("wall") && contact.getFixtureA().getBody().getUserData() instanceof Bullet) {
                    System.out.println("all");
                } {

                }

                if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
                    playerBody = contact.getFixtureA().getBody();
                    otherBody = contact.getFixtureB().getBody();
                } else if (contact.getFixtureB().getBody().getUserData() instanceof Player){
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
                    if (contact.getFixtureA().getBody().getUserData() instanceof NPC ) {
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
                        NPC npc = (NPC) npcBody.getUserData();
                        npc.hit(30);
                        bullet.alive = false;
                        System.out.println("hit to npc. Hit Number: " + number++);
                    }

                    return;
                }
                // below are all collisions involving a player

                if (otherBody.getUserData() instanceof Item) {
                    Item item = (Item) otherBody.getUserData();
                    GameData.getInstance().getHudRenderSystem().setActiveItem(item.getSprite().getTexture());
                    GameData.getInstance().setHeldItemType(item.getType());
                    GameData.getInstance().getItemSystem().removeItem(item, otherBody);

                }


                //player bullet hit handler
                if (otherBody.getUserData() instanceof Bullet) {
                    Bullet bullet = (Bullet) otherBody.getUserData();
                    if (bullet.origin == Bullet.Origin.PLAYER) {
                        return;
                        //this bullet was fired by a player
                    }
                    System.out.println("hit to player");

                }



                // Collision involves player, player can jump now! (once there are bullets, we'll need to check for bullet user data too)
                //this is very hacky
                // no it's not >:C
                // >:D  "optimizing performance"
                boolean hacky = otherBody.getUserData().equals("wall");
                gameData.setTouchingPlatform(hacky);
//                if (!(otherBody.getUserData() instanceof Item) && !otherBody.getUserData().equals("bullet") && !otherBody.getUserData().equals("grapplingHook")) {
//
//                    gameData.setTouchingPlatform(true);
//                }

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

        gameData.setAssets(assets);
        gameData.setSkin(game.skin);
    }


    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // add widgets
        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
        logo.setScale(1.5f);
        logo.setAlign(Align.center);
        table.add(logo).padBottom(50).row();

        Label label = new Label("Loading resources...", game.skin);
        label.setAlignment(Align.center);
        table.add(label).pad(10).row();

        bar = new ProgressBar(0, 1, 0.01f, false, game.skin);
        bar.setValue(0);
        bar.setAnimateDuration(0.5f);
        table.add(bar).row();

        countLeftLabel = new Label("", game.skin);
        label.setAlignment(Align.center);
        table.add(countLeftLabel).pad(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (assets.getManager().update()) {
            game.setScreen(new MainMenuScreen(game));
        }

        countLeftLabel.setText((int) (assets.getManager().getProgress() * 100) + "%");
        bar.setValue(assets.getManager().getProgress());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
        assets.getManager().dispose();
    }
}
