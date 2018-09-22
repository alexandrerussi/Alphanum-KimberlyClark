package com.alphanum.plenitud.alphanumkc.model;

import com.alphanum.plenitud.alphanumkc.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Dispenser {

    private String idDisp;
    private String nomeDisp;
    private String localDisp;
    private String precoDisp;
    private Integer qtdAtual;
    private Integer qtdMax;
    private Integer qtdDispenserPorCem;
    private float latitude;
    private float longitude;





    public Dispenser() {
    }

    public void salvar(){
        FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getFirebase();
        referenceFirebase.child("dispenser").child(getIdDisp()).setValue(this);
    }

    @Exclude
    private String getIdDisp() {
        return idDisp;
    }

    public void setIdDisp(String idDisp) {
        this.idDisp = idDisp;
    }

    public String getNomeDisp() {
        return nomeDisp;
    }

    public void setNomeDisp(String nomeDisp) {
        this.nomeDisp = nomeDisp;
    }

    public String getPrecoDisp() {
        return precoDisp;
    }

    public void setPrecoDisp(String precoDisp) {
        this.precoDisp = precoDisp;
    }

    public Integer getQtdAtual() {
        return qtdAtual;
    }

    public void setQtdAtual(Integer qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public Integer getQtdMax() {

        return qtdMax;
    }

    public void setQtdMax(Integer qtdMax) {
        this.qtdMax = qtdMax;
    }

    public String getLocalDisp() {
        return localDisp;
    }

    public void setLocalDisp(String localDisp) {
        this.localDisp = localDisp;
    }

    public Integer getQtdDispenserPorCem() {
        return qtdDispenserPorCem;
    }

    public void setQtdDispenserPorCem(Integer qtdDispenserPorCem) {
        this.qtdDispenserPorCem = qtdDispenserPorCem;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

}
