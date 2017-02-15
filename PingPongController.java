package pong;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
//import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class PingPongController {

    final int PADDLE_MOVEMENT_INCREMENT = 30;
    final int BALL_MOVEMENT_INCREMENT = 3;

    DoubleProperty currentKidPaddleY = new SimpleDoubleProperty();
    DoubleProperty ballCenterX = new SimpleDoubleProperty();
    DoubleProperty ballCenterY = new SimpleDoubleProperty();
    DoubleProperty currentCompPaddleY = new SimpleDoubleProperty();

    double allowedPaddleTopY;
    double allowedPaddleBottomY;
    double centerTableY;
    double initialCompPaddleY;

    @FXML Rectangle table;
    @FXML Rectangle compPaddle;
    @FXML Rectangle kidPaddle;
    @FXML Circle ball;

    Timeline timeline;

    int compScore;
    int kidScore;

    public void initialize(){
        currentKidPaddleY.set(kidPaddle.getLayoutY());
        kidPaddle.layoutYProperty().bind(currentKidPaddleY);

        allowedPaddleTopY = PADDLE_MOVEMENT_INCREMENT;
        allowedPaddleBottomY = table.getHeight() - kidPaddle.getHeight() - PADDLE_MOVEMENT_INCREMENT;

        ballCenterX.set(ball.getCenterX());
        ballCenterY.set(ball.getCenterY());

        ball.centerXProperty().bind(ballCenterX);
        ball.centerYProperty().bind(ballCenterY);

        centerTableY = table.getHeight()/2;

        initialCompPaddleY = compPaddle.getLayoutY();

        currentCompPaddleY.set(initialCompPaddleY);
        compPaddle.layoutYProperty().bind(currentCompPaddleY);

    }

    public void keyReleasedHandler (KeyEvent event){

        KeyCode keyCode = event.getCode();
//        System.out.println("You pressed " + keyCode);

        switch (keyCode){

            case UP:
                process_key_Up();
                break;
            case DOWN:
                process_key_Down();
                break;
            case N:
                process_key_N();
                break;
            case Q:
                Platform.exit();
                break;
            case S:
                process_key_S();
                break;

        }
    }

    private void process_key_Up (){
//        System.out.println("Processing the Up key");
        if (currentKidPaddleY.get() > allowedPaddleTopY){
            currentKidPaddleY.set(currentKidPaddleY.get() - PADDLE_MOVEMENT_INCREMENT);
        }
    }

    private void process_key_Down(){
        System.out.println("Processing the Down key!!!");

        if (currentKidPaddleY.get() < allowedPaddleBottomY){
            currentKidPaddleY.set(currentKidPaddleY.get() + PADDLE_MOVEMENT_INCREMENT);
        }
    }

    private void process_key_N(){
        System.out.println("Processing the N key");
    }

    private void process_key_S(){

//        System.out.println("Processing the S key");
        ballCenterY.set(currentKidPaddleY.doubleValue() + kidPaddle.getHeight()/2);
        ballCenterX.set(kidPaddle.getLayoutX());

        moveTheBall();

    }

    private void moveTheBall(){

        Random randomYGenerator = new Random();
        double randomYIncrenent = randomYGenerator.nextInt(BALL_MOVEMENT_INCREMENT);

        final boolean isServingFromTop = (ballCenterY.get() <= centerTableY)?true:false;


        KeyFrame keyFrame = new KeyFrame(new Duration(10), event -> {

            if (ballCenterX.get() >= -20) {
                ballCenterX.set(ballCenterX.get() - BALL_MOVEMENT_INCREMENT);

                if (isServingFromTop) {
                    ballCenterY.set(ballCenterY.get() + randomYIncrenent);
                    currentCompPaddleY.set(currentCompPaddleY.get() + 1);
                } else {
                    ballCenterY.set(ballCenterY.get() - randomYIncrenent);
                    currentCompPaddleY.set(currentCompPaddleY.get() - 1);
                }


                if (checkForBallPaddleContact(compPaddle)) {
                    timeline.stop();
                    currentCompPaddleY.set(initialCompPaddleY);
                    bounceTheBall();
                };
            }
            else {
                timeline.stop();
                currentCompPaddleY.set(initialCompPaddleY);
                updateScore();
                }
        });

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

/*
        timeline = new Timeline();
        timeline.setCycleCount(1);

        KeyValue keyValue = new KeyValue(ballCenterX, 0);
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), keyValue);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
*/
    }

    private boolean checkForBallPaddleContact(Rectangle paddle){

        if (ball.intersects(paddle.getBoundsInParent())){
            return true;
        }
        else {
            return false;
        }
    }

    private void bounceTheBall(){

        double theBallOffTheTable = table.getWidth() + 20;

        KeyFrame keyFrame = new KeyFrame(new Duration(10), event -> {

            System.out.println(ballCenterX);
            if(ballCenterX.get() < theBallOffTheTable){
                ballCenterX.set(ballCenterX.get() + BALL_MOVEMENT_INCREMENT);

                if (checkForBallPaddleContact(kidPaddle)){
                    timeline.stop();
                    moveTheBall();
                };

            }
            else {
                timeline.stop();
                updateScore();
            }
        });

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
    }

    private void updateScore(){

        if (ballCenterX.get() > table.getWidth()){
            compScore ++;
        }
        else if (ballCenterY.get() > 0 && ballCenterY.get() <= table.getHeight()){
            kidScore ++;
        }
        else {
            compScore ++;
        }

        System.out.println("Computer: " + compScore + " and Kid: " + kidScore);
    }

}
