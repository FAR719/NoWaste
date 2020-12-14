package com.far.nowaste;

public class Tickets {
    // variabili
    public String oggetto, emailT, data;
    public boolean stato;

    // costruttore
    public Tickets(String oggetto, String emailT, String data, boolean stato) {
        this.oggetto = oggetto;
        this.emailT = emailT;
        this.data = data;
        this.stato = stato;
    }

    //costruttore vuoto per firebase
    private Tickets(){}

    public String getOggetto() {
        return oggetto;
    }

    public String getEmailT() {
        return emailT;
    }

    public String getData() {
        return data;
    }

    public boolean isStato() {
        return stato;
    }
}
