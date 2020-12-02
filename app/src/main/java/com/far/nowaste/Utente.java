package com.far.nowaste;

public class Utente {

    // definizione campi
    private String nome, email;

    // costruttore
    private Utente(String nome, String email){
        this.nome = nome;
        this.email = email;
    }

    //costruttore vuoto utile per FireBase
    private Utente(){}

    // getter e setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
