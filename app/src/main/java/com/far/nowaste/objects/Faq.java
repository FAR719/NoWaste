package com.far.nowaste.objects;

public class Faq {
    String domanda, risposta;

    // costruttore
    public Faq(String domanda, String risposta) {
        this.domanda = domanda;
        this.risposta = risposta;
    }

    // costruttore vuoto per firebase
    public Faq(){}

    //getter
    public String getDomanda() {
        return domanda;
    }

    public String getRisposta() {
        return risposta;
    }
}
