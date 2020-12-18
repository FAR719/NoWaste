package com.far.nowaste.Objects;

public class Tickets {
    // variabili
    public String oggetto, email;
    public int day, month, year, hour, minute, second;
    public boolean stato;

    // costruttore


    public Tickets(String oggetto, String email, int day, int month, int year, int hour, int minute, int second, boolean stato) {
        this.oggetto = oggetto;
        this.email = email;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.stato = stato;
    }

    //costruttore vuoto per firebase
    private Tickets(){}

    public String getOggetto() {
        return oggetto;
    }

    public String getEmail() {
        return email;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public boolean getStato() {
        return stato;
    }
}
