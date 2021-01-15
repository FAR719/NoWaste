package com.far.nowaste.objects;

public class Utente {

    // definizione campi
    public String fullName, email, image, city, quartiere;
    public boolean isGoogle, isOperatore;

    // costruttore nuovo utente
    public Utente(String fullName, String email, String image, boolean isGoogle, boolean isOperatore, String city, String quartiere){
        this.fullName = fullName;
        this.email = email;
        this.image = image;
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
