package com.far.nowaste.Objects;

public class Message {
    // variabili
    public String testo;
    public int day, month, year, hour, minute, second;
    public boolean operatore;

    // costruttore
    public Message(String testo, int day, int month, int year, int hour, int minute, int second, boolean operatore) {
        this.testo = testo;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.operatore = operatore;
    }

    // costruttore vuoto per Firebase
    private Message(){}

    // getter

    public String getTesto() {
        return testo;
    }

    public boolean isOperatore() {
        return operatore;
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
