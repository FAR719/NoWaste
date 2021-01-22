package com.far.nowaste.objects;

public class Luogo {

    String nome, categoria;
    double lat, lng;
    boolean visible;

    //costruttore vuoto utile per Firebase
    private Luogo() {}

    // getter
    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isVisible() { return visible; }

    // setter
    public void setVisible(boolean visible) { this.visible = visible; }
}
