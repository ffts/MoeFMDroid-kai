package ffts.android.moefmdroid.player;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import ffts.android.moefmdroid.modules.Song;

/**
 * Created by ffts on 14-3-22.
 * Email:ffts133@gmail.com
 */
public class MoePlayer extends MediaPlayer{
    private static MoePlayer mInstance;
    private MediaPlayer mediaPlayer;
    private List<Song> playList;

    private MoePlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static MoePlayer getInstance() {
        if (mInstance == null) {
            mInstance = new MoePlayer();
        }
        return mInstance;
    }

    public void setPlayList(List<Song> list) {
        this.playList = list;
    }

    public void play() {
        if (playList != null && playList.size() > 0) {

        }
    }
}
