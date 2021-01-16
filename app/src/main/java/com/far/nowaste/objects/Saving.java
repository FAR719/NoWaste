package com.far.nowaste.objects;

public class Saving {

    String tipo;
    double punteggio;
    int quantita, year, month;
    int ntipo;

    public Saving(String tipo, double punteggio, int year, int month, int ntipo) {
        this.tipo = tipo;
        this.punteggio = punteggio;
        this.year = year;
        this.month = month;
        this.ntipo = ntipo;
        this.quantita = 1;
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

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
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
