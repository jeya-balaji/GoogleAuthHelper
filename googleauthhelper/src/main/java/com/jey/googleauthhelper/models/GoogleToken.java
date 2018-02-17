package com.jey.googleauthhelper.models;

/**
 * Created by jeyabalaji on 2/17/2018.
 */

public class GoogleToken {

    private String AccessToken;
    private String RefreshToken;
    private Long ExpiresIn;
    private String AuthToken;


    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return ExpiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        ExpiresIn = expiresIn;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }
}
