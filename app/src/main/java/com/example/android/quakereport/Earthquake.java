package com.example.android.quakereport;

/**
 * Created by darip on 13-01-2018.
 */

public class Earthquake {

    private String mag, location, place, date, time, url;

    public Earthquake(String mag, String location, String place, String date, String time, String url){
        this.mag = mag;
        this.location = location;
        this.place = place;
        this.date = date;
        this.time = time;
        this.url = url;
    }

    public String getMag(){
        return mag;
    }

    public String getUrl(){
        return url;
    }

    public String getPlace(){
        return place;
    }

    public String getDate(){
        return date;
    }
    public String getLocation(){
        return location;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String t){
        this.time = t;
    }

    public void setUrl(String t){
        this.url = t;
    }

    public void setLocation(String l){
        this.location = l;
    }

    public void setMag(String m){
        this.mag = m;
    }
    public void setPlace(String p){
        this.place = p;
    }
    public void setDate(String d){
        this.date = d;
    }

}
