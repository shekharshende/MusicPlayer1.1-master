package com.example.root.musicplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.root.musicplayer.Fragment.FileBrowsingFragment;
import com.example.root.musicplayer.Fragment.PlayListFragment;
import com.example.root.musicplayer.Fragment.PlayerFragment;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.util.ArrayList;

public class MusicPlayer extends Activity {

    public static String PLAYER_FRAGMENT = "playerFragment";
    public static String PLAYLIST_FRAGMENT = "playlist";
    public static String FILEBROWSER_FRAGMENT = "filebrowser";
    private ArrayList<Media> playlistArrayList = new ArrayList<>();
    private PlaylistDbAdapter dbHelper;
    FileBrowsingFragment fileBrowsingFragment;
    Menu menu;

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            int currentPosition = intent.getIntExtra("message", 0);
            Log.e("s", "" + currentPosition);
            int maxDuration = intent.getIntExtra("duration", 0);
            PlayerFragment playerFragment = (PlayerFragment) getFragmentManager().findFragmentById(R.id.FragmentContainer);
            playerFragment.getCurrentDuration(currentPosition);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_browsing_file);
        dbHelper = new PlaylistDbAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file_browsing, menu);
        this.menu = menu;
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.gotoList:
                PlayListFragment playListFragment = new PlayListFragment();
                FragmentManager listManager = getFragmentManager();
                if (getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT) != null) {
                    FragmentTransaction listTransaction = listManager.beginTransaction();
                    listTransaction.remove(getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT));
                    listTransaction.replace(R.id.relativeLayout, playListFragment, PLAYLIST_FRAGMENT);
                    listTransaction.commit();

                } else {
                    FragmentTransaction listTransaction = listManager.beginTransaction();
                    listTransaction.replace(R.id.relativeLayout, playListFragment, PLAYLIST_FRAGMENT);
                    listTransaction.addToBackStack(null);
                    listTransaction.commit();
                }
                break;

            case R.id.fileOpen:
                fileBrowsingFragment = new FileBrowsingFragment();
                FragmentManager fm = getFragmentManager();
                if (getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT) != null) {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.remove(getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT));
                    fragmentTransaction.replace(R.id.relativeLayout, fileBrowsingFragment, FILEBROWSER_FRAGMENT);
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.relativeLayout, fileBrowsingFragment, FILEBROWSER_FRAGMENT);
                    fragmentTransaction.commit();
                }
                break;

            case R.id.addToPlaylist:
                dbHelper.open();
                dbHelper.addToPlayList(playlistArrayList);
                PlayListFragment playListFragment1 = new PlayListFragment();
                FragmentManager addtoPlaylistManager = getFragmentManager();
                if (getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT) != null) {
                    FragmentTransaction playlistTransaction = addtoPlaylistManager.beginTransaction();
                    playlistTransaction.remove(getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT));
                    playlistTransaction.replace(R.id.relativeLayout, playListFragment1, PLAYLIST_FRAGMENT);
                    playlistTransaction.commit();

                } else {
                    FragmentTransaction playlistTransaction = addtoPlaylistManager.beginTransaction();
                    playlistTransaction.replace(R.id.relativeLayout, playListFragment1, PLAYLIST_FRAGMENT);
                    playlistTransaction.commit();

                }
                break;

            case R.id.gotoPlay:
                FragmentManager fragmentManagerforPlaylist2 = getFragmentManager();
                FragmentManager playerManager = getFragmentManager();
                if (getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT) != null) {
                    FragmentTransaction playerTransaction = fragmentManagerforPlaylist2.beginTransaction();
                    playerTransaction.remove(getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT));
                    playerTransaction.commit();
                }
                if (getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT) != null) {
                    FragmentTransaction playerTransaction = playerManager.beginTransaction();
                    playerTransaction.remove(getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT));
                    playerTransaction.commit();
                }
                break;
        }
        return true;

    }

    public void updateUi(int position) {
        PlayerFragment playerFragment = (PlayerFragment) this.getFragmentManager().findFragmentById(R.id.FragmentContainer);
        playerFragment.setPosition(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }

    @Override
    public void onBackPressed() {

        Log.e("Count :::", "" + getFragmentManager().getBackStackEntryCount());
        if (fileBrowsingFragment != null) {
            if (fileBrowsingFragment.Directory.getParentFile() != null) {
                fileBrowsingFragment.Directory = fileBrowsingFragment.Directory.getParentFile();
                fileBrowsingFragment.refreshFilesList();
                return;
            }
        }
        removeFragOnBackPress();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            moveTaskToBack(true);
        }
    }

    public void playlist(ArrayList<Media> media) {
        this.playlistArrayList = media;
        for (int i = 0; i < playlistArrayList.size(); i++) {
            if (playlistArrayList.get(i).isChecked() == true) {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(true);
                break;
            } else {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(false);
            }
        }
    }

    private void removeFragOnBackPress() {
        FragmentManager fragmentManagerForFileBrowsing = getFragmentManager();
        if (getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT) != null) {
            FragmentTransaction fragmentTransactionForFileBrowsing = fragmentManagerForFileBrowsing.beginTransaction();
            fragmentTransactionForFileBrowsing.remove(getFragmentManager().findFragmentByTag(FILEBROWSER_FRAGMENT));
            fragmentTransactionForFileBrowsing.commit();
        }
        FragmentManager fragmentManagerforPlaylistRemove = getFragmentManager();
        if (getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT) != null) {
            FragmentTransaction fragmentTransactionforPlaylist = fragmentManagerforPlaylistRemove.beginTransaction();
            fragmentTransactionforPlaylist.remove(getFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT));
            fragmentTransactionforPlaylist.commit();
        }
    }
}