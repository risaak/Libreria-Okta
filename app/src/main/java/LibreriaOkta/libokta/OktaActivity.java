package LibreriaOkta.libokta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.libokta.R;

import org.json.JSONObject;

import LibreriaOkta.OktaInterface;
import LibreriaOkta.OktaManager;
import okhttp3.MediaType;

public class OktaActivity extends AppCompatActivity implements OktaInterface.View {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private OktaInterface.Presenter mPresenter;
public WebView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mView = (WebView) findViewById(R.id.webView);
        mPresenter = new OktaManager(this);
      //  mPresenter.forgetPassword("azavala.yunius@gmail.com","https://dev-575230.okta.com");
        mPresenter.SingIn("produccioneszavala4@gmail.com","Morado456","https://dev-575230.okta.com","0oa1r27qreeuNSDYU357","0oa1r27qreeuNSDYU357");
            //mPresenter.changePassword("aide34zavala@gmail.com","https://dev-575230.okta.com","00NzM_yOsJbQDeRlLu0KeoqudoIc_rrN8h-4VGghX6","1234567","12345678");
        //mPresenter.createUser("Pedro","Marquez","imss","Imss","Mex","Zac","Zac","pedro5@gmail.com","123456","https://dev-575230.okta.com",true,
          //      true,"00NzM_yOsJbQDeRlLu0KeoqudoIc_rrN8h-4VGghX6","0oa1r27qreeuNSDYU357");
        //mPresenter.SingIn("pedro5@gmail.com","123456","https://dev-575230.okta.com","0oa1r27qreeuNSDYU357","0oa1r27qreeuNSDYU357");
//            mPresenter.forgetPassword("aide34zavala@gmail.com","https://dev-575230.okta.com","00NzM_yOsJbQDeRlLu0KeoqudoIc_rrN8h-4VGghX6");

    }


    @Override
    public void resultSingIn(JSONObject result) {
        JSONObject resultado = result;
        mPresenter.getCode("https://dev-575230.okta.com","0oa1r27qreeuNSDYU357",mView,result);
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
    public void resultchangePasswordNoData(JSONObject result) {

    }




}
