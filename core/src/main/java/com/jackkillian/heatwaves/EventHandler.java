package com.jackkillian.heatwaves;


import com.jackkillian.heatwaves.screens.GameOverScreen;
import com.jackkillian.heatwaves.screens.GameScreen;

public class EventHandler {
    private float countdown;

    private int r;
    private int g;
    private int b;

    private int npcMax;
    private int npcBoost;

    private boolean eventIsActive = false;
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
        npcMax = 5;
        kills = 0;
        activeEvent = EventHandler.EventType.HEAT_SHIMMER;
        countdown = 30;
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
                //random number between 60 and 100
                r = 135;
                g = 206;
                b = 235;
                countdown = (int) (Math.random() * (100 - 60) + 60);
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
                //random number between 60 and 100

                r = 135;
                g = 206;
                b = 235;
                countdown = (int) (Math.random() * (100 - 60) + 60);

                eventIsActive = false;
                return;
            }
            kills = 0;
            countdown = EventType.HEAT_WAVES.duration;
            EventType.HEAT_WAVES.end = true;
        }
        if (countdown <= 0 && activeEvent == EventType.HEAT_BLAZE) {
            eventString = "Heat Blaze ends in: ";
            npcMax = 15;
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
