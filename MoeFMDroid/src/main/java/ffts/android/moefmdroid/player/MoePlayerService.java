package ffts.android.moefmdroid.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaCodecList;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.http.MoeClient;
import ffts.android.moefmdroid.http.MoeDataResponseHandler;
import ffts.android.moefmdroid.http.MoeResultResponseHandler;
import ffts.android.moefmdroid.modules.Fav;
import ffts.android.moefmdroid.modules.Song;
import ffts.android.moefmdroid.utils.ToastUtils;

public class MoePlayerService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener {

    public static final int PLAY_MODE_MAGIC = 0;
    public static final int PLAY_MODE_FAV_SONG = 1;
    public static final int PLAY_MODE_FAV_MUSIC = 2;
    public static final int PLAY_MODE_FAV_RADIO = 3;
    private static final int MESSAGE_UPDATE_PROGRESS = 0;
    private static final int MESSAGE_STOP_UPDATE_PROGRESS = 1;
    public static final int PLAY_STATE_PLAYING = 0;
    public static final int PLAY_STATE_PAUSE = 1;
    public static final int PLAY_STATE_STOP = 2;
    public final Object lock = new Object();

    private final MoePlayerBinder mBinder = new MoePlayerBinder();
    private MediaPlayer mPlayer;
    private List<Song> playList;
    private boolean isLoop = false;
    private boolean isRequesting = false;
    private int playMode = PLAY_MODE_MAGIC;
    private int index = 0;//歌曲index
    private int page = 1;//播放列表页数
    private Song currentSong;
    private int playState = PLAY_STATE_STOP;

    private OnPlayerStatusChangedListener onStatusChangedListener;
    //    private OnPreparedListener onPreparedListener;
    private OnUpdateListener onUpdateListener;
//    private OnCompletedListener onCompletedListener;


