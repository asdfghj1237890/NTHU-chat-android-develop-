/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.nthuchat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static java.lang.String.valueOf;


public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    public FinalAsyncHttpClient finalAsyncHttpClient;
    public AsyncHttpClient client ;

    private Button mSignInButton1;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mFirebaseUser;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private EditText mIdView;
    private EditText mPasswordView;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mFBdiv;
    public User user;
    public AppDatabase dbinstance;
    public String fire_div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(!TextUtils.isEmpty(getCookieText())){
            Toast.makeText(SignInActivity.this,"Using Your Cookie",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }*/
        //Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_sign_in);

        dbinstance = AppDatabase.getAppDatabase(getApplicationContext());
        if(dbinstance.userDao().getUser() != null) {
            dbinstance.userDao().delete(dbinstance.userDao().getUser());
        }
        // Assign fields
        mIdView = (EditText) findViewById(R.id.Input_id);
        mIdView.setHintTextColor(Color.BLACK);
        mPasswordView = (EditText) findViewById(R.id.Input_pw);
        mPasswordView.setHintTextColor(Color.BLACK);
        mSignInButton1 = (Button) findViewById(R.id.sign_in_button_1);

        // Set click listeners
        mSignInButton1.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        //Toast.makeText(SignInActivity.this, "[signin.start]currentUser: "+currentUser, Toast.LENGTH_SHORT).show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String Id = mIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(Id)) {
            mIdView.setError(getString(R.string.error_field_required));
            focusView = mIdView;
            cancel = true;
        } else if (!isIdValid(Id)) {
            mIdView.setError(getString(R.string.error_invalid_email));
            focusView = mIdView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login(Id,password);
        }
    }

    private boolean isIdValid(String Id) {
        return Id.length() > 5;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void login(String email,String passwd){
        RequestParams params=new RequestParams();
        finalAsyncHttpClient = new FinalAsyncHttpClient();
        client = finalAsyncHttpClient.getAsyncHttpClient();
        CookieUtils.saveCookie(client,this);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(SignInActivity.this);
        client.setCookieStore(myCookieStore);
        params.put("account",email);
        params.put("password",passwd);
        params.put("secCode","na");
        params.put("stay", "0");
        client.post("http://lms.nthu.edu.tw/sys/lib/ajax/login_submit.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result=new String(responseBody);
                if (result!=null){
                    String test = result.substring(18,22);
                    String fire_email = "";
                    String fire_passwd = "";
                    fire_div = "";

                    if (test.equals("true")){
                        try{
                            JSONObject jsonObj = new JSONObject(result);
                            fire_email = jsonObj.getJSONObject("ret").getString("email");
                            fire_passwd = jsonObj.getJSONObject("ret").getString("name")+"_ilmschat";
                            fire_div = jsonObj.getJSONObject("ret").getString("divName");
                            //Toast.makeText(LoginActivity.this,jsonObj.getString("ret"), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(LoginActivity.this,jsonObj.getJSONObject("ret").getString("email"), Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, jsonObj.getString("email"));
                            //Log.d(TAG, jsonObj.getString("status"));
                            createAccount(fire_email, fire_passwd);
                            signIn_ac(fire_email,fire_passwd,fire_div,jsonObj);
                        }catch (Exception e){
                            Log.d(TAG,"Json login Firebase Fail");
                        }
                        //Toast.makeText(SignInActivity.this, "Login Access, cookie=" + getCookieText(), Toast.LENGTH_SHORT).show();
                        CookieUtils.setCookies(CookieUtils.getCookie(SignInActivity.this));
                        if(mFirebaseAuth.getCurrentUser() != null) {
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            //Toast.makeText(SignInActivity.this, "[login.auth]=" +mFirebaseAuth, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(SignInActivity.this, "[login.user]=" +mFirebaseUser, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }
                else {
                    Toast.makeText(SignInActivity.this,"Access Fail",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        // [START create_user_with_email]
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //Toast.makeText(SignInActivity.this, "Create Account Success", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(SignInActivity.this, "Create Account Fail", Toast.LENGTH_SHORT).show();
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthUserCollisionException e){
                                Log.w(TAG, "createUserWithEmail:failure CE", task.getException());
                            }catch(Exception e){
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }

                    }
                });
        // [END create_user_with_email]
    }

    private String getCookieText() {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(SignInActivity.this);
        List<Cookie> cookies = myCookieStore.getCookies();
        Log.d(TAG, "cookies.size() = " + cookies.size());
        CookieUtils.setCookies(cookies);
        for (Cookie cookie : cookies) {
            Log.d(TAG, cookie.getName() + " = " + cookie.getValue());
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (!TextUtils.isEmpty(cookieName)
                    && !TextUtils.isEmpty(cookieValue)) {
                sb.append(cookieName + "=");
                sb.append(cookieValue + ";");
            }
        }
        Log.e("cookie", sb.toString());
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button_1:
                attemptLogin();
                break;
        }
    }

    private void signIn_ac(String email, String password, final String div,JSONObject jsonObj) {
        Log.d(TAG, "signIn_ac:" + email);
        Log.d(TAG, "mFirebaseAuth :" + mFirebaseAuth);
        // [START sign_in_with_email]
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task_sign) {
                        if (task_sign.isSuccessful()) {
                            user = new User();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mFBdiv = mFirebaseDB.getReference("/users/"+mFirebaseAuth.getCurrentUser().getUid());
                            //Toast.makeText(SignInActivity.this, "Login Firebase Success.", Toast.LENGTH_SHORT).show();
                            final User user = new User();
                            mFBdiv.child("div").setValue(div);
                            user.setDiv(div);
                            client.get("http://lms.nthu.edu.tw/home.php", new AsyncHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers,
                                                      byte[] responseBody, Throwable error) {
                                }
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                                    String json = new String(data);
                                    Document document_unbox = Jsoup.parse(json);
                                    analysecourse(document_unbox,user);
                                    dbinstance.userDao().insertAll(user);
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task_sign.getException());
                            Toast.makeText(SignInActivity.this, "SignIn failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void analysecourse(Document document, User user){
        Elements elements = document.select("div.mnuItem>a");
        String title_name = "";
        for (int i = 0; i < elements.size()-1 ; i++) {
            String final_name = "";
            String[] title = elements.get(i).text().split("");
            //Log.d(TAG, title);
            for (int m = 0; m < title.length; m++) {
                if (title[m].matches("[A-Za-z0-9() &]*")) {
                    title[m] = "";
                } else {
                    final_name += title[m];
                }
            }
            title_name += final_name + "#";
            //Toast.makeText(this, final_name, Toast.LENGTH_SHORT).show();
        }
        user.setClasses(title_name);
    }
}
