package com.example.ofirm.minesweeper;

import android.content.Context;


/**
 * Created by OfirMonis on 27/12/2015.
 */
public class Score {
    private int Id;
    private int Level;
    private int Time;
    private String Name;
    private double Logitude;
    private double Latitude;
    protected Context context;
    public final static int MAX_SCORES = 10;

    public double getLogitude() {
        return Logitude;
    }

    public void setLogitude(double logitude) {
        Logitude = logitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public Score(int id,int level,int time,String name, double longitude,double latitude){
        this.Name = name;
        this.Time = time;
        this.Level = level;
        this.Latitude = latitude;
        this.Logitude = longitude;
        this.Id = id;


    }
    public Score(){
        this.setName("");
        this.setTime(0);
        this.setLogitude(0);
        this.setLatitude(0);
        this.setLevel(0);
    }

    public Score(Context context) {
        this.context = context;

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public void initScore(Score score){
        score.setName("");
        score.setTime(0);
        score.setLogitude(0);
        score.setLatitude(0);
        //score.setLevel(0);
    }


    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public String getName() {
        return Name;
    }

    public void setTime(int time) {
        Time= time;
    }

    public void setName(String name) {
        Name = name;
    }


    public int getTime() {
        return Time;
    }




}

