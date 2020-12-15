package com.far.nowaste;

public class Tickets {
    // variabili
    public String oggetto, email, data;
    public boolean stato;

    // costruttore
    public Tickets(String oggetto, String email, String data, boolean stato) {
        this.oggetto = oggetto;
        this.email = email;
        this.data = data;
        this.stato = stato;
    }

    //costruttore vuoto per firebase
    private Tickets(){}

    public String getOggetto() {
        return oggetto;
    }

    public String getEmail() {
        return email;
    }

    public String getData() {
        return data;
    }

    public boolean getStato() {
        return stato;
    }
}
