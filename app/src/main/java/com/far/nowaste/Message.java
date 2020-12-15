package com.far.nowaste;

public class Message {
    // variabili
    String descrizione;
    boolean operatore;

    public Message(String descrizione, boolean operatore) {
        this.descrizione = descrizione;
        this.operatore = operatore;
    }

    // costruttore vuoto per Firebase
    private Message(){}

    // getter

    public String getDescrizione() {
        return descrizione;
    }

    public boolean isOperatore() {
        return operatore;
    }
}
