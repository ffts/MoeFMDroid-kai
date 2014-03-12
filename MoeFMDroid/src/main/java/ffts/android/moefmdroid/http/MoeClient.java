package ffts.android.moefmdroid.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import ffts.android.moefmdroid.oauth.MoeOAuth;

/**
 * Created by ffts on 13-8-4.
 * Email:ffts133@gmail.com
 */
public class MoeClient {

    //API地址
    public static final String HOST_MOEFOU = "http://api.moefou.org/";
    public static final String HOST_MOEFM = "http://moe.fm/";
    public static final String API_OAUTH_REQUEST_TOKEN = "oauth/request_token";
    public static final String API_OAUTH_AUTHORIZE = "oauth/authorize";
    public static final String API_OAUTH_ACCESS_TOKEN = "oauth/access_token";
    public static final String API_USER_DETAIL = "user/detail.json";
    public static final String API_FM_PLAYLIST = "listen/playlist?api=json";

    private static MoeClient instance;
    private AsyncHttpClient client = new AsyncHttpClient();

    private MoeClient() {
        client = new AsyncHttpClient();
    }

    public static MoeClient getInstance() {
        if (instance == null) {
            instance = new MoeClient();
        }
        return instance;
    }

    private String sign(String s) {
        return MoeOAuth.getInstance().sign(s);
    }

    public void get(String url, AsyncHttpResponseHandler handler) {
        client.get(sign(url), handler);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(sign(url), params, handler);
    }

    public void get(Context context, String url, AsyncHttpResponseHandler handler) {
        client.get(context, sign(url), handler);
    }

    public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(context, sign(url), params, handler);
    }

    public void get(String url, MoeDataResponseHandler handler) {
        client.get(sign(url), handler);
    }

    public void post(String url, AsyncHttpResponseHandler handler) {
        client.post(sign(url), handler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(sign(url), params, handler);
    }

    public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(context, sign(url), params, handler);
    }

}
