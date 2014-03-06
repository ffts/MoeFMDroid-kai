package ffts.android.moefmdroid.oauth;

import android.util.Log;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Created by ffts on 13-8-4.
 * Email:ffts133@gmail.com
 */
public class MoeOAuth {

    private static MoeOAuth instance;
    private CommonsHttpOAuthConsumer consumer;
    private CommonsHttpOAuthProvider provider;
    private String access_token, access_token_secret, verifier;
    private String oauth_url = "none";
    public static String CONSUMERKEY = "";
    public static String CONSUMERSECRET = "";

    public static MoeOAuth getInstance(){
        if(instance == null){
            instance = new MoeOAuth();
        }
        return instance;
    }

    private MoeOAuth() {
        consumer = new CommonsHttpOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
        provider = new CommonsHttpOAuthProvider(
                "http://api.moefou.org/oauth/request_token",
                "http://api.moefou.org/oauth/access_token",
                "http://api.moefou.org/oauth/authorize");
    }

    public String getAccess_Token() {
        return this.access_token;
    }

    public String getAccess_Token_Secret() {
        return this.access_token_secret;
    }

    public String getVerifier() {
        return this.verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getOAuthUrl() {
        return this.oauth_url;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAccess_token_secret(String access_token_secret) {
        this.access_token_secret = access_token_secret;
    }

    public String getRequestToken() {

        String callBackUrl = "moefm://moefmdroid.verify.ok";
        String oauthURL = null;
        try {
            oauthURL = provider.retrieveRequestToken(consumer, callBackUrl);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
        Log.d("MOED", "request:" + consumer.getToken());
        Log.d("MOED", "request:" + consumer.getTokenSecret());

        return oauthURL;

    }


    public void getAccessToken() {

        provider.setOAuth10a(true);
        try {
            provider.retrieveAccessToken(consumer, verifier);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
        this.access_token = consumer.getToken();
        this.access_token_secret = consumer.getTokenSecret();
    }

    public String sign(String url) {
        if (consumer != null) {
            try {
                return consumer.sign(url);
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
                return url;
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
                return url;
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
                return url;
            }
        } else {
            return url;
        }
    }

    public void setSign() {
        consumer.setTokenWithSecret(access_token, access_token_secret);
    }

    public void setSign(String token, String token_secret) {
        this.access_token = token;
        this.access_token_secret = token_secret;
        consumer.setTokenWithSecret(access_token, access_token_secret);
    }
}
