package com.far.nowaste;

public class Evento {

    // definizione campi
    public String title;
    public int year, month, day;

    // costruttore
    public Evento(String title, int year, int month, int day){
        this.title = title;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // costruttore vuoto utile per FireBase
    private Evento(){}

    // getter
    public String getTitle(){
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
