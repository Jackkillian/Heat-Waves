package com.jackkillian.heatwaves;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class WorldManager {
    private World world;

    // array containing the active bullets.
    private final Array<Bullet> activeBullets = new Array<Bullet>();

    // bullet pool.
    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void update(float delta) {
        // if you want to free dead bullets, returning them to the pool:
        Bullet item;
        int len = activeBullets.size;

        GameData.getInstance().getBatch().begin();
        for (int i = len; --i >= 0;) {
            item = activeBullets.get(i);
            if (!item.alive) {
                activeBullets.removeIndex(i);
                bulletPool.free(item);
            } else {
                // Render bullets
                item.update(delta);
            }
        }
        GameData.getInstance().getBatch().end();
    }

    public void createBullet(float x, float y, float xVel, float yVel) {
        // if you want to spawn a new bullet:
        Bullet item = bulletPool.obtain();
        item.init(x, y, xVel, yVel);
        activeBullets.add(item);
    }

    public void createGrapplingHook(float shooterX, float shooterY, float v, float v1) {
        Bullet item = bulletPool.obtain();
        item.init(shooterX, shooterY, v, v1, true);
        activeBullets.add(item);
    }
}
