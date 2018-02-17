package com.jey.googleauthhelper.models;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import java.util.List;

/**
 * Created by jeyabalaji on 2/17/2018.
 */

public class SignInParams {

    private GoogleSignInOptions SignInOptions;
    private List<Scope> Scopes;
    private String ClientId;
    private String ClientSecret;

    public GoogleSignInOptions getSignInOptions() {
        return SignInOptions;
    }

    public void setSignInOptions(GoogleSignInOptions signInOptions) {
        SignInOptions = signInOptions;
    }

    public List<Scope> getScopes() {
        return Scopes;
    }

    public void setScopes(List<Scope> scopes) {
        Scopes = scopes;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getClientSecret() {
        return ClientSecret;
    }

    public void setClientSecret(String clientSecret) {
        ClientSecret = clientSecret;
    }
}
