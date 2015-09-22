package com.example.root.musicplayer.Fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.example.root.musicplayer.Adapter.PlayListAdapter;
import com.example.root.musicplayer.MusicPlayer;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.util.ArrayList;

public class PlayListFragment extends ListFragment {
    private ArrayList<Media> playlistArrayList = new ArrayList<Media>();
    private PlayListAdapter playListAdapter;
    private PlaylistDbAdapter dbHelper;
    private MusicPlayer musicPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHelper = new PlaylistDbAdapter(getActivity());
        addDataTolist();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.getItem(2).setVisible(true);
    }

    private void addDataTolist() {
        playlistArrayList.clear();
        playlistArrayList.addAll(dbHelper.readPlaylist());
        playListAdapter = new PlayListAdapter(getActivity(), playlistArrayList);
        playListAdapter.notifyDataSetChanged();
        setListAdapter(playListAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        musicPlayer = (MusicPlayer) activity;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        int pos = position;
        PlayerFragment playerFragmentFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", pos);
        bundle.putParcelableArrayList("playlistarray", playlistArrayList);
        playerFragmentFragment.setArguments(bundle);
        musicPlayer.updateUi(position);
        FragmentManager fragmentManagerforPlaylist = getFragmentManager();
        FragmentTransaction fragmentTransactionforPlaylist = fragmentManagerforPlaylist.beginTransaction();
        fragmentTransactionforPlaylist.remove(getFragmentManager().findFragmentByTag(MusicPlayer.PLAYLIST_FRAGMENT));
        fragmentTransactionforPlaylist.commit();
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);
    }
}
