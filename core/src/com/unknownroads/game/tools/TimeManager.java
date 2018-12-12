package com.unknownroads.game.tools;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class TimeManager {

    private float lapTime; //in secs, because delta is secs

    private ArrayList<Float> timesList;
    private int bestTimeIndex;

    public TimeManager () {
        this.lapTime = -1;
        this.timesList = new ArrayList<Float>();
        this.bestTimeIndex = 0;
    }

    public void newLap(){

        if(lapTime != -1)
            timesList.add(lapTime);

        if(timesList.size() > 0 && lapTime < timesList.get(bestTimeIndex)) {
            bestTimeIndex = timesList.size() - 1;
        }

        for(float f : timesList)
            System.out.print(f + "|");
        System.out.println("best index" + bestTimeIndex);

        lapTime = 0;

    }

    public void update(float delta){
        this.lapTime += delta;
    }

    public float getLapTime(){
        return this.lapTime;
    }

    public float getBestLap(){
        return timesList.size() > 0 ? timesList.get(bestTimeIndex):-1;
    }


}
