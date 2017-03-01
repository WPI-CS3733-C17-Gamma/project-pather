package app.display;

import app.applicationControl.ApplicationController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleTimer {
    final Logger logger = LoggerFactory.getLogger(IdleTimer.class);

    private double time;

    PatientController patientController;

    Timeline timer;


    public void setTime (double time) {
        if (time >= 1000) {
            this.time = time;
        }
        else {
            this.time = 1000;
        }
    }

    public double getTime() {
        return time;
    }

    // start the timer running
    public void resetTimer (PatientMemento memento) {
        timer.stop();
        timer = new Timeline(new KeyFrame (Duration.millis(time)));
        timer.setOnFinished((ActionEvent e) -> {
            logger.info("timer ended");
            patientController.revertState(memento);
        });
        timer.play();
    }

    /**
     * @param patientController controller that this timer will talk to
     */
    public void setPatientController (PatientController patientController) {
        this.patientController = patientController;
    }

    /**
       keep constructor private
       creates a timer with a default time of 10 seconds
     */
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
