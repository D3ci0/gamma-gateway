package com.gamma.gateway.model.dto;

public class SignStoreResponseMessage {
    private String resultCode;

    public SignStoreResponseMessage() {
    }

    public SignStoreResponseMessage(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
