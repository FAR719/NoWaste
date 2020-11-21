package com.far.nowaste;

public class Rifiuto {

    // definizione campi
    private String nome;
    private String descrizione;
    private String materiale;
    private String smaltimento;
    private int punteggio;

    // costruttore
    private Rifiuto(String nome, String descrizione, String materiale, String smaltimento, int punteggio){
        this.nome = nome;
        this.descrizione = descrizione;
        this.materiale = materiale;
        this.smaltimento = smaltimento;
        this.punteggio = punteggio;
    }

    //costruttore vuoto utile per FireBase
    private Rifiuto(){}

    // getter e setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getMateriale() {
        return materiale;
    }

    public void setMateriale(String materiale) {
        this.materiale = materiale;
    }

    public String getSmaltimento() {
        return smaltimento;
    }

    public void setSmaltimento(String smaltimento) {
        this.smaltimento = smaltimento;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
