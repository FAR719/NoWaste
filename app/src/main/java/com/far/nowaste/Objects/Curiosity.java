package com.far.nowaste.Objects;

public class Curiosity {

    public String descrizione;
    public String etichetta;

    // costruttore
    public Curiosity(String descrizione, String etichetta){
        this.descrizione = descrizione;
        this.etichetta = etichetta;
    }

    // costruttore vuoto utile per FireBase
    private Curiosity(){}

    // getter
    public String getDescrizione() {
        return descrizione;
    }

    public String getEtichetta() {
        return etichetta;
    }
}
