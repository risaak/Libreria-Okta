package com.libokta;

import org.json.JSONObject;

public interface OktaInterface {

    interface View {
        void resultSingIn(JSONObject result);

        void resultToken(JSONObject result);

        void resultCreateUser(JSONObject result);

        void resultActivationEmail(JSONObject result);

    }

    interface Presenter {
        void getTokenWithCode(String clientId,String urlDomain);

        void SingIn(final String userName, String password, String urlDomain,String apikey);

        public void createUser(String firstName, String lastName, String title, String institution, String country, String state,
                               String city, String email, String password, String urlDomain, boolean isProfessional, boolean receiveInformation,
                               String apiKey,String clientId);

        void resendActivationEmail(String userId, String urlDomain,String apiKey);
    }
}
