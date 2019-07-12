package com.delycomps.birth.WebService;

import com.delycomps.birth.Entidades.Contacto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("pin")
    @Expose
    private String pin;

    @SerializedName("result")
    @Expose
    private Contacto result;

    public String getMsg() {
        return msg;
    }

    @SerializedName("msg")
    @Expose
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Contacto getResult() {
        return result;
    }

    public void setResult(Contacto result) {
        this.result = result;
    }
}
