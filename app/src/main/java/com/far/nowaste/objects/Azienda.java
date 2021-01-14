package com.far.nowaste.objects;

public class Azienda {

    String nome, indirizzo, telefono, email, lun, mar, mer, gio, ven, sab, dom;

    public Azienda(String nome, String indirizzo, String telefono, String email, String lun, String mar,
                   String mer, String gio, String ven, String sab, String dom) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.email = email;
        this.lun = lun;
        this.mar = mar;
        this.mer = mer;
        this.gio = gio;
        this.ven = ven;
        this.sab = sab;
        this.dom = dom;
    }

    public String getNome() {
        return nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getLun() {
        return lun;
    }

    public String getMar() {
        return mar;
    }

    public String getMer() {
        return mer;
    }

    public String getGio() {
        return gio;
    }

    public String getVen() {
        return ven;
    }

    public String getSab() {
        return sab;
    }

    public String getDom() {
        return dom;
    }
}
