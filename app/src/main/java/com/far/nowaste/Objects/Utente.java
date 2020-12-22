package com.far.nowaste.Objects;

public class Utente {

    // definizione campi
    public String fullName, email, image, city, quartiere;
    public int nPlastica, nOrganico, nSecco, nCarta, nVetro, nMetalli, nElettrici, nSpeciali;
    public double pPlastica, pOrganico, pSecco, pCarta, pVetro, pMetalli, pElettrici, pSpeciali;
    public boolean isGoogle, isOperatore;

    // costruttore nuovo utente
    public Utente(String fullName, String email, String image, boolean isGoogle, boolean isOperatore, String city, String quartiere){
        this.fullName = fullName;
        this.email = email;
        this.image = image;
        this.nPlastica = 0;
        this.nOrganico = 0;
        this.nSecco = 0;
        this.nCarta = 0;
        this.nVetro = 0;
        this.nMetalli = 0;
        this.nElettrici = 0;
        this.nSpeciali = 0;
        this.pPlastica = 0;
        this.pOrganico = 0;
        this.pSecco = 0;
        this.pCarta = 0;
        this.pVetro = 0;
        this.pMetalli = 0;
        this.pElettrici = 0;
        this.pSpeciali = 0;
        this.isGoogle = isGoogle;
        this.isOperatore = isOperatore;
        this.city = city;
        this.quartiere = quartiere;
    }

    //costruttore vuoto utile per FireBase
    private Utente(){}

    // getter e setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getnPlastica() {
        return nPlastica;
    }

    public void setnPlastica(int nPlastica) {
        this.nPlastica = nPlastica;
    }

    public int getnOrganico() {
        return nOrganico;
    }

    public void setnOrganico(int nOrganico) {
        this.nOrganico = nOrganico;
    }

    public int getnSecco() {
        return nSecco;
    }

    public void setnSecco(int nSecco) {
        this.nSecco = nSecco;
    }

    public int getnCarta() {
        return nCarta;
    }

    public void setnCarta(int nCarta) {
        this.nCarta = nCarta;
    }

    public int getnVetro() {
        return nVetro;
    }

    public void setnVetro(int nVetro) {
        this.nVetro = nVetro;
    }

    public int getnMetalli() {
        return nMetalli;
    }

    public void setnMetalli(int nMetalli) {
        this.nMetalli = nMetalli;
    }

    public int getnElettrici() {
        return nElettrici;
    }

    public void setnElettrici(int nElettrici) {
        this.nElettrici = nElettrici;
    }

    public int getnSpeciali() {
        return nSpeciali;
    }

    public void setnSpeciali(int nSpeciali) {
        this.nSpeciali = nSpeciali;
    }

    public double getpPlastica() {
        return pPlastica;
    }

    public void setpPlastica(double pPlastica) {
        this.pPlastica = pPlastica;
    }

    public double getpOrganico() {
        return pOrganico;
    }

    public void setpOrganico(double pOrganico) {
        this.pOrganico = pOrganico;
    }

    public double getpSecco() {
        return pSecco;
    }

    public void setpSecco(double pSecco) {
        this.pSecco = pSecco;
    }

    public double getpCarta() {
        return pCarta;
    }

    public void setpCarta(double pCarta) {
        this.pCarta = pCarta;
    }

    public double getpVetro() {
        return pVetro;
    }

    public void setpVetro(double pVetro) {
        this.pVetro = pVetro;
    }

    public double getpMetalli() {
        return pMetalli;
    }

    public void setpMetalli(double pMetalli) {
        this.pMetalli = pMetalli;
    }

    public double getpElettrici() {
        return pElettrici;
    }

    public void setpElettrici(double pElettrici) {
        this.pElettrici = pElettrici;
    }

    public double getpSpeciali() {
        return pSpeciali;
    }

    public void setpSpeciali(double pSpeciali) {
        this.pSpeciali = pSpeciali;
    }

    public boolean isGoogle() {
        return isGoogle;
    }

    public boolean isOperatore() {
        return isOperatore;
    }

    public void setOperatore(boolean isOperatore) {
        this.isOperatore = isOperatore;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getQuartiere() {
        return quartiere;
    }

    public void setQuartiere(String quartiere) {
        this.quartiere = quartiere;
    }
}
