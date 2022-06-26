package com.jackkillian.heatwaves;


import com.badlogic.gdx.math.MathUtils;
import com.jackkillian.heatwaves.screens.GameOverScreen;
import com.jackkillian.heatwaves.screens.GameScreen;

public class EventHandler {
    private float countdown;

    private int r;
    private int g;
    private int b;

    private int npcMax;
    private int npcBoost;

    private boolean eventIsActive;
    private int kills;


    private enum EventType {
        HEAT_SHIMMER(60, false, 3),
        HEAT_WAVES(120, false, 5),
        HEAT_BLAZE(200, false, 15);

        int duration;
        boolean end;
        int kills;

        EventType(int duration, boolean end, int kills) {
            this.duration = duration;
            this.end = end;
            this.kills = kills;
        }
    }


    private String eventString;

    private static EventType activeEvent = EventHandler.EventType.HEAT_SHIMMER;

    public EventHandler() {

        npcBoost = 0;
        npcMax = 6;
        kills = 0;
        activeEvent = EventHandler.EventType.HEAT_SHIMMER;
        EventType.HEAT_SHIMMER.end = false;
        EventType.HEAT_WAVES.end = false;
        EventType.HEAT_BLAZE.end = false;
        countdown = 30;
        eventIsActive = false;
        eventString = "Heat Shimmer in: ";
        r = 135;
        g = 206;
        b = 235;
    }

    public float getCountdown() {
        return countdown;
    }

    public String getEventString() {
        return eventString;
    }


    public void update(float delta) {
        countdown -= delta;


        if (countdown <= 0 && activeEvent == EventType.HEAT_SHIMMER) {
            eventString = "Heat Shimmer ends in: ";
            r = 227;
            g = 169;
            b = 91;
            eventIsActive = true;



            System.out.println(EventType.HEAT_SHIMMER.end);
            if (EventType.HEAT_SHIMMER.end) {

                if (kills < EventType.HEAT_SHIMMER.kills) {
                    ((GameScreen) GameData.getInstance().getGame().getScreen()).music.stop();
                    GameData.getInstance().getGame().setScreen(new GameOverScreen(false));
                }

                activeEvent = EventType.HEAT_WAVES;
                eventString = "Heat Waves in: ";
                // this is countdown to next event
                r = 135;
                g = 206;
                b = 235;
                countdown = MathUtils.random(30, 45);
                eventIsActive = false;
                return;
            }
            kills = 0;
            countdown = EventType.HEAT_SHIMMER.duration;
            EventType.HEAT_SHIMMER.end = true;


        }
        if (countdown <= 0 && activeEvent == EventType.HEAT_WAVES) {
            eventString = "Heat Waves end in: ";
            npcMax = 9;

            eventIsActive = true;
            r = 227;
            g = 169;
            b = 91;


            if (EventType.HEAT_WAVES.end) {
                if (kills < EventType.HEAT_WAVES.kills) {
                    ((GameScreen) GameData.getInstance().getGame().getScreen()).music.stop();
                    GameData.getInstance().getGame().setScreen(new GameOverScreen(false));
                }
                activeEvent = EventType.HEAT_BLAZE;
                eventString = "Heat Blaze in: ";
                // this is countdown to next event

                r = 135;
                g = 206;
                b = 235;
                countdown = MathUtils.random(30, 45);

                eventIsActive = false;
                return;
            }
            kills = 0;
            countdown = EventType.HEAT_WAVES.duration;
            EventType.HEAT_WAVES.end = true;
        }
        if (countdown <= 0 && activeEvent == EventType.HEAT_BLAZE) {
            eventString = "Heat Blaze ends in: ";
            npcMax = 16;
            r = 227;
            g = 169;
            b = 91;
            eventIsActive = true;

            if (EventType.HEAT_BLAZE.end) {
                //game over either way
                if (kills < EventType.HEAT_BLAZE.kills) {
                    ((GameScreen) GameData.getInstance().getGame().getScreen()).music.stop();
                    GameData.getInstance().getGame().setScreen(new GameOverScreen(false));
                } else {
                    ((GameScreen) GameData.getInstance().getGame().getScreen()).music.stop();
                    GameData.getInstance().getGame().setScreen(new GameOverScreen(true));
                }
            }
            kills = 0;
            countdown = EventType.HEAT_BLAZE.duration;
            EventType.HEAT_BLAZE.end = true;
        }


    }

    public void playerOnDeath() {
        if (activeEvent == EventType.HEAT_BLAZE && activeEvent.end) {
            ((GameScreen) GameData.getInstance().getGame().getScreen()).music.stop();
            GameData.getInstance().getGame().setScreen(new GameOverScreen(false));
        }
    }

    public String getRequiredKills() {
        return kills + " / " + activeEvent.kills;
    }
    public boolean isEventActive() {
        return eventIsActive;
    }

    public int getR() {
        return r;
    }
    public int getG() {
        return g;
    }
    public int getB() {
        return b;
    }
    public void addKill() {
        if (kills >= activeEvent.kills) return;
        kills++;
    }
    public void addNPCMax() {
        npcBoost++;
    }
    public void subtractNPCMax() {
        npcBoost--;
    }

    public int getNpcMax() {
        return npcMax + npcBoost;
    }

}
