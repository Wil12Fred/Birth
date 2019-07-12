package com.delycomps.birth.Entidades;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contacto {
    @SerializedName("idUser")
    @Expose
    private int idUser;
    @SerializedName("hideYear")
    @Expose
    private int hideYear;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phonenumber")
    @Expose
    private String phonenumber;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("surnames")
    @Expose
    private String surnames;
    @SerializedName("names")
    @Expose
    private String names;
    @SerializedName("tokenFB")
    @Expose
    private String tokenFB;
    @SerializedName("codUpdate")
    @Expose
    private String codUpdate;

    public String getCodUpdate() {
        return codUpdate;
    }

    public void setCodUpdate(String codUpdate) {
        this.codUpdate = codUpdate;
    }

    public Contacto(int idUser, int hideYear, String name, String phonenumber, String birthday, String surnames, String names, String tokenFB) {
        this.idUser = idUser;
        this.hideYear = hideYear;
        this.name = name;
        this.phonenumber = phonenumber;
        this.birthday = birthday;
        this.surnames = surnames;
        this.names = names;
        this.tokenFB = tokenFB;
    }

    public Contacto(String name, String phonenumber) {
        this.name = name;
        this.phonenumber = phonenumber;
    }

    public String getTokenFB() {
        return tokenFB;
    }

    public void setTokenFB(String tokenFB) {
        this.tokenFB = tokenFB;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getHideYear() {
        return hideYear;
    }

    public void setHideYear(int hideYear) {
        this.hideYear = hideYear;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
