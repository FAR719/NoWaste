package com.far.nowaste.Objects;

public class Evento {

    // definizione campi
    public String title, description;
    public int year, month, day;

    // costruttore
    public Evento(String title, String description, int year, int month, int day){
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
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
