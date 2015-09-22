package com.example.root.musicplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.musicplayer.R;
import com.example.root.musicplayer.modelClass.Media;

import java.util.ArrayList;

/**
 * Created by root on 31/8/15.
 */
public class PlayListAdapter extends ArrayAdapter<Media> {
    ArrayList<Media> playlistArray;
    private Context context;

    public PlayListAdapter(Context context, ArrayList<Media> playlistArray) {
        super(context, R.layout.fragment_list, android.R.id.text1, playlistArray);
        this.playlistArray = playlistArray;
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.fragment_list, parent, false);
        } else
            row = convertView;

        Media modelclass = playlistArray.get(position);

        TextView textView = (TextView) row.findViewById(R.id.itemOnList);
        textView.setSingleLine(true);
        textView.setText(modelclass.getSong_title());
        ImageView imageView = (ImageView) row.findViewById(R.id.playlist_image);
        imageView.setImageResource(R.drawable.ic_songicon);
        return row;
    }

}