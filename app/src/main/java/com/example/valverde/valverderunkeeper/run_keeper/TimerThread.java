package com.example.valverde.valverderunkeeper.run_keeper;

import android.os.Handler;
import android.widget.TextView;

public class TimerThread extends Thread {
    private static final Object lock = new Object();
    private static final int HOUR_FACTOR = 3600000;
    private static final int MINUTE_FACTOR = 60000;
    private static final int SECOND_FACTOR = 1000;
    private boolean running = false;
    private boolean paused = false;
    private long pauseTime = 0;
    private TextView timeField;
    private Handler handler;

    public TimerThread(Handler handler, TextView timeField) {
        this.handler = handler;
        this.timeField = timeField;
    }


    @Override
    public void run() {
        final int SLEEP_TIME = 10;
        final long startTime = System.currentTimeMillis();
        running = true;
        while (running) {
            pauseTime = checkIfIsPaused(pauseTime);
            try {
                final long timeElapsedInMillis = System.currentTimeMillis() - startTime;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String timeString = getTimeInFormat(timeElapsedInMillis - getPausedTime());
                        if (running) timeField.setText(timeString);
                    }
                });
                Thread.sleep(SLEEP_TIME);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private String getTimeInFormat(long elapsedTime) {
        String hours = Long.toString(elapsedTime / HOUR_FACTOR);
        elapsedTime %= HOUR_FACTOR;
        String minutes = getMinutes(elapsedTime);
        elapsedTime %= MINUTE_FACTOR;
        String seconds = getSeconds(elapsedTime);
        return hours+":"+minutes+":"+seconds;
    }


    private String getMinutes(long time) {
        int minutes = (int)time / MINUTE_FACTOR;
        if (minutes < 10) return "0"+minutes;
        else return Integer.toString(minutes);
    }


    private String getSeconds(long time) {
        int seconds = (int)time / SECOND_FACTOR;
        if (seconds < 10) return "0"+seconds;
        else return Integer.toString(seconds);
    }


    public long getPausedTime() {
        return pauseTime;
    }


    private long checkIfIsPaused(long time) {
        synchronized (lock) {
            while (paused) {
                try {
                    long pauseStart = System.currentTimeMillis();
                    lock.wait();
                    time += System.currentTimeMillis() - pauseStart;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return time;
    }


    public void pause() {
        paused = true;
    }

    public void unpause() {
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }


    public void setRunning(boolean running) {
        this.running = running;
    }
}
