package fr.Hygon.TowerBow.utils;

import java.util.concurrent.TimeUnit;

public class StopWatch { // https://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }


    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }

    //elapsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime);
        } else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }


    //elapsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        } else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }

    //elapsed time in minutes
    public String getHumanHour() {
        long millis = getElapsedTime();
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        StringBuilder time = new StringBuilder();

        if(hours != 0) {
            time.append(hours).append(":").append(minutes).append(":").append(seconds);
        } else if(minutes != 0) {
            time.append(minutes).append(":").append(seconds);
        } else {
            time.append(seconds);
        }

        return time.toString();
    }
}