package com.far.nowaste;

public class Tickets {
    // variabili
    public String oggetto, email, day, month, year;
    public boolean stato;

    // costruttore
    public Tickets(String oggetto, String email, String day, String month, String year, boolean stato) {
        this.oggetto = oggetto;
        this.email = email;
        this.day = day;
        this.month = month;
        this.year = year;
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

    public String getDay() { return day; }

    public String getMonth() { return month; }

    public String getYear() { return year; }

    public boolean getStato() {
        return stato;
    }
}
