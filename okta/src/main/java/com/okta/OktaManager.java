package com.okta;

import android.view.textclassifier.TextSelection;

import java.net.HttpURLConnection;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class OktaManager {

    private static String callbackURI = "impella://callback";

    public void signUpUser(String email, String password,String firstName,String lastName,String title, String instituteName,String city,String state, String country,Boolean isProfessional,Boolean receiveInformation){



    }
    public void signIn(String username,String password){
        HttpUrl httpUrl= new HttpUrl.Builder()
                .addQueryParameter("username",username)
                .addQueryParameter("password",password)
                .scheme("https")
                .host("dev-242240.okta.com")
                .addPathSegment("w")
                .build();
       // Request request= Request.Builder().get().url(httpUrl).build();
    }
}
