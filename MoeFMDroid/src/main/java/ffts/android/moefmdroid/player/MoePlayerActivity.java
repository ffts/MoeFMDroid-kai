package ffts.android.moefmdroid.player;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import ffts.android.moefmdroid.MoePlayereFragment;
import ffts.android.moefmdroid.R;

public class MoePlayerActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
        MoePlayereFragment.MoePlayerController {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private MoePlayerService moePlayerService;

    private MoePlayereFragment playereFragment = MoePlayereFragment.newInstance();

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
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
                moePlayerService = ((MoePlayerService.MoePlayerBinder)iBinder).getService();
                moePlayerService.setOnStatusChangedListener(playereFragment);
                moePlayerService.setOnUpdateListener(playereFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    public MoePlayerService getMoePlayerService() {
        return moePlayerService;
    }

    public int getCurrentIndex() {
        return moePlayerService.getCurrentIndex();
    }
}
