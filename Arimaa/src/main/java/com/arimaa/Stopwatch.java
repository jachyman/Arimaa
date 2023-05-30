package com.arimaa;

import javafx.scene.text.Text;

public class Stopwatch implements Runnable {
    public Text timeText;
    private int minutes, seconds;
    private boolean isStopped;
    Thread thread;

    public Stopwatch() {
        minutes = 0;
        seconds = 0;
        isStopped = true;
        timeText = new Text();
        thread = null;
        setTime();
    }

    public void setTime() {

        if (seconds == 60){
            seconds = 0;
            minutes++;
        }

        String m = minutes >= 10 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);
        String s = seconds >= 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);

        timeText.setText(m + ":" + s);
    }
    @Override
    public void run() {
        while (!isStopped) {
            try {
                Thread.sleep(1000);
                seconds++;
                setTime();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        isStopped = false;
    }

    public void stop(){
        isStopped = true;
    }

    public void resume() {
        isStopped = false;
    }

}
