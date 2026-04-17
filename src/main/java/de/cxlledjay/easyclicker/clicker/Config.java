package de.cxlledjay.easyclicker.clicker;


public class Config {


    public enum ClickMode {
        HOLDING,
        INDIVIDUAL
    }


    // settings that shall be tracked
    private ClickMode mode;
    private int speed;
    private final int maxSpeed = 20;



    // constructor

    public Config(ClickMode mode, int speed){
        this.mode = mode;
        this.speed = speed;
    }



    // getter and setter

    public ClickMode getClickMode () {
        return mode;
    }

    public void setClickMode (ClickMode mode){
        this.mode = mode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }



}
