package ffts.android.moefmdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import ffts.android.moefmdroid.app.Constants;
import ffts.android.moefmdroid.http.MoeClient;
import ffts.android.moefmdroid.http.MoeDataResponseHandler;
import ffts.android.moefmdroid.modules.Song;
import ffts.android.moefmdroid.oauth.MoeOAuth;
import ffts.android.moefmdroid.player.MoePlayerActivity;
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
            startActivity(new Intent(this, MoePlayerActivity.class));
//            RequestParams params = new RequestParams();
//            params.put("fav", "music");
//            MoeClient.getInstance().get(
//                    MoeClient.HOST_MOEFM + MoeClient.API_FM_PLAYLIST, params,
//                    new MoeDataResponseHandler<List<Song>>("playlist") {
//
//                        @Override
//                        public void onSuccess(List<Song> data) {
//                            super.onSuccess(data);
//                            for (Song song : data) {
//                                DebugUtils.debug("request success:" + song.getSub_title());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Throwable e, JSONObject errorResponse) {
//                            super.onFailure(e, errorResponse);
//                            DebugUtils.debug("request faild");
//                            e.printStackTrace();
//                        }
//                    }
//            );
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
