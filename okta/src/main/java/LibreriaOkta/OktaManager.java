package LibreriaOkta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dacompsc.general.base.BaseSharedActivity;
import com.okta.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OktaManager extends AppCompatActivity implements OktaInterface.Presenter {
    private OktaInterface.View mView;
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private static String callbackURI = "impella://callback";
    private static String scope = "openid+offline_access+profile+address+phone+email";
    private String code;
    private String codeVerifier;


    public OktaManager(OktaInterface.View mView) {
        this.mView = mView;
    }

    public OktaManager() {
    }

    @Override
    public void getTokenWithCode(String clientId, String urlDomain, String code, String codeVerified) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = new FormBody.Builder().add("scope", scope).add("client_id", clientId).add("code_verifier", codeVerified)
                .add("redirect_uri", callbackURI).add("code", code).add("grant_type", "authorization_code").build();

        Request request = new Request.Builder()
                .url(urlDomain + "/oauth2/default/v1/token").post(body).build();
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
    public void SignIn(final String userName, String password, final String urlDomain, final String apikey, final String clientId) {
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
                                signInData(urlDomain, userName, apikey);
                            }

                        } catch (JSONException e) {
                            final JSONObject json;
                            try {
                                json = new JSONObject(myResponse);
                                mView.resultSignIn(json);
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
    public void ActivationEmail(String userId, String urlDomain, String apiKey) {
        OkHttpClient client = new OkHttpClient();
        String postBody = "";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId + "/lifecycle/activate")
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
                            JSONObject json = new JSONObject();
                            mView.resultActivationEmail(json);
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
                .url(urlDomain + "/api/v1/users/" + userId + "/lifecycle/reactivate?sendEmail=true")
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
                            JSONObject json = new JSONObject();
                            mView.resultActivationEmail(json);
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    @Override
    public void changePassword(String userId, String urlDomain, String apiKey, String oldPassword, String newPassword) {
        OkHttpClient client = new OkHttpClient();

        String postBody = "{  \"oldPassword\":{\"value\":\"" + oldPassword + "\"},\n" +
                "   \"newPassword\":{\"value\":\"" + newPassword + "\"} }";


        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId + "/credentials/change_password")
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
                            JSONObject json = new JSONObject();
                            mView.resultChangePassword(json);
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    @Override
    public void changePasswordNoData(String userId, String urlDomain, String apiKey) {
        OkHttpClient client = new OkHttpClient();

        String postBody = "";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId + "/credentials/change_password")
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
                            mView.resultchangePasswordNoData(json);

                        } catch (JSONException e) {
                            JSONObject json = new JSONObject();
                            mView.resultchangePasswordNoData(json);
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void forgotPassword(String userName, String urlDomain, String apikey) {
        OkHttpClient client = new OkHttpClient();

        String postBody = "";
        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);

        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userName + "/credentials/forgot_password")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apikey)
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
                            mView.resultForgotPassword(json);

                        } catch (JSONException e) {
                            JSONObject json = new JSONObject();
                            mView.resultForgotPassword(json);
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

    public void signInData(String urlDomain, String userName, String apikey) {
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
                            mView.resultSignIn(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    public void getCode(final String urlDomain, final String clientId, WebView view, JSONObject json) {
        String sesionToken = "";
        try {
            sesionToken = json.getString("sessionToken");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        codeVerifier();
        codeChange();
        String code1 = code;
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 64;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String state = buffer.toString();

        String dataUrl = urlDomain + "/oauth2/default/v1/authorize?client_id=" + clientId + "&response_type=code&response_mode=fragment&scope=" + scope + "&redirect_uri=" + callbackURI + "&state=" + state + "&code_challenge_method=S256&code_challenge=" + codeVerifier + "&sessionToken=" + sesionToken;

        view.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                int index = url.indexOf("code");
                if (index != -1) {
                    int indexEnd = url.indexOf("&state");
                    String resultCode = url.substring(index + 5, indexEnd);
                    getTokenWithCode(clientId, urlDomain, resultCode, code);
                    // mView.resultPeticion(resultCode,code);
                } else {
                    final JSONObject json = new JSONObject();
                    mView.resultToken(json);
                }

            }
        });
        view.loadUrl(dataUrl);
    }

    @Override
    public void createUserWithoutCredentials(final String firstName, final String lastName, final String title, final String institution, final String country, final String state, final String city, final String email, final String urlDomain, final boolean isProfessional, final boolean receiveInformation, final String apiKey, final String clientId) {
        OkHttpClient client = new OkHttpClient();
        String postBody = "{\n" +
                "   \"profile\": {\n" +
                "   \"login\": \"" + email + "\",\n" +
                "   \"email\": \"" + email + "\",\n" +
                "   \"firstName\": \"" + firstName + "\",\n" +
                "   \"lastName\": \"" + lastName + "\",\n" +
                "   \"title\": \"" + title + "\"" + ",\n" +
                "   \"city\": \"" + city + "\"" + ",\n" +
                "   \"country\": \"" + country + "\"" + ",\n" +
                "   \"instituteName\": \"" + institution + "\",\n" +
                "   \"professional\": \"" + isProfessional + "\",\n" +
                "   \"receiveInformation\": \"" + receiveInformation  + "\",\n" +
                "   \"state\": \"" + state + "\"" + "\n" +

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
                            mView.resultCreateUser(json);

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
    public void addUserToGroup(String groupId, String userId, String urlDomain, String apiKey) {

        OkHttpClient client = new OkHttpClient();
        String postBody ="";
        RequestBody body = RequestBody.create(MEDIA_TYPE, "{}");
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/groups/" + groupId + "/users/"+userId)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
                .put(body)
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

    @Override
    public void existingUser(String userId, String urlDomain, String apiKey) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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
                            mView.resultExistingUser(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    @Override
    public void userGroups(String urlDomain,String userId,String apiKey) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/" + userId+"/groups")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
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

                            mView.userGroups(myResponse);



                    }
                });
            }
        });
    }

    @Override
    public void addUserExistingToGroup(String institution, String country, boolean isProfessional, boolean receiveInformation, String apiKey, String urlDomain, String groupId, String userId) {

        OkHttpClient client = new OkHttpClient();
        String postBody = "{\n" +
                "   \"profile\": {\n" +
                "   \"country\": \"" + country + "\"" + ",\n" +
                "   \"instituteName\": \"" + institution + "\",\n" +
                "   \"professional\": \"" + isProfessional + "\",\n" +
                "   \"receiveInformation\": \"" + receiveInformation  + "\"" + "\n" +
                "} \n" +
                "}";

        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/groups/" + groupId + "/users/"+userId)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "SSWS " + apiKey)
                .put(body)
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


    public void addDataUser(String country,String institution, String urlDomain, boolean isProfessional, boolean receiveInformation,
                                            String apiKey,String userId) {


        OkHttpClient client = new OkHttpClient();


        String postBody = "{\n" +
                "   \"profile\": {\n" +
                "   \"country\": \"" + country + "\"" + ",\n" +
                "   \"instituteName\": \"" + institution + "\",\n" +
                "   \"professional\": \"" + isProfessional + "\",\n" +
                "   \"receiveInformation\": \"" + receiveInformation + "\"" + "\n" +
                "} \n" +
                "}";

        RequestBody body = RequestBody.create(MEDIA_TYPE, postBody);
        Request request = new Request.Builder()
                .url(urlDomain + "/api/v1/users/"+userId)
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
                            mView.resultAddDataUser(json);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }


}
