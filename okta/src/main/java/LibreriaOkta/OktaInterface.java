package LibreriaOkta;

import android.webkit.WebView;

import org.json.JSONObject;

public interface OktaInterface {

    interface View {
        void resultSingIn(JSONObject result);

        void resultToken(JSONObject result);

        void resultCreateUser(JSONObject result);

        void resultActivationEmail(JSONObject result);

        void resultChangePassword(JSONObject result);

        void resultForgotPassowrd(JSONObject result);

        void resultchangePasswordNoData(JSONObject result);

    }

    interface Presenter {
        void getTokenWithCode(String clientId, String urlDomain, String code, String codeVerified);

        void SingIn(String userName, String password, String urlDomain, String apikey, String clientId);

        void createUser(String firstName, String lastName, String title, String institution, String country, String state,
                        String city, String email, String password, String urlDomain, boolean isProfessional, boolean receiveInformation,
                        String apiKey, String clientId);

        void resendActivationEmail(String userId, String urlDomain, String apiKey);

        void changePassword(String userId, String urlDomain, String apiKey, String oldPassword, String newPassword);

        void changePasswordNoData(String userId, String urlDomain, String apiKey);

        void forgetPassword(String userName, String urlDomain, String apikey);

        void getCode(String urlDomain, String clientId, WebView view, JSONObject json);

        void createUserWithoutCredentials(String firstName, String lastName, String title, String institution, String country, String state,
                                          String city, String email, String urlDomain, boolean isProfessional, boolean receiveInformation,
                                          String apiKey, String clientId);

    }
}
