package com.jey.googleauthhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jey.googleauthhelper.exceptions.NoScopeFoundException;
import com.jey.googleauthhelper.models.GoogleToken;
import com.jey.googleauthhelper.models.SignInParams;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class GoogleAccountManager {

    private static String TAG = "com.jey.googleauthhelper.GoogleAccountManager";

    private SignInParams mSignInParams;

    public GoogleAccountManager(SignInParams signInParams){
        mSignInParams = signInParams;
    }

    @NonNull
    public Intent getChooseAccountIntent(Activity activity) {
        if (mSignInParams.getScopes().size()  > 1) {
            Scope firstScope = mSignInParams.getScopes().remove(0);
            List<Scope> scopes = mSignInParams.getScopes();

            Log.d(TAG, "Scopes "+scopes.size());

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(mSignInParams.getSignInOptions())
                    .requestScopes(firstScope, scopes.toArray(new Scope[scopes.size()]))
                    .requestServerAuthCode(mSignInParams.getClientId())
                    .requestProfile()
                    .requestEmail()
                    .build();

            GoogleSignInClient client = GoogleSignIn.getClient(activity, gso);

            Log.d(TAG, "Service client code "+gso.getServerClientId());
            return client.getSignInIntent();
        } else {
            throw new NoScopeFoundException("No scopes found");
        }
    }

    public GoogleToken getGoogleToken(Intent data) throws ApiException, InterruptedException, ExecutionException {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( data);
       return handleSignInResult(task);
    }

    private GoogleToken handleSignInResult(Task<GoogleSignInAccount> completedTask) throws ApiException, InterruptedException, ExecutionException {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Account Name "+account.getId());

            return new AccessTokenCreator().execute(account, mSignInParams.getClientId(), mSignInParams.getClientSecret()).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code = " + e.getMessage());
            throw e;
        }
    }

    private static class AccessTokenCreator extends AsyncTask<Object, Object, GoogleToken> {
        @Override
        protected GoogleToken doInBackground(Object... objects) {
            GoogleSignInAccount googleAccount = (GoogleSignInAccount) objects[0];
            String clientId = (String) objects[1];
            String clientSecret = (String) objects[2];
            Log.d(TAG,"Requesting token for account: " + googleAccount.getEmail());

            return getGoogleToken(googleAccount, clientId, clientSecret);
        }

        private GoogleToken getGoogleToken(GoogleSignInAccount googleAccount, String clientId, String clientSecret) {
            try {
                GoogleToken token = new GoogleToken();
                GoogleTokenResponse tokenResponse =
                        new GoogleAuthorizationCodeTokenRequest(
                                new NetHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                "https://www.googleapis.com/oauth2/v4/token",
                                clientId,
                                clientSecret,
                                googleAccount.getServerAuthCode(),
                                "")
                                .execute();

                String accessToken = tokenResponse.getAccessToken();
                String refreshToken = tokenResponse.getRefreshToken();
                Long expiresInSeconds = tokenResponse.getExpiresInSeconds();

                token.setAccessToken(accessToken);
                token.setRefreshToken(refreshToken);
                token.setExpiresIn(expiresInSeconds + System.currentTimeMillis());
                token.setAuthToken(googleAccount.getServerAuthCode());
                return token;
            } catch (IOException e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return null;
            }
        }
    }

}
