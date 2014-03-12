package ffts.android.moefmdroid.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import ffts.android.moefmdroid.utils.DebugUtils;

/**
 * Created by ffts on 14-3-11.
 * Email:ffts133@gmail.com
 */
public class MoeDataResponseHandler<DataType> extends JsonHttpResponseHandler {

    private String dataKey;

    public MoeDataResponseHandler(String dataKey) {
        super(DEFAULT_CHARSET);
        this.dataKey = dataKey;
    }

    public MoeDataResponseHandler(String charset, String dataKey) {
        super(charset);
        this.dataKey = dataKey;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        DebugUtils.debug("json:" + response.toString());
        try {
            if (!response.getJSONObject("response").getJSONObject("information").getBoolean("has_error")) {
                Gson gson = new Gson();
                Type type = getType();
                DataType data = gson.fromJson(
                        response.getJSONObject("response").getString(dataKey),
                        type
                );
                onSuccess(data);
            } else {
                DebugUtils.debug("request error:" +
                        response.getJSONObject("response").getJSONObject("information").getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(e, response);
        }
    }

    public void onSuccess(DataType data) {
    }

    private Type getType() {
        Type type = String.class;
        Type mySuperClass = this.getClass().getGenericSuperclass();
        if (mySuperClass instanceof ParameterizedType)
            type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }
}
