package ffts.android.moefmdroid.fragment;

import android.app.Activity;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.modules.Song;
import ffts.android.moefmdroid.player.MoePlayerActivity;
import ffts.android.moefmdroid.player.MoePlayerService;

import static ffts.android.moefmdroid.R.id.player_iv_cover;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoePlayereFragment.MoePlayerController} interface
 * to handle interaction events.
 * Use the {@link MoePlayereFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoePlayereFragment extends Fragment implements MoePlayerService.OnPlayerStatusChangedListener,
        MoePlayerService.OnUpdateListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MoePlayerController mListener;
    private ProgressBar mProgressBar;
    private long mDuration;
    private List<Song> mSongs;
    private int mPage = 1;
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
                if (position != ((MoePlayerActivity) getActivity()).getCurrentIndex()) {
                    ((MoePlayerActivity) getActivity()).getMoePlayerService().play(position);
                }
                if (mSongs.get(position).getFav_sub() != null) {
                    ibLike.setImageResource(R.drawable.btn_liked);
                } else {
                    ibLike.setImageResource(R.drawable.btn_like);
                }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        int seconds = (duration / 1000) % 60;
        int minutes = duration / (1000 * 60);
        String totalTime =
                        (minutes < 10 ? "0"+minutes : minutes) + ":"
                        + (seconds < 10 ? "0"+seconds : seconds);
        time_total.setText(totalTime);
        mProgressBar.setMax(duration);
        songPager.setCurrentItem(index);
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
    public void OnProgressUpdated(int progress, String progressString) {
        time_current.setText(progressString);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void OnSongUpdated(Song song) {

    }

    @Override
    public void OnSongListUpdated(List<Song> songs, boolean needRefresh) {
        if (mSongs == null) {
            mSongs = new ArrayList<Song>();
        }
        if (needRefresh) {
            mSongs.clear();
        }
        mSongs.addAll(songs);
        songPagerAdapter.notifyDataSetChanged();
        if (needRefresh) {
            songPager.setCurrentItem(0);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MoePlayerController {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public void OnPlayClick();

        public void OnNextClick();

        public void OnLikeClick();

        public void OnHateClick();
    }

    class PlayerSongAdapter extends PagerAdapter {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            if (mSongs == null) {
                return 0;
            } else {
                return mSongs.size();
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
            Song songInfo = mSongs.get(position);
            song.setText(songInfo.getTitle());
            album.setText(songInfo.getWiki_title());
            ImageLoader.getInstance().displayImage(songInfo.getCover().getSuqare(), cover);
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
            ((ViewPager) container).removeView((View) object);
        }
    }

}
