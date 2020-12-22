package com.far.nowaste.Objects;

public class Evento {

    // definizione campi
    public String email, title, description;
    public int year, month, day;

    // costruttore
    public Evento(String email, String title, String description, int year, int month, int day){
        this.email = email;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // costruttore vuoto utile per FireBase
    private Evento(){}

    // getter
    public String getEmail() {
        return email;
    }

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
