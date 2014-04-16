package ffts.android.moefmdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.List;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.app.Constants;
import ffts.android.moefmdroid.http.MoeClient;
import ffts.android.moefmdroid.http.MoeDataResponseHandler;
import ffts.android.moefmdroid.http.MoeResultResponseHandler;
import ffts.android.moefmdroid.modules.Song;
import ffts.android.moefmdroid.oauth.MoeOAuth;
import ffts.android.moefmdroid.player.MoePlayerActivity;
import ffts.android.moefmdroid.utils.DebugUtils;
import ffts.android.moefmdroid.utils.StringUtils;
import ffts.android.moefmdroid.utils.ToastUtils;

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
        //todo 完善授权验证
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String token = sp.getString("token", "");
        String token_secret = sp.getString("token_secret", "");
        if (StringUtils.isNotNull(token, token_secret)) {
            MoeOAuth.getInstance().setSign(token, token_secret);
            MoeClient.getInstance().getSelfDetail(
                    this,
                    new MoeResultResponseHandler() {

                        @Override
                        public void onSuccess() {
                            super.onSuccess();
                            startActivity(new Intent(LoadingActivity.this, MoePlayerActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                            super.onFailure(statusCode, headers, responseBody, e);
                            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                startActivityForResult(new Intent(LoadingActivity.this, OAuthActivity.class), Constants.REQUEST_CODE_FOROA);
                            } else {
                                ToastUtils.toast(getResources().getString(R.string.msg_connect_error) + ":" + statusCode);
                                e.printStackTrace();
                            }
                        }
                    }
            );
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
