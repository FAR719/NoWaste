package com.far.nowaste;

public class Utente {

    // definizione campi
    public String fullName, email;
    public double nPlastica, nOrganico, nIndifferenziata, nCarta, nVetro, nMetalli, nElettrici, nSpeciali;
    public double pPlastica, pOrganico, pIndifferenziata, pCarta, pVetro, pMetalli, pElettrici, pSpeciali;

    // costruttore nuovo utente
    public Utente(String fullName, String email){
        this.fullName = fullName;
        this.email = email;
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
        this.pElettrici = 0;
        this.pSpeciali = 0;
    }

    // costruttore utente già presente in Firebase
    public Utente(String fullName, String email,
                  double nPlastica, double pPlastica,
                  double nOrganico, double pOrganico,
                  double nIndifferenziata, double pIndifferenziata,
                  double nCarta, double pCarta,
                  double nVetro, double pVetro,
                  double nMetalli, double pMetalli,
                  double nElettrici, double pEettrici,
                  double nSpeciali, double pSpeciali){
        this.fullName = fullName;
        this.email = email;
        this.nPlastica = nPlastica;
        this.nOrganico = nOrganico;
        this.nIndifferenziata = nIndifferenziata;
        this.nCarta = nCarta;
        this.nVetro = nVetro;
        this.nMetalli = nMetalli;
        this.nElettrici = nElettrici;
        this.nSpeciali = nSpeciali;
        this.pPlastica = pPlastica;
        this.pOrganico = pOrganico;
        this.pIndifferenziata = pIndifferenziata;
        this.pCarta = pCarta;
        this.pVetro = pVetro;
        this.pElettrici = pEettrici;
        this.pSpeciali = pSpeciali;
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

    public double getnPlastica() {
        return nPlastica;
    }

    public void setnPlastica(int nPlastica) {
        this.nPlastica = nPlastica;
    }

    public double getnOrganico() {
        return nOrganico;
    }

    public void setnOrganico(int nOrganico) {
        this.nOrganico = nOrganico;
    }

    public double getnIndifferenziata() {
        return nIndifferenziata;
    }

    public void setnIndifferenziata(int nIndifferenziata) {
        this.nIndifferenziata = nIndifferenziata;
    }

    public double getnCarta() {
        return nCarta;
    }

    public void setnCarta(int nCarta) {
        this.nCarta = nCarta;
    }

    public double getnVetro() {
        return nVetro;
    }

    public void setnVetro(int nVetro) {
        this.nVetro = nVetro;
    }

    public double getnMetalli() {
        return nMetalli;
    }

    public void setnMetalli(int nMetalli) {
        this.nMetalli = nMetalli;
    }

    public double getnElettrici() {
        return nElettrici;
    }

    public void setnElettrici(int nElettrici) {
        this.nElettrici = nElettrici;
    }

    public double getnSpeciali() {
        return nSpeciali;
    }

    public void setnSpeciali(int nSpeciali) {
        this.nSpeciali = nSpeciali;
    }

    public double getpPlastica() {
        return pPlastica;
    }

    public void setpPlastica(long pPlastica) {
        this.pPlastica = pPlastica;
    }

    public double getpOrganico() {
        return pOrganico;
    }

    public void setpOrganico(long pOrganico) {
        this.pOrganico = pOrganico;
    }

    public double getpIndifferenziata() {
        return pIndifferenziata;
    }

    public void setpIndifferenziata(long pIndifferenziata) {
        this.pIndifferenziata = pIndifferenziata;
    }

    public double getpCarta() {
        return pCarta;
    }

    public void setpCarta(long pCarta) {
        this.pCarta = pCarta;
    }

    public double getpVetro() {
        return pVetro;
    }

    public void setpVetro(long pVetro) {
        this.pVetro = pVetro;
    }

    public double getpMetalli() {
        return pMetalli;
    }

    public void setpMetalli(long pMetalli) {
        this.pMetalli = pMetalli;
    }

    public double getpElettrici() {
        return pElettrici;
    }

    public void setpElettrici(long pElettrici) {
        this.pElettrici = pElettrici;
    }

    public double getpSpeciali() {
        return pSpeciali;
    }

    public void setpSpeciali(long pSpeciali) {
        this.pSpeciali = pSpeciali;
    }
}
