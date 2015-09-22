package com.example.root.musicplayer.Fragment;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.root.musicplayer.Adapter.FilePickerListAdapter;
import com.example.root.musicplayer.Comparator.FileComparator;
import com.example.root.musicplayer.MusicPlayer;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

public class FileBrowsingFragment extends ListFragment implements com.example.root.musicplayer.listner.Playlist {

    public final static String FILE_PATH = "/";

    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String MP3FileExtension = "mp3";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/";
    public File Directory;
    private ArrayList<Media> mediaFile;
    private FilePickerListAdapter filePickerListAdapter;
    private boolean ShowHiddenFiles = false;
    private PlaylistDbAdapter dbHelper;
    private MenuItem playlist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Directory = new File(DEFAULT_INITIAL_DIRECTORY);
        // Initialize the ArrayList
        mediaFile = new ArrayList<Media>();
        // Set the ListAdapter
        filePickerListAdapter = new FilePickerListAdapter(getActivity(), mediaFile, this);
        setListAdapter(filePickerListAdapter);//u get list
        dbHelper = new PlaylistDbAdapter(getActivity());
        dbHelper.open();
        Directory = new File(FILE_PATH);
        if (getActivity().getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES))
            ShowHiddenFiles = getActivity().getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        refreshFilesList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.getItem(2).setVisible(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //refresh list
    public void refreshFilesList() {

        mediaFile.clear();
        ExtensionFilenameFilter filter =
                new ExtensionFilenameFilter(MP3FileExtension);

        File[] files = Directory.listFiles(filter);

        if (files != null && files.length > 0) {

            for (File f : files) {
                Media media = new Media();

                if (f.isHidden() && !ShowHiddenFiles) {

                    continue;
                }

                media.setFile(f);
                mediaFile.add(media);

            }

            Collections.sort(mediaFile, new FileComparator());
        }
        filePickerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int position) {
        File newFile = mediaFile.get(position).getFile();

        if (newFile.isFile()) {
        } else {

            Directory = newFile;
            refreshFilesList();
        }
    }

    @Override
    public void ischecked(Integer position, boolean b) {


        mediaFile.get(position).setIsChecked(b);
        filePickerListAdapter.notifyDataSetChanged();

        MusicPlayer musicPlayer = (MusicPlayer) getActivity();
        musicPlayer.playlist(mediaFile);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);
    }

    public class ExtensionFilenameFilter implements FilenameFilter {

        String mp3Extention;

        public ExtensionFilenameFilter(String extensions) {

            super();
            mp3Extention = extensions;
        }

        public boolean accept(File dir, String filename) {

            if (new File(dir, filename).isDirectory()) {

                // Accept all directory names
                return true;
            }
            if (filename.endsWith(mp3Extention)) {

                // The filename ends with the extension
                return true;
            }

            // The filename did not match any of the extensions
            return false;
        }

    }
}


