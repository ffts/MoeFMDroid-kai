package ffts.android.moefmdroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.modules.Song;
import ffts.android.moefmdroid.player.MoePlayerActivity;
import ffts.android.moefmdroid.player.MoePlayerController;
import ffts.android.moefmdroid.player.MoePlayerService;

import static ffts.android.moefmdroid.R.id.player_iv_cover;


public class MoePlayereFragment extends Fragment implements MoePlayerService.OnPlayerStatusChangedListener,
        MoePlayerService.OnUpdateListener, View.OnClickListener, MoePlayerActivity.OnServiceBindListener {

    private MoePlayerController mListener;
    private ProgressBar mProgressBar;
    private long mDuration;
    private List<Song> mSongs;
    private MoePlayerService moePlayerService;
    private ViewPager songPager;
    private PlayerSongAdapter songPagerAdapter;
    private TextView time_current, time_total;
    private ImageButton ibPlay, ibNext, ibLike, ibHate;

    public static MoePlayereFragment newInstance() {
        MoePlayereFragment fragment = new MoePlayereFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    public MoePlayereFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
//        mSongs = moePlayerService.getSongs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moe_player, container, false);
        songPager = (ViewPager) view.findViewById(R.id.player_viewpager);
        songPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position % MoePlayerService.PLAY_LIST_SIZE != ((MoePlayerActivity) getActivity()).getCurrentIndex()) {
                    ((MoePlayerActivity) getActivity()).getMoePlayerService().play(position % MoePlayerService.PLAY_LIST_SIZE);
                }
                if (moePlayerService.getCurrentSong().getFav_sub() != null) {
                    ibLike.setImageResource(R.drawable.btn_liked);
                } else {
                    ibLike.setImageResource(R.drawable.btn_like);
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        songPagerAdapter = new PlayerSongAdapter();
        songPager.setAdapter(songPagerAdapter);
        time_current = (TextView) view.findViewById(R.id.player_tv_current_time);
        time_total = (TextView) view.findViewById(R.id.player_tv_total_time);
        mProgressBar = (ProgressBar) view.findViewById(R.id.player_pb_progress);
        ibPlay = (ImageButton) view.findViewById(R.id.player_ib_play);
        ibPlay.setOnClickListener(this);
        ibNext = (ImageButton) view.findViewById(R.id.player_ib_next);
        ibNext.setOnClickListener(this);
        ibLike = (ImageButton) view.findViewById(R.id.player_ib_like);
        ibLike.setOnClickListener(this);
        ibHate = (ImageButton) view.findViewById(R.id.player_ib_hate);
        ibHate.setOnClickListener(this);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        moePlayerService = ((MoePlayerActivity) getActivity()).getMoePlayerService();
        if (moePlayerService != null) {
            mSongs = moePlayerService.getSongs();
            songPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MoePlayerController) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MoePlayerController");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnPrepared(Song song, int duration, int index) {

    }

    @Override
    public void OnPaused() {
        ibPlay.setImageResource(R.drawable.btn_play);
    }

    @Override
    public void OnResume() {
        ibPlay.setImageResource(R.drawable.btn_pause);
    }

    @Override
    public void OnCompleted(int nextIndex) {
        songPager.setCurrentItem(nextIndex, true);
    }

    @Override
    public void OnNext(int nextIndex) {
        songPager.setCurrentItem(nextIndex, true);
    }

    @Override
    public void OnProgressUpdated(int progress, String progressString) {
        time_current.setText(progressString);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void OnSongUpdated(Song song) {
        time_current.setText("00:00");
        time_total.setText(song.getStream_time());
        mProgressBar.setMax(Integer.valueOf(song.getStream_length()) * 1000);
        getActivity().invalidateOptionsMenu();
        if (song.getFav_sub() == null) {
            ibLike.setImageResource(R.drawable.btn_like);
        } else {
            ibLike.setImageResource(R.drawable.btn_liked);
        }
    }

    @Override
    public void OnSongListUpdated(List<Song> songs, boolean needRefresh) {
        mSongs = songs;
        if (needRefresh) {
            songPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnLiked(Song song, int index) {
        if (song.getFav_sub() != null) {
            ibLike.setImageResource(R.drawable.btn_liked);
        } else {
            ibLike.setImageResource(R.drawable.btn_like);
        }
        mSongs.set(index, song);
    }

    @Override
    public void OnHated(List<Song> list) {
        mSongs = list;
        songPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnLikedAlbum(Song song) {
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_ib_play:
                if (mListener != null) {
                    mListener.OnPlayClick();
                }
                break;
            case R.id.player_ib_next:
                if (mListener != null) {
                    mListener.OnNextClick();
                    songPager.setCurrentItem(moePlayerService.getCurrentIndex());
                }
                break;
            case R.id.player_ib_like:
                if (mListener != null) {
                    mListener.OnLikeClick();
                }
                break;
            case R.id.player_ib_hate:
                if (mListener != null) {
                    mListener.OnHateClick();
                }
                break;
        }
    }

    @Override
    public void onServiceBind(MoePlayerService service) {
        moePlayerService = service;
    }

    class PlayerSongAdapter extends PagerAdapter {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            if (mSongs == null) {
                return 0;
            } else {
                return 1000;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.layout_player_song, container, false);
            if (view == null) {
                return null;
            }
            TextView song = (TextView) view.findViewById(R.id.player_tv_song_title);
            TextView album = (TextView) view.findViewById(R.id.player_tv_album_title);
            ImageView cover = (ImageView) view.findViewById(player_iv_cover);
            Song songInfo = mSongs.get(position % MoePlayerService.PLAY_LIST_SIZE);
            song.setText(songInfo.getTitle());
            album.setText(songInfo.getWiki_title());
            ImageLoader.getInstance().displayImage(songInfo.getCover().getLarge(), cover);
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private MoePlayerActivity getMoePlayerActivity() {
        return (MoePlayerActivity) getActivity();
    }

    private MoePlayerService getMoePlayerService() {
        if (moePlayerService == null) {
            moePlayerService = getMoePlayerActivity().getMoePlayerService();
        }
        return moePlayerService;
    }

}
