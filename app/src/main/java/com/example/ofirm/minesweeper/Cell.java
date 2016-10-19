package com.example.ofirm.minesweeper;

import android.content.Context;

public class Cell {

    public final static int COVERED = 100;
    public final static int MINED = 101;
    public final static int FLAGGED = 102;
    public final static int EMPTY = 103;
    public final static int RED_MINE = 104;
    public final static int NUMBER = 105;
    public final static int WRONG_MINED = 106;


    private int position;                   //[i][j]
    private int numberOfMines;              // = 0
    private int status;                     // = COVERED;

    private boolean isMined;
    private boolean clickable;            // can Cell accept click events

    protected Context context;

    public Cell(Context context) {
        this.context = context;
        setDefaults();
    }

    public void setClickable(boolean isClickable) {
        this.clickable = isClickable;
    }

    public boolean getClickable() {
        return clickable;
    }

    private void setDefaults() {
        setClickable(true);
        isMined = false;
        setStatus(COVERED);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return this.position;
    }

    public void setMined(){
        this.isMined = true;
    }

    public boolean getIsMined(){
        return this.isMined;
    }

    public int getNumberOfMines(){
        return this.numberOfMines;
    }

    public void setNumberOfMines(int numberOfMines){
        this.numberOfMines = numberOfMines;
    }

    public void increaseNumberOfMines(){
        this.numberOfMines++;
    }

    public void decreaseNumberOfMines(){
        this.numberOfMines--;
    }

    public void makeMeCovered(){
        setClickable(true);
        setStatus(COVERED);
    }

}
