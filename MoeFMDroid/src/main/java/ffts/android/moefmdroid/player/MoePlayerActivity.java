package ffts.android.moefmdroid.player;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import ffts.android.moefmdroid.R;
import ffts.android.moefmdroid.fragment.MoePlayereFragment;

public class MoePlayerActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
        MoePlayerController {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private MoePlayerService moePlayerService;

    private MoePlayereFragment playereFragment = MoePlayereFragment.newInstance();

    private boolean isMenuFirstSeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moe_player);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_play_mode_magic),
                                getString(R.string.title_play_mode_song),
                                getString(R.string.title_play_mode_music),
                                getString(R.string.title_play_mode_radio)
                        }
                ),
                this
        );
        bindPlayService();
        initPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (moePlayerService != null) {
            moePlayerService.removeOnStatusChangedListener();
            moePlayerService.removeOnUpdateListener();
        }
    }

    private void initPlayer() {
        getSupportFragmentManager().beginTransaction().add(R.id.container, playereFragment).commit();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem fav = menu.findItem(R.id.action_fav_album);
        if (fav != null && moePlayerService != null) {
            if (!moePlayerService.isFavAlbum()) {
                fav.setIcon(R.drawable.btn_ablum_like);
            } else {
                fav.setIcon(R.drawable.btn_ablum_liked);
            }
        }
        MenuItem loop = menu.findItem(R.id.action_loop);
        if (loop != null && moePlayerService != null) {
            if (moePlayerService.isLooping()) {
                loop.setIcon(R.drawable.ico_looping);
            } else {
                loop.setIcon(R.drawable.ico_loop);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.moe_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_fav_album) {
            moePlayerService.likeAlbum();
            return true;
        }
        if (id == R.id.action_loop) {
            moePlayerService.toggleLoop();
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        if (!isMenuFirstSeleted) {
            isMenuFirstSeleted = true;
            return false;
        }
        if (moePlayerService != null) {
            moePlayerService.requestPlayList(position, true);
        }
        return true;
    }

    private void startPlayService() {
        Intent intent = new Intent(this, MoePlayerService.class);
        startService(intent);
    }

    private void bindPlayService() {
        Intent intent = new Intent(this, MoePlayerService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                moePlayerService = ((MoePlayerService.MoePlayerBinder) iBinder).getService();
                if (playereFragment != null) {
                    playereFragment.onServiceBind(moePlayerService);
                }
                moePlayerService.setOnStatusChangedListener(playereFragment);
                moePlayerService.setOnUpdateListener(playereFragment);
//                moePlayerService.requestPlayList(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    //todo 播放控制

    @Override
    public void OnPlayClick() {
        moePlayerService.togglePlaying();
    }

    @Override
    public void OnNextClick() {
        moePlayerService.next();
    }

    @Override
    public void OnLikeClick() {
        moePlayerService.like();
    }

    @Override
    public void OnHateClick() {
        moePlayerService.hate(false);
    }

    @Override
    public void OnLikeAlbumClick() {
        moePlayerService.likeAlbum();
    }

    public MoePlayerService getMoePlayerService() {
        return moePlayerService;
    }

    public int getCurrentIndex() {
        return moePlayerService.getCurrentIndex();
    }

    public interface OnServiceBindListener {
        public void onServiceBind(MoePlayerService service);
    }

}
