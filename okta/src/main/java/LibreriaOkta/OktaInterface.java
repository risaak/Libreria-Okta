package LibreriaOkta;

import android.webkit.WebView;

import org.json.JSONObject;

public interface OktaInterface {

    interface View {
        void resultSignIn(JSONObject result);

        void resultToken(JSONObject result);

        void resultCreateUser(JSONObject result);

        void resultActivationEmail(JSONObject result);

        void resultChangePassword(JSONObject result);

        void resultForgotPassword(JSONObject result);

        void resultchangePasswordNoData(JSONObject result);

        void resultaddUserGroup(JSONObject result);

        void resultExistingUser(JSONObject result);

        void resultUserInGroup(boolean result);

        void userGroups(String result);

        void resultAddDataUser(JSONObject result);

        void resultResetPassword(JSONObject result);

    }

    interface Presenter {
        void getTokenWithCode(String clientId, String urlDomain, String code, String codeVerified);

        void SignIn(String userName, String password, String urlDomain, String apikey, String clientId);

        void createUser(String firstName, String lastName, String title, String institution, String country, String state,
                        String city, String email, String password, String urlDomain, boolean isProfessional, boolean receiveInformation,
                        String apiKey, String clientId);

        void ActivationEmail(String userId, String urlDomain, String apiKey);

        void resendActivationEmail(String userId, String urlDomain, String apiKey);

        void changePassword(String userId, String urlDomain, String apiKey, String oldPassword, String newPassword);

        void changePasswordNoData(String userId, String urlDomain, String apiKey);

        void forgotPassword(String userName, String urlDomain, String apikey);

        void getCode(String urlDomain, String clientId, WebView view, JSONObject json);

        void createUserWithoutCredentials(String firstName, String lastName, String title, String institution, String country, String state,
                                          String city, String email, String urlDomain, boolean isProfessional, boolean receiveInformation,
                                          String apiKey, String clientId);

        void addUserToGroup(String groupId, String userId, String urlDomain, String apiKey);

        void existingUser(String userId, String urlDomain, String apiKey);

        void userGroups(String urlDomain, String userId, String apiKey);

        void addUserExistingToGroup(String institution, String country, boolean isProfessional, boolean receiveInformation,
                                    String apiKey, String urlDomain, String groupId, String userId);

        void addDataUser(String country, String institution, String urlDomain, boolean isProfessional, boolean receiveInformation,
                         String apiKey, String userId);

        void resetPassword(String urlDomain, String userId, String apikey);

    }
}
