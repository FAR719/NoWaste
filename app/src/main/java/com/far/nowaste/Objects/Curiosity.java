package com.far.nowaste.Objects;

public class Curiosity {

    public String titolo;
    public String descrizione;
    public String etichetta;

    // costruttore
    public Curiosity(String titolo, String descrizione, String etichetta){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.etichetta = etichetta;
    }

    // costruttore vuoto utile per FireBase
    private Curiosity(){}

    // getter

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getEtichetta() {
        return etichetta;
    }
}
