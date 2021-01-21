package com.far.nowaste.objects;

public class Saving {

    String tipo;
    double punteggio;
    int year, month;
    int ntipo;

    public Saving(String tipo, double punteggio, int year, int month, int ntipo) {
        this.tipo = tipo;
        this.punteggio = punteggio;
        this.year = year;
        this.month = month;
        this.ntipo = ntipo;
    }

    //costruttore vuoto utile per FireBase
    public Saving() {}

    public String getTipo() {
        return tipo;
    }

    public double getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(double punteggio) {
        this.punteggio = punteggio;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getNtipo() {
        return ntipo;
    }
}
