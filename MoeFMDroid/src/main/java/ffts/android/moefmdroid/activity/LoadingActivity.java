package ffts.android.moefmdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import ffts.android.moefmdroid.app.Constants;
import ffts.android.moefmdroid.http.MoeClient;
import ffts.android.moefmdroid.modules.User;
import ffts.android.moefmdroid.oauth.MoeOAuth;
import ffts.android.moefmdroid.utils.DebugUtils;
import ffts.android.moefmdroid.utils.StringUtils;

/**
 * Created by ffts on 13-8-4.
 * Email:ffts133@gmail.com
 */
public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOAuth();
    }

    private void checkOAuth() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String token = sp.getString("token", "");
        String token_secret = sp.getString("token_secret", "");
        if (StringUtils.isNotNull(token, token_secret)) {
            MoeOAuth.getInstance().setSign(token, token_secret);
            MoeClient.getInstance().get("http://api.moefou.org/user/detail.json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            DebugUtils.debug("oauth_request:\n" + MoeOAuth.getInstance()
                                    .sign("http://api.moefou.org/user/detail.json"));
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            super.onSuccess(response);
                            DebugUtils.debug("oauth_request: success\n" + response.toString());
                            try {
                                Gson gson = new Gson();
                                if (!response.getJSONObject("response")
                                        .getJSONObject("information")
                                        .getBoolean("has_error")) {
                                    User user = gson.fromJson(
                                            response.getJSONObject("response").getJSONObject("user").toString(),
                                            User.class);
                                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                    sp.edit().putLong("uid", user.getUid()).commit();
                                    sp.edit().putString("user_name", user.getUser_name()).commit();
                                    sp.edit().putString("user_nickname", user.getUser_nickname()).commit();
//                                    startActivity(new Intent(LoadingActivity.this, MoePlayerActivity.class));
//                                    LoadingActivity.this.finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable e, JSONObject errorResponse) {
                            super.onFailure(e, errorResponse);
                            DebugUtils.debug("oauth_request: faild");
                            e.printStackTrace();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
        } else {
            startActivityForResult(new Intent(this, OAuthActivity.class), Constants.REQUEST_CODE_FOROA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_FOROA) {
                checkOAuth();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
