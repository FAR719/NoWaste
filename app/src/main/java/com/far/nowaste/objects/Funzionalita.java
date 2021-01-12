package com.far.nowaste.objects;

public class Funzionalita {
    public String nome, testo;

    // costruttore
    public Funzionalita(String nome, String testo) {
        this.nome = nome;
        this.testo = testo;
    }

    // costruttore vuoto per firebase
    public Funzionalita(){}

    //getter
    public String getNome() {
        return nome;
    }

    public String getTesto() {
        return testo;
    }
}