    public MoePlayerService() {
        initPlayer();
        playList = new ArrayList<Song>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        requestPlayList(playMode, true);
        return mBinder;
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    /**
     * 请求播放列表
     *
     * @param mode      播放模式
     * @param isRefresh 是否刷新播放列表
     */
    public void requestPlayList(int mode, final boolean isRefresh) {
        if (isRequesting) {
            return;
        }
        if (isRefresh) {
            page = 1;
            index = 0;
        }
        String parMode = "";
        if (mode == 1) {
            parMode = "song";
        } else if (mode == 2) {
            parMode = "music";
        } else if (mode == 3) {
            parMode = "radio";
        }
        MoeClient.getInstance().getPlayList(parMode, page, null,
                new MoeDataResponseHandler<List<Song>>("playlist") {

                    @Override
                    public void onStart() {
                        super.onStart();
                        isRequesting = true;
                    }

                    @Override
                    public void onSuccess(List<Song> data) {
                        super.onSuccess(data);
                        if (isRefresh) {
                            playList.clear();
                        }
                        playList.addAll(data);
                        if (onUpdateListener != null) {
                            onUpdateListener.OnSongListUpdated(playList, isRefresh);
                        }
                        if (page == 1) {
                            play();
                        }
                        page++;
                    }

                    @Override
                    public void onFailure(Throwable e, JSONObject errorResponse) {
                        super.onFailure(e, errorResponse);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        isRequesting = false;
                    }
                }
        );
    }

    private void play() {
        if (mPlayer == null) {
            return;
        }
        if (playList == null || playList.size() < 0) {
            return;
        }
        currentSong = playList.get(index);
        Uri uri = Uri.parse(currentSong.getUrl());
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mPlayer.prepareAsync();
        if ((playList.size() - index) < 3) {
            requestPlayList(playMode, false);
        }
    }

    public void play(int index) {
        this.index = index;
        if (index < playList.size()) {
            play();
        }
    }

    public void pause() {
        if (mPlayer != null && playState == PLAY_STATE_PLAYING) {
            mPlayer.pause();
            playState = PLAY_STATE_PAUSE;
            if (onStatusChangedListener != null) {
                onStatusChangedListener.OnPaused();
            }
        }
    }

    public void resume() {
        if (mPlayer != null) {
            mPlayer.start();
            playState = PLAY_STATE_PLAYING;
            if (onStatusChangedListener != null) {
                onStatusChangedListener.OnResume();
            }
        }
    }

    public void next() {
        index++;
        if (index < playList.size()) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            play();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.start();
        playState = PLAY_STATE_PLAYING;
        if (onStatusChangedListener != null) {
            onStatusChangedListener.OnPrepared(playList.get(index), mPlayer.getDuration(), index);
            onStatusChangedListener.OnResume();
        }
        changeProgress(true);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MoeClient.getInstance().logMusic(
                currentSong.getSub_id(),
                this,
                new AsyncHttpResponseHandler()
        );
        changeProgress(false);
        playState = PLAY_STATE_STOP;
        next();
        if (onStatusChangedListener != null) {
            onStatusChangedListener.OnCompleted(index);
        }
    }

    public class MoePlayerBinder extends Binder {
        public MoePlayerService getService() {
            return MoePlayerService.this;
        }
    }

    /**
     * 播放器状态监听器
     */
    public interface OnPlayerStatusChangedListener {
        /**
         * 歌曲准备完毕，缓冲完毕，可以播放
         *
         * @param song     歌曲对象
         * @param duration 歌曲时常
         * @param index    歌曲index
         */
        public void OnPrepared(Song song, int duration, int index);

        /**
         * 歌曲暂停
         */
        public void OnPaused();

        /**
         * 歌曲恢复播放
         */
        public void OnResume();

        /**
         * 歌曲播放完毕
         *
         * @param nextIndex 下一首的index
         */
        public void OnCompleted(int nextIndex);
    }

    public void setOnStatusChangedListener(OnPlayerStatusChangedListener listener) {
        this.onStatusChangedListener = listener;
    }

    public interface OnPreparedListener {
        public void OnPrepared(Song song, int duration, int index);
    }

//    public void setOnPreoaredListener(OnPreparedListener listener) {
//        this.onPreparedListener = listener;
//    }

    public interface OnCompletedListener {
        public void OnCompleted(int nextIndex);
    }

//    public void setOnCompletedListener(OnCompletedListener listener) {
//        this.onCompletedListener = listener;
//    }

    public interface OnUpdateListener {
        public void OnProgressUpdated(int progress, String progressString);

        public void OnSongUpdated(Song song);

        public void OnSongListUpdated(List<Song> songs, boolean needRefresh);

        public void OnLiked(Song song, int index);

        public void OnHated(List<Song> list);
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.onUpdateListener = listener;
    }

    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE_PROGRESS:
                    if (mPlayer != null) {
                        setProgress();
                        progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
                    }
                    break;
                case MESSAGE_STOP_UPDATE_PROGRESS:
                    break;
            }
        }
    };

    private void changeProgress(boolean isChangeProgress) {
        if (isChangeProgress) {
            progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
        } else {
            progressHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
        }
    }

    private void setProgress() {
        int milliseconds = mPlayer.getCurrentPosition();
        int seconds = (milliseconds / 1000) % 60;
        int minutes = milliseconds / (1000 * 60);
        String currentTime =
                (minutes < 10 ? "0" + minutes : minutes) + ":"
                        + (seconds < 10 ? "0" + seconds : seconds);
        if (onUpdateListener != null) {
            onUpdateListener.OnProgressUpdated(milliseconds, currentTime);
        }
    }

    public List<Song> getSongs() {
        return this.playList;
    }

    public int getCurrentIndex() {
        return index;
    }

    public void togglePlaying() {
        if (playState == PLAY_STATE_STOP) {
            play();
        } else if (playState == PLAY_STATE_PAUSE) {
            resume();
        } else if (playState == PLAY_STATE_PLAYING) {
            pause();
        }
    }

    public void like() {
        if (currentSong.getFav_sub() != null) {
            like(true);
        } else {
            like(false);
        }
    }

    public void like(final boolean isCancel) {
        synchronized (lock) {
            MoeDataResponseHandler<Fav> add = new MoeDataResponseHandler<Fav>("fav") {
                @Override
                public void onSuccess(Fav data) {
                    currentSong.setFav_sub(data);
                    playList.set(index, currentSong);
                    if (onUpdateListener != null) {
                        onUpdateListener.OnLiked(currentSong, index);
                    }
                    ToastUtils.toast(getResources().getString(R.string.msg_like_success));
                }
            };
            MoeDataResponseHandler<Integer> delete = new MoeDataResponseHandler<Integer>("fav_id"){
                @Override
                public void onSuccess(Integer data) {
                    currentSong.setFav_sub(null);
                    playList.set(index, currentSong);
                    if (onUpdateListener != null) {
                        onUpdateListener.OnLiked(currentSong, index);
                    }
                }
            };
            MoeClient.getInstance().likeMusic(isCancel, currentSong.getSub_id(), this, isCancel?delete:add);
        }
    }

    public void hate(final boolean isCancel) {
        synchronized (lock) {
            MoeDataResponseHandler<Fav> add = new MoeDataResponseHandler<Fav>("fav"){
                @Override
                public void onSuccess(Fav data) {
                    super.onSuccess(data);
                    //todo 仍需优化
                    for (Iterator<Song> iterator = playList.iterator(); iterator.hasNext(); ) {
                        Song song = iterator.next();
                        if (song.getSub_id() == data.getFav_obj_id()) {
                            iterator.remove();
                            play();
                            if (onUpdateListener != null) {
                                onUpdateListener.OnHated(playList);
                            }
                            break;
                        }
                    }
                }
            };
            MoeDataResponseHandler<Integer> delete = new MoeDataResponseHandler<Integer>("fav"){
                ///todo 待实现
            };
            MoeClient.getInstance().hateMusic(isCancel, currentSong.getSub_id(), this, isCancel?delete:add);
        }
    }

}
