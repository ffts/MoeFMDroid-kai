package ffts.android.moefmdroid.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import ffts.android.moefmdroid.oauth.MoeOAuth;
import ffts.android.moefmdroid.player.MoePlayerService;
import ffts.android.moefmdroid.utils.DebugUtils;

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
    public static final String API_FM_LOG_MUSIC = "ajax/log?log_obj_type=sub&log_type=listen&obj_type=song&api=json";
    public static final String API_MOE_ADD_FAV = "fav/add.json";
    public static final String API_MOE_DELETE_FAV = "fav/delete.json";

    private static MoeClient instance;
    private AsyncHttpClient client = new AsyncHttpClient();

    private MoeClient() {
        super();
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
        DebugUtils.debug(sign(url));
        client.get(sign(url), handler);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        DebugUtils.debug(sign(AsyncHttpClient.getUrlWithQueryString(true, url, params)));
        client.get(sign(AsyncHttpClient.getUrlWithQueryString(true, url, params)), handler);
    }

    public void get(Context context, String url, AsyncHttpResponseHandler handler) {
        DebugUtils.debug(sign(url));
        client.get(context, sign(url), handler);
    }

    public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        DebugUtils.debug(sign(AsyncHttpClient.getUrlWithQueryString(true, url, params)));
        client.get(context, sign(AsyncHttpClient.getUrlWithQueryString(true, url, params)), handler);
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


    /**
     * 获取播放列表
     * @param mode      播放模式
     * @param page      页数
     * @param context   context，用来取消请求，可以为空
     * @param handler   请求结果处理handler
     */
    public void getPlayList(String mode, int page, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("perpage", MoePlayerService.PLAY_LIST_SIZE);
        params.put("fav", mode);
        if (context == null) {
            get(
                    HOST_MOEFM + API_FM_PLAYLIST,
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFM + API_FM_PLAYLIST,
                    params,
                    handler
            );
        }
    }

    /**
     * 播放记录
     * @param songId    歌曲id，sub_id
     * @param context   contedt
     * @param handler   handler
     */
    public void logMusic(int songId, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("obj_id", songId);
        if (context == null) {
            get(
                    HOST_MOEFM + API_FM_LOG_MUSIC,
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFM + API_FM_LOG_MUSIC,
                    params,
                    handler
            );
        }
    }

    /**
     * 收藏/取消收藏歌曲
     * @param isCancel  是否取消
     * @param songId    歌曲id
     * @param context   context
     * @param handler   handler
     */
    public void likeSong(boolean isCancel, int songId, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("fav_obj_type", "song");
        params.put("fav_type", "1");
        params.put("fav_obj_id", songId+"");
        if (context == null) {
            get(
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        }
    }

    /**
     * 抛弃/取消抛弃歌曲
     * @param isCancel  是否取消
     * @param songId    歌曲id
     * @param context   context
     * @param handler   handler
     */
    public void hateSong(boolean isCancel, int songId, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("fav_obj_type", "song");
        params.put("fav_type", "2");
        params.put("fav_obj_id", songId+"");
        if (context == null) {
            get(
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        }
    }

    /**
     * 收藏/取消收藏专辑
     * @param isCancel  是否取消
     * @param albumId    专辑id
     * @param context   context
     * @param handler   handler
     */
    public void likeAlbum(boolean isCancel, int albumId, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("fav_obj_type", "music");
        params.put("fav_type", "1");
        params.put("fav_obj_id", albumId+"");
        if (context == null) {
            get(
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFOU + (isCancel ? API_MOE_DELETE_FAV : API_MOE_ADD_FAV),
                    params,
                    handler
            );
        }
    }

    /**
     * 获取用户信息
     * @param uid       用户id
     * @param context   context
     * @param handler   handler
     */
    public void getUserDetail(long uid, Context context, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        if (context == null) {
            get(
                    HOST_MOEFOU + API_USER_DETAIL,
                    params,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFOU + API_USER_DETAIL,
                    params,
                    handler
            );
        }
    }

    /**
     *
     * @param context   context
     * @param handler   handler
     */
    public void getSelfDetail(Context context, AsyncHttpResponseHandler handler) {
        if (context == null) {
            get(
                    HOST_MOEFOU + API_USER_DETAIL,
                    handler
            );
        } else {
            get(
                    context,
                    HOST_MOEFOU + API_USER_DETAIL,
                    handler
            );
        }
    }

}
