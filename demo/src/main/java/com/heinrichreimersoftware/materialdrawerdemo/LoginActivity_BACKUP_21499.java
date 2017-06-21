package com.heinrichreimersoftware.materialdrawerdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.util.concurrent.RunnableFuture;

import javax.net.ssl.HttpsURLConnection;
import static android.Manifest.permission.READ_CONTACTS;
import android.widget.Toast;
import android.graphics.Typeface;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.cookie.Cookie;
<<<<<<< HEAD
import com.unstoppable.submitbuttonview.SubmitButton;
import android.os.Handler;
=======
import android.os.Handler;
import com.unstoppable.submitbuttonview.SubmitButton;

>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
<<<<<<< HEAD
//    private VideoView mVideoView;
=======
    //private VideoView mVideoView;
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
    private TextView mTextView;
    private final String TAG = "LoginActivity";
    private SubmitButton mSubmitView;
    private SubmitButton.OnResultEndListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(LoginActivity.this, "Login Access, cookie=" + getCookieText(), Toast.LENGTH_SHORT).show();
        if(!TextUtils.isEmpty(getCookieText())){
            Toast.makeText(LoginActivity.this,"Using Your Cookie",Toast.LENGTH_LONG).show();
            finish();
            Intent myIntent = new Intent(LoginActivity.this,NavigationActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        setContentView(R.layout.activity_login);
        mTextView = (TextView)findViewById(R.id.textView);
        Typeface otfFace_chosence = Typeface.createFromAsset(getAssets(), "fonts/Chosence.otf");
        mTextView.setTypeface(otfFace_chosence);
<<<<<<< HEAD
//        mVideoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg);
//        mVideoView.setVideoURI(uri);
//        mVideoView.start();
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer){
//                mediaPlayer.setLooping(true);
//            }
//        });
=======
        //mVideoView = (VideoView) findViewById(R.id.videoView);
        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg);
        //mVideoView.setVideoURI(uri);
        //mVideoView.start();
        /*mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer){
                mediaPlayer.setLooping(true);
            }
        });*/
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setHintTextColor(Color.WHITE);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setHintTextColor(Color.WHITE);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

<<<<<<< HEAD
//        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//        mEmailSignInButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                attemptLogin();
//            }
//        });

        mLoginFormView = findViewById(R.id.login_form);

        /**
         * 传入submit结果以呈现不同结果反馈效果
         *
         * @param boolean isSucceed
         */

        //Submit Button https://github.com/Someonewow/SubmitButton
        mSubmitView = (SubmitButton)findViewById(R.id.submitbutton);
        mSubmitView.reset();
        mSubmitView.setOnClickListener(new OnClickListener() {
=======
        /*Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });*/

<<<<<<< HEAD
        mSubmitView.setVisibility(View.VISIBLE);
        mSubmitView.setOnResultEndListener(listener);

        /**
         * 重置SubmitButton
         */
        //mSubmitView.reset();

        /**
         * 设置进度(该方法仅在progressStyle设置为progress时有效)
         *
         * @param progress 进度值 (0-100)
         */
        //mSubmitView.setProgress(50);

        /**
         * 设置动画结束回调接口
         *
         * @param listener
         */
//        mSubmitView.setOnResultEndListener(OnResultEndListener listener);
=======
        mLoginFormView = findViewById(R.id.login_form);

        mSubmitView = (SubmitButton) findViewById(R.id.submitbutton);
        mSubmitView.reset();
        mSubmitView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                attemptLogin();
            }

        });
        mSubmitView.setVisibility(View.VISIBLE);
        mSubmitView.setOnResultEndListener(listener);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mSubmitView.setProgress(50);
<<<<<<< HEAD
=======

>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            mSubmitView.doResult(false);
<<<<<<< HEAD
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    mSubmitView.reset();
                }
            }, 2000);
=======
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        }
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
            mSubmitView.doResult(false);
<<<<<<< HEAD
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    mSubmitView.reset();
                }
            }, 2000);

=======
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            mSubmitView.doResult(false);
<<<<<<< HEAD
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    mSubmitView.reset();
                }
            }, 2000);

=======
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
            mSubmitView.doResult(false);
<<<<<<< HEAD
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    mSubmitView.reset();
                }
            }, 2000);
=======
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute();
            login(email,password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("1");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void login(String email,String passwd){
        RequestParams params=new RequestParams();
        FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
        AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
        CookieUtils.saveCookie(client,this);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(LoginActivity.this);
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
                    //Toast.makeText(LoginActivity.this,result,Toast.LENGTH_LONG).show();
                    String test = result.substring(18,22);
                    if (test.equals("true")){
                        //Toast.makeText(LoginActivity.this, "Login Access, cookie=" + getCookieText(), Toast.LENGTH_SHORT).show();
                        CookieUtils.setCookies(CookieUtils.getCookie(LoginActivity.this));
                        finish();
                        Intent myIntent = new Intent(LoginActivity.this,NavigationActivity.class);
                        LoginActivity.this.startActivity(myIntent);


//                        int progress = 0;
//                        Timer timer = new Timer();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                mSubmitView.doResult(true);
//                            }
//                        }, 0, 1000);

//                        new Handler().postDelayed(new Runnable(){
//                            public void run(){
//                                mSubmitView.doResult(true);
//                            }
//                        }, 3000);

                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        mSubmitView.doResult(false);
<<<<<<< HEAD
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                mSubmitView.reset();
                            }
                        }, 3000);
=======
                        new Handler().postDelayed(new Runnable() {
                            public void run(){
                                mSubmitView.reset();
                            }
                        },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,"Access Fail",Toast.LENGTH_LONG).show();
                    mSubmitView.doResult(false);
<<<<<<< HEAD
=======
                    new Handler().postDelayed(new Runnable() {
                        public void run(){
                            mSubmitView.reset();
                        }
                    },2000);
>>>>>>> 24c69be5066b8f737894b37182d0ee124893293b
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
    private String getCookieText() {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(LoginActivity.this);
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


}

