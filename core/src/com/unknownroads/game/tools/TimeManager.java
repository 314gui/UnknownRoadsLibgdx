package com.unknownroads.game.tools;

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

        if(timesList.size() > 0 && lapTime > timesList.get(bestTimeIndex)) {
            bestTimeIndex = timesList.size() - 1;
        }

        System.out.println("New time" + lapTime);
        lapTime = 0;

    }

    public void update(float delta){
        this.lapTime += delta;
        System.out.println(this.lapTime);
    }

    public float getLapTime(){
        return this.lapTime;
    }

    public void display(){



    }

}
