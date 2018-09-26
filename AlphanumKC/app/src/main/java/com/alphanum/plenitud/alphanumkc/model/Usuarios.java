package com.alphanum.plenitud.alphanumkc.model;

import android.content.pm.ConfigurationInfo;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuarios {

    private String idUser;
    private String nomeUser;
    private String dataNascUser;
    private String emailUser;
    private String pontosUser;
    private String telefoneUser;
    private String senhaUser;
    private Double latitudeUser;
    private Double longitudeUser;
    private Double saldo;


    public void salvar(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
        referenceFirebase.child("usuarios").child(getIdUser()).setValue(this);
    }

    public Usuarios() {
    }

    public Usuarios(String idUser, String nomeUser, String dataNascUser, String emailUser, String pontosUser, String telefoneUser, String senhaUser) {
        this.idUser = idUser;
        this.nomeUser = nomeUser;
        this.dataNascUser = dataNascUser;
        this.emailUser = emailUser;
        this.pontosUser = pontosUser;
        this.telefoneUser = telefoneUser;
        this.senhaUser = senhaUser;
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getDataNascUser() {
        return dataNascUser;
    }

    public void setDataNascUser(String dataNascUser) {
        this.dataNascUser = dataNascUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPontosUser() {
        return pontosUser;
    }

    public void setPontosUser(String pontosUser) {
        this.pontosUser = pontosUser;
    }

    public String getTelefoneUser() {
        return telefoneUser;
    }

    public void setTelefoneUser(String telefoneUser) {
        this.telefoneUser = telefoneUser;
    }

    @Exclude
    public String getSenhaUser() {
        return senhaUser;
    }

    public void setSenhaUser(String senhaUser) {
        this.senhaUser = senhaUser;
    }

    public Double getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(Double latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public Double getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(Double longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}
