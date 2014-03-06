package ffts.android.moefmdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.app.Constants;
import ffts.android.moefmdroid.oauth.MoeOAuth;
import ffts.android.moefmdroid.oauth.OAuthWebActivity;
import ffts.android.moefmdroid.utils.DebugUtils;

public class OAuthActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth_activity);
        findViewById(R.id.bt_qb_ok).setOnClickListener(this);
        findViewById(R.id.bt_qb_shine).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.oauth, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_qb_ok:
                new OAuthRequestTask().execute();
                break;
            case R.id.bt_qb_shine:
                finish();
                break;
            default:
                break;
        }
    }

    class OAuthRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = MoeOAuth.getInstance().getRequestToken();
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                DebugUtils.debug(s);
                Intent intent = new Intent();
                intent.setClass(OAuthActivity.this, OAuthWebActivity.class);
                intent.putExtra("url", s);
                OAuthActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_FOROA);
            }
        }
    }

    class OAuthAccessTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            MoeOAuth.getInstance().getAccessToken();
            DebugUtils.debug("oauth:\ntoken: " + MoeOAuth.getInstance().getAccess_Token());
            DebugUtils.debug("oauth:\ntoken_secret: "+MoeOAuth.getInstance().getAccess_Token_Secret());
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit().putString("token", MoeOAuth.getInstance().getAccess_Token()).commit();
            sp.edit().putString("token_secret", MoeOAuth.getInstance().getAccess_Token_Secret()).commit();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(OAuthActivity.this, "签订契约成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_FOROA:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String verifier = data.getStringExtra("verifier");
                        if (verifier != null && !verifier.equals("")) {
                            MoeOAuth.getInstance().setVerifier(verifier);
                            new OAuthAccessTask().execute();
                        }
                    }
                } else {

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
