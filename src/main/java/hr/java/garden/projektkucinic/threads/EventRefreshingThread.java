package hr.java.garden.projektkucinic.threads;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.controllers.AllPlanedActivitiesController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class EventRefreshingThread implements Runnable {

    private AllPlanedActivitiesController controller;

    public EventRefreshingThread(AllPlanedActivitiesController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    if (isMidnight()) {
                        Platform.runLater(() -> {
                            controller.show();
                        });
                    }
                }),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        while (UrbanGardenApplication.getMainStage().isShowing()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        timeline.stop();

    }


    private boolean isMidnight() {
        LocalTime now = LocalTime.now(ZoneId.of("Europe/Zagreb"));
        LocalTime midnight = LocalTime.MIDNIGHT;
        long differenceInMillis = Math.abs(ChronoUnit.MILLIS.between(now, midnight));
        return differenceInMillis < 250;
    }


}
