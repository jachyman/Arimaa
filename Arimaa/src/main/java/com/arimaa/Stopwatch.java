package com.arimaa;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Stopwatch{
    public Text timeText;
    private int minutes;
    private int seconds;
    private Timeline timeline;

    public Stopwatch() {
        minutes = 0;
        seconds = 0;
        timeText = new Text();
        setTimeline();
        setTime();
    }

    private void setTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            setTime();
        });

        timeline = new Timeline(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);
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

    public void start() {
        timeline.play();
    }

    public void pause(){
        timeline.pause();
    }

    public void resume(){
        timeline.play();
    }

    public void clear(){
        timeline.stop();
        minutes = 0;
        seconds = 0;
        setTime();
    }
}
