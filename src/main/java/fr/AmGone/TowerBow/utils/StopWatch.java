package fr.AmGone.TowerBow.utils;

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
    public static String getHumanTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        StringBuilder time = new StringBuilder();

        if(hours != 0) {
            time.append(getHumanHours(millis)).append(":").append(getHumanMinutes(millis)).append(":").append(getHumanSeconds(millis));
        } else {
            time.append(getHumanMinutes(millis)).append(":").append(getHumanSeconds(millis));
        }

        return time.toString();
    }

    private static String getHumanHours(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;

        if(hours < 10) {
            return "0" + hours;
        } else {
            return String.valueOf(hours);
        }
    }

    private static String getHumanMinutes(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;

        if(minutes < 10) {
            return "0" + minutes;
        } else {
            return String.valueOf(minutes);
        }
    }

    private static String getHumanSeconds(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if(seconds < 10) {
            return "0" + seconds;
        } else {
            return String.valueOf(seconds);
        }
    }
}