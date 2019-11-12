package com.libokta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dacompsc.general.base.BaseSharedActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import LibreriaOkta.OktaInterface;
import LibreriaOkta.OktaManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.dacompsc.general.base.BaseSharedActivity;

public class OktaActivity extends BaseSharedActivity implements OktaInterface.View {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private OktaInterface.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new OktaManager(this);
        mPresenter.forgetPassword("azavala.yunius@gmail.com","https://dev-575230.okta.com");
    }


    @Override
    public void resultSingIn(JSONObject result) {
        JSONObject resultado = result;
    }

    @Override
    public void resultToken(JSONObject result) {
        JSONObject resultado = result;
    }

    @Override
    public void resultCreateUser(JSONObject result) {
        JSONObject resultado = result;
    }

    @Override
    public void resultActivationEmail(JSONObject result) {
        JSONObject resultado = result;
    }

    @Override
    public void resultChangePassword(JSONObject result) {
        JSONObject resultado = result;
    }

    @Override
    public void resultForgotPassowrd(JSONObject json) {
        JSONObject resultado = json;
    }


    @Override
    public Toolbar getToolbar() {
        return null;
    }
}
