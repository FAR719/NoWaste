package com.far.nowaste.objects;

public class Rifiuto {

    // definizione campi
    public String nome, descrizione, materiale, smaltimento, immagine;
    public double carbon_dioxide, energy, oil, water, fertilizer, sand;
    public int ntipo;

    // costruttore vuoto utile per FireBase
    private Rifiuto(){}

    // getter
    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getMateriale() {
        return materiale;
    }

    public String getSmaltimento() {
        return smaltimento;
    }

    public double getCarbon_dioxide() {
        return carbon_dioxide;
    }

    public double getEnergy() {
        return energy;
    }

    public double getOil() {
        return oil;
    }

    public double getWater() {
        return water;
    }

    public double getSand() {
        return sand;
    }

    public double getFertilizer() {
        return fertilizer;
    }

    public String getImmagine() {
        return immagine;
    }

    public int getNtipo() {
        return ntipo;
    }
}
