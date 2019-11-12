package com.i.libreria_okta;

import android.support.v7.widget.Toolbar;
import android.util.Base64;

import com.dacompsc.general.base.BaseSharedActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OktaManager extends BaseSharedActivity implements OktaInterface.Presenter {
    private OktaInterface.View mView;
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private static String callbackURI = "impella://callback";
    private static String scope = "openid+offline_access+profile+address+phone+email";
    private String code;
    private String codeVerifier;

    public OktaManager(OktaInterface.View mView) {
        this.mView = mView;
    }

    @Override
    public void getTokenWithCode(String clientId, String urlDomain) {
        codeVerifier();
        codeChange();
        OkHttpClient client = new OkHttpClient();

        String postBody = "{\n" +
                "   \"grant_type\": \"authorization_code\",\n" +
                "   \"code\": \"" + code + "\",\n" +
                "   \"scope\": \"" + scope + "\",\n" +
                "   \"client_id\": \"" + clientId + "\",\n" +
                "   \"redirect_uri\": \"" + callbackURI + "\", \n" +
                "   \"code_verifier\": \"" + codeVerifier + "\" \n" +
                "}";


        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain + "/oauth2/default/v1/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
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
                            final JSONObject json = new JSONObject(myResponse);
                            mView.resultToken(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    @Override
    public void SingIn(final String userName, String password, final String urlDomain, final String apikey) {
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("username", userName);
            postdata.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/authn")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
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
                final JSONObject json = new JSONObject();

                OktaManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONObject json = new JSONObject(myResponse);
                            if (!json.getString("errorSummary").equals("")) {
                                singInData(urlDomain, userName, apikey);
                            }

                        } catch (JSONException e) {
                            final JSONObject json;
                            try {
                                json = new JSONObject(myResponse);
                                mView.resultSingIn(json);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }

    @Override
    public void createUser(final String firstName, final String lastName, final String title, final String institution, final String country,
                           final String state, final String city, final String email, final String password, final String urlDomain,
                           final boolean isProfessional, final boolean receiveInformation, final String apiKey, final String clientId) {
        OkHttpClient client = new OkHttpClient();
        String postBody = "{\n" +
                "   \"profile\": {\n" +
                "   \"login\": \"" + email + "\",\n" +
                "   \"email\": \"" + email + "\",\n" +
                "   \"firstName\": \"" + firstName + "\",\n" +
                "   \"lastName\": \"" + lastName + "\",\n" +
                "   \"title\": \"" + title + "\"" + ",\n" +
                "   \"city\": \"" + city + "\"" + ",\n" +
                "   \"state\": \"" + state + "\"" + "\n" +
                "}, \n" +
                "   \"credentials\": {\n" +
                "   \"password\": {\"value\":\"" + password + "\"}\n" +
                "} \n" +
                "}";

        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users?activate=false")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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
                            if (!json.getString("id").equals("")) {
                                dataUser(firstName, lastName, title, institution, country, state, city, email, password, urlDomain, isProfessional
                                        , receiveInformation, apiKey, json, clientId);
                            }

                        } catch (JSONException e) {
                            JSONObject json;
                            try {
                                mView.resultCreateUser(json = new JSONObject(myResponse));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void resendActivationEmail(String userId, String urlDomain, String apiKey) {
        OkHttpClient client = new OkHttpClient();
        String postBody = "";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId + "/lifecycle/activate?sendEmail=false")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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
                            mView.resultActivationEmail(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void changePassword(String userId, String urlDomain, String apiKey,String oldPassword,String newPassword) {
        OkHttpClient client = new OkHttpClient();

        String postBody = "{" +
                "   \"oldPassword\": {" +
                "   \"value\": \"" + oldPassword +
                "}," +
                "   \"newPassword\": {" +
                "   \"value\":\"" + newPassword + "}" +
                "}";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain +"/api/v1/users/"+ userId+"/credentials/change_password")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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
                            final JSONObject json = new JSONObject(myResponse);
                            mView.resultChangePassword(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    @Override
    public void forgetPassword(String userName, String urlDomain) {
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        String link="/myapp/some/deep/link/i/want/to/return/to";
        try {
            postdata.put("username", userName);
            postdata.put("factorType", "EMAIL");
            postdata.put("relayState", link);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/authn/recovery/password")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
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
                            final JSONObject json = new JSONObject(myResponse);
                            mView.resultForgotPassowrd(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }


    private void codeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] cod = new byte[32];
        sr.nextBytes(cod);
        String code1 = Base64.encodeToString(cod, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        code = code1.replace('+', '-').replace('/', '_').replace("=", "");

    }

    private void codeChange() {
        byte[] bytes = new byte[0];
        try {
            bytes = code.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        String challenge = new String(Base64.encode(digest, Base64.NO_WRAP));
        codeVerifier = challenge.replace('+', '-').replace('/', '_').replace("=", "");

    }

    private void dataUser(String firstName, String lastName, String title, String institution, String country, String state, String city, String email, String password, String urlDomain, boolean isProfessional, boolean receiveInformation,
                          String apiKey, JSONObject jsonData, String clientId) {
        String userId = "";

        OkHttpClient client = new OkHttpClient();
        try {
            userId = jsonData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postBody = "{\n" +
                "   \"id\": \"" + userId + "\",\n" +
                "   \"scope\": \"USER\",\n" +
                "   \"profile\": {\n" +
                "   \"instituteName\": \"" + institution + "\",\n" +
                "   \"professional\": \"" + isProfessional + "\",\n" +
                "   \"receiveInformation\": \"" + receiveInformation + "\"" + "\n" +
                "}, \n" +
                "   \"credentials\": {\n" +
                "   \"userName\": \"" + email + "\",\n" +
                "   \"password\": {\"value\":\"" + password + "\"}\n" +
                "} \n" +
                "}";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/apps/" + clientId + "/users")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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
                            mView.resultCreateUser(json);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }

    public void singInData(String urlDomain, String userName, String apikey) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userName)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apikey)
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
                            final JSONObject json = new JSONObject(myResponse);
                            mView.resultSingIn(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }
}
