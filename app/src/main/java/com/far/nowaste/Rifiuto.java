package com.far.nowaste;

public class Rifiuto {

    // definizione campi
    public String nome, descrizione, materiale, smaltimento, immagine;
    public double punteggio;

    // costruttore
    public Rifiuto(String nome, String descrizione, String materiale, String smaltimento, double punteggio, String immagine){
        this.nome = nome;
        this.descrizione = descrizione;
        this.materiale = materiale;
        this.smaltimento = smaltimento;
        this.punteggio = punteggio;
        this.immagine = immagine;
    }

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

    public double getPunteggio() {
        return punteggio;
    }

    public String getImmagine() {
        return immagine;
    }
}
