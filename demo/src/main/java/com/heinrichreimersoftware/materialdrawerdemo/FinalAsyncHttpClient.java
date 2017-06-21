package com.heinrichreimersoftware.materialdrawerdemo;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FinalAsyncHttpClient {

    AsyncHttpClient client;

    public FinalAsyncHttpClient() {
        client = new AsyncHttpClient();
        client.setConnectTimeout(5);//5s超时
        if (CookieUtils.getCookies() != null) {//每次请求都要带上cookie
            BasicCookieStore bcs = new BasicCookieStore();
            bcs.addCookies(CookieUtils.getCookies().toArray(
                    new Cookie[CookieUtils.getCookies().size()]));
            client.setCookieStore(bcs);
        }
    }

    public AsyncHttpClient getAsyncHttpClient(){
        return this.client;
    }


}