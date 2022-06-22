package com.jackkillian.heatwaves;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class WorldManager {
    private World world;

    // array containing the active bullets.
    private final Array<Bullet> activeBullets = new Array<Bullet>();

    //array of active npcs
    private final Array<NPC> activeNPCs = new Array<NPC>();

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
        //spawn new NPCs
        if (activeNPCs.size < 5) {
            // generate random number between 100 and 1500
            int x = (int) (Math.random() * (2500 - 100) + 100);
            int y = (int) (Math.random() * (2500 - 100) + 100);
            // choose random item from enum Item.ItemType
            NPC.NPCType npcType = NPC.NPCType.values()[(int) (Math.random() * NPC.NPCType.values().length)];
            activeNPCs.add(new NPC(npcType, x, y));
        }

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
        len = activeNPCs.size;
        for (int i = len; --i >= 0;) {
            NPC npc = activeNPCs.get(i);
            if (!npc.alive) {
                activeNPCs.removeIndex(i);
            } else {
                // Render bullets
                npc.update(delta);
            }
        }


        GameData.getInstance().getBatch().end();
    }

    public void createBullet(float x, float y, float xVel, float yVel, Bullet.Origin origin) {
        // if you want to spawn a new bullet:
        Bullet item = bulletPool.obtain();
        item.origin = origin;
        item.init(x, y, xVel, yVel);
        activeBullets.add(item);
    }

    public void createGrapplingHook(float shooterX, float shooterY, float v, float v1) {
        Bullet item = bulletPool.obtain();
        item.init(shooterX, shooterY, v, v1, true);
        activeBullets.add(item);
    }


    @Deprecated
    public void createNPC(NPC.NPCType type, float x, float y) {
//        activeNPCs.add(new NPC(type, x, y));
//
//        System.out.println("npc done");
    }
}
