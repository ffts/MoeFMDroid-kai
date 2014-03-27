package ffts.android.moefmdroid.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
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
import java.util.List;

import ffts.android.moefmdroid.http.MoeClient;
import ffts.android.moefmdroid.http.MoeDataResponseHandler;
import ffts.android.moefmdroid.modules.Song;

public class MoePlayerService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener {

    public static final int PLAY_MODE_MAGIC = 0;
    public static final int PLAY_MODE_FAV_SONG = 1;
    public static final int PLAY_MODE_FAV_MUSIC = 2;
    public static final int PLAY_MODE_FAV_RADIO = 3;
    private static final int MESSAGE_UPDATE_PROGRESS = 0;
    private static final int MESSAGE_STOP_UPDATE_PROGRESS = 1;

    private final MoePlayerBinder mBinder = new MoePlayerBinder();
    private MediaPlayer mPlayer;
    private List<Song> playList;
    private boolean isLoop = false;
    private boolean isRequesting = false;
    private int playMode = PLAY_MODE_MAGIC;
    private int index = 0;//歌曲index
    private int page = 1;//播放列表页数
    private Song currentSong;

    private OnPreparedListener onPreparedListener;
    private OnUpdateListener onUpdateListener;
    private OnCompletedListener onCompletedListener;

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
     * @param mode 播放模式
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

    private void next() {
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
        if (onPreparedListener != null) {
            onPreparedListener.OnPrepared(playList.get(index), mPlayer.getDuration());
        }
        changeProgress(true);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MoeClient.getInstance().logMusic(
                Integer.toString(currentSong.getSub_id()),
                this,
                new AsyncHttpResponseHandler()
        );
        changeProgress(false);
        next();
        if (onCompletedListener != null) {
            onCompletedListener.OnCompleted(index);
        }
    }

    public class MoePlayerBinder extends Binder {
        public MoePlayerService getService() {
            return MoePlayerService.this;
        }
    }

    public interface OnPreparedListener {
        public void OnPrepared(Song song, int duration);
    }

    public void setOnPreoaredListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    public interface OnCompletedListener {
        public void OnCompleted(int nextIndex);
    }

    public void setOnCompletedListener(OnCompletedListener listener) {
        this.onCompletedListener = listener;
    }

    public interface OnUpdateListener {
        public void OnProgressUpdated(int progress, String progressString);

        public void OnSongUpdated(Song song);

        public void OnSongListUpdated(List<Song> songs, boolean needRefresh);
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

}
