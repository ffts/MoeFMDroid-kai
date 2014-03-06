package ffts.android.moefmdroid.oauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ffts.android.moefmdroid.utils.DebugUtils;

/**
 * Created by ffts on 13-8-4.
 */
public class OAuthWebActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        WebView web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                DebugUtils.debug("oauth_web:\n"+url);
                if (url.contains("moefm://moefmdroid.verify.ok")) {
                    String[] ss = url.split("&");
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].contains("oauth_verifier=")) {
                            String verifier = ss[i].substring(ss[i].indexOf("=") + 1, ss[i].length());
                            if (verifier != null && !verifier.equals("")) {
                                Intent data = new Intent();
                                data.putExtra("verifier", verifier);
                                DebugUtils.debug("oauth_web:\nverifier: "+verifier);
                                setResult(RESULT_OK, data);
                                finish();
                                return true;
                            }
                        }
                    }
                }
                setResult(RESULT_CANCELED);
                finish();
                return false;
            }
        });
        setContentView(web);
        if (url != null && !url.equals("")) {
            web.loadUrl(url);
        }else {
            finish();
        }
    }
}
