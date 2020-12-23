package com.far.nowaste.objects;

public class Report {
    // variabili
    public String tipologia, cassonetto, indirizzo, commento, email;
    public int day, month, year, hour, minute, second;

    // costruttore
    public Report(String tipologia, String cassonetto, String indirizzo, String commento, String email, int day, int month, int year, int hour, int minute, int second) {
        this.tipologia = tipologia;
        this.cassonetto = cassonetto;
        this.indirizzo = indirizzo;
        this.commento = commento;
        this.email = email;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    // costruttore vuoto per fb
    private Report(){};

    // getter

    public String getTipologia() {
        return tipologia;
    }

    public String getCassonetto() {
        return cassonetto;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getCommento() {
        return commento;
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
