package com.far.nowaste.objects;

public class Oil {

    String tipo;
    double punteggio;
    int quantita, year, month;

    //costruttore vuoto utile per FireBase
    public Oil() {}

    public String getTipo() {
        return tipo;
    }

    public double getPunteggio() {
        return punteggio;
    }

    public int getQuantita() {
        return quantita;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
