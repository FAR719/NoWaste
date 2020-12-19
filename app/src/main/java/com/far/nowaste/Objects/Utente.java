package com.far.nowaste.Objects;

public class Utente {

    // definizione campi
    public String fullName, email, image;
    public int nPlastica, nOrganico, nIndifferenziata, nCarta, nVetro, nMetalli, nElettrici, nSpeciali;
    public double pPlastica, pOrganico, pIndifferenziata, pCarta, pVetro, pMetalli, pElettrici, pSpeciali;
    public boolean isGoogle;

    // costruttore nuovo utente
    public Utente(String fullName, String email, String image, boolean isGoogle){
        this.fullName = fullName;
        this.email = email;
        this.image = image;
        this.nPlastica = 0;
        this.nOrganico = 0;
        this.nIndifferenziata = 0;
        this.nCarta = 0;
        this.nVetro = 0;
        this.nMetalli = 0;
        this.nElettrici = 0;
        this.nSpeciali = 0;
        this.pPlastica = 0;
        this.pOrganico = 0;
        this.pIndifferenziata = 0;
        this.pCarta = 0;
        this.pVetro = 0;
        this.pMetalli = 0;
        this.pElettrici = 0;
        this.pSpeciali = 0;
        this.isGoogle = isGoogle;
    }

    //costruttore vuoto utile per FireBase
    private Utente(){}

    // getter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public int getnPlastica() {
        return nPlastica;
    }

    public int getnOrganico() {
        return nOrganico;
    }

    public int getnIndifferenziata() {
        return nIndifferenziata;
    }

    public int getnCarta() {
        return nCarta;
    }

    public int getnVetro() {
        return nVetro;
    }

    public int getnMetalli() {
        return nMetalli;
    }

    public int getnElettrici() {
        return nElettrici;
    }

    public int getnSpeciali() {
        return nSpeciali;
    }

    public double getpPlastica() {
        return pPlastica;
    }

    public double getpOrganico() {
        return pOrganico;
    }

    public double getpIndifferenziata() {
        return pIndifferenziata;
    }

    public double getpCarta() {
        return pCarta;
    }

    public double getpVetro() {
        return pVetro;
    }

    public double getpMetalli() {
        return pMetalli;
    }

    public double getpElettrici() {
        return pElettrici;
    }

    public double getpSpeciali() {
        return pSpeciali;
    }

    public boolean isGoogle() {
        return isGoogle;
    }
}
