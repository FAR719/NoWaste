package com.far.nowaste.Objects;

public class Bug {

    public String oggetto, testo ,email;
    public int day, month, year, hour, minute, second;

    // costruttore
    public Bug(String oggetto, String testo, String email, int day, int month, int year, int hour, int minute, int second) {
        this.oggetto = oggetto;
        this.testo = testo;
        this.email = email;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    // costruttore vuoto per firebase
    public Bug(){}

    // getter
    public String getOggetto() {
        return oggetto;
    }

    public String getTesto() {
        return testo;
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
}
