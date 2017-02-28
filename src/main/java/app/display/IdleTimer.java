package app.display;

import app.applicationControl.ApplicationController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

/**
 * Created by saahil claypool on 2/27/2017.
 */
public class IdleTimer {

    private double time;

    PatientController patientController;

    Timeline timer;

    public void setTime (double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    // start the timer running
    public void resetTimer () {
        timer.stop();
        timer = new Timeline(new KeyFrame (Duration.millis(time)));
        timer.setOnFinished((ActionEvent e) -> {
            System.out.println("timer ended");
            patientController.revertState();
        });
        timer.play();
        System.out.println("Timer Started");
    }

    public void setPatientController (PatientController patientController) {
        System.out.println("Patient controller is " + patientController);
        this.patientController = patientController;
    }

    private IdleTimer () {
        this.time = 10000;
        this.timer = new Timeline(new KeyFrame (Duration.millis(time)));
    }

    /**
     * get the singleton
     */
    private static class SingletonHelper {
        private static IdleTimer instance = new IdleTimer();
        private static IdleTimer getInstance() {
            return instance;
        }
    }
    /**
     * get the instance of the IdleTimer
     * @return
     */
    public static IdleTimer getInstance() {
        return SingletonHelper.getInstance();
    }




}
