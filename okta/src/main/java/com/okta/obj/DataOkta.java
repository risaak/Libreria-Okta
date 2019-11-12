package com.okta.obj;

import android.os.Bundle;

public class DataOkta {

    private String oktaUrl;
    private String clientId;
    private String idp;
    private String apiKey;
    private String oktaConfig;

  //  private static String path = (NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as String).appending("/Okta.plist");
   // private static String bundle = Bundle.main.path(forResource: "Okta", ofType: "plist") ?? "";


    public String getOktaUrl() {
        return oktaUrl;
    }

    public void setOktaUrl(String oktaUrl) {
        this.oktaUrl = oktaUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getOktaConfig() {
        return oktaConfig;
    }

    public void setOktaConfig(String oktaConfig) {
        this.oktaConfig = oktaConfig;
    }
}
