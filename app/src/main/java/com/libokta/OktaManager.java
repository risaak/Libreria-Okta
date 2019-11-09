package com.libokta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OktaManager extends AppCompatActivity {

    public static final MediaType  MEDIA_TYPE = MediaType.parse("application/json");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  SingIn("omu.user1@mailinator.com","Pa$$word1","https://dev-242240.okta.com");

    }
        //user con contraseña y password
    public void SingIn(final String username, String password, final String urlDomain){
        final JSONObject[] jsonResul = new JSONObject[1];
        final OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        JSONObject json;

        try {
            postdata.put("username",username);
            postdata.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       RequestBody body= RequestBody.create(MEDIA_TYPE,postdata.toString());
        Request request= new Request.Builder()
                .url(urlDomain+"/api/v1/authn")
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();

                OktaManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);

                           if(json.get("status").equals("SUCCESS")) {
                               //Usuario correcto

                           }else if(json.get("errorSummary").equals("Authentication failed"));
                            {
                                //Autenticacion fallida
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    public void createUser(String firstName, String lastName, String title, String institution,String country, String state,
            String city, String email,String password,String urlDomain, boolean isProfessional, boolean receiveInformation,
                            String token){
        OkHttpClient client = new OkHttpClient();

        String postBody="{\n"+
                "   \"profile\": {\n"+
                "   \"login\": \""+email+"\",\n"+
                "   \"email\": \""+email+"\",\n"+
                "   \"firstName\": \""+firstName+"\",\n"+
                "   \"lastName\": \""+lastName+"\",\n"+
                "   \"title\": \""+title+"\""+ "\n"+
                "   \"city\": \""+city+"\""+ "\n"+
                "   \"state\": \""+state+"\""+ "\n"+
                "}, \n"+
                "   \"credentials\": {\n"+
                "   \"password\": {\"value\":\""+password+"\"}\n"+
                "} \n"+
                "}";
        /*
        String postBody="{\n"+
                "   \"profile\": {\n"+
                "   \"firstName\": \""+firstName+"\",\n"+
                "   \"lastName\": \""+lastName+"\",\n"+
                "   \"email\": \""+email+"\",\n"+
                "   \"login\": \""+email+"\",\n"+
                "   \"mobilePhone\": \""+mobilePhone+"\""+ "\n"+
                "}, \n"+
                "   \"credentials\": {\n"+
                "   \"password\": {\"value\":\""+password+"\"}\n"+
                "} \n"+
                "}";*/


        JSONObject obj;
        RequestBody body= RequestBody.create(MEDIA_TYPE,postBody);

        Request request= new Request.Builder()
                .url(urlDomain+"/api/v1/users?activate=false")
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization","SSWS "+token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();

                OktaManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            if(json.get("status").equals("STAGED")) {
                                //Usuario creado falta activacion el json contiene el link de activacion

                            }else if(json.get("errorSummary").equals("Authentication failed"));{
                                //Autenticacion fallida
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }
    public void resendActivationEmail(String userId,String urlDomain){
        OkHttpClient client = new OkHttpClient();
        Request request= new Request.Builder()
                .url(urlDomain+"api/v1/users/"+userId+"/lifecycle/activate?sendEmail=false")
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();

                OktaManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(myResponse);
                           String activationUrl= json.getString("ativationUrl");
                           if(!activationUrl.equals("")){
                               //se tiene la url de activacion
                           }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


}
