package ffts.android.moefmdroid.http;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import ffts.android.moefmdroid.utils.DebugUtils;

/**
 * Created by ffts on 14-3-11.
 * Email:ffts133@gmail.com
 */
public class MoeResultResponseHandler extends JsonHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        DebugUtils.debug("json:" + response.toString());
        try {
            if (!response.getJSONObject("response").getJSONObject("information").getBoolean("has_error")) {
                onSuccess();
            } else {
                DebugUtils.debug("request error:" +
                        response.getJSONObject("response").getJSONObject("information").getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e, response);
        }
    }

    public void onSuccess() {
    }
}
