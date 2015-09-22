package com.example.root.musicplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.musicplayer.Fragment.FileBrowsingFragment;
import com.example.root.musicplayer.R;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.util.ArrayList;

/**
 * Created by root on 27/8/15.
 */
public class FilePickerListAdapter extends ArrayAdapter<Media> {
    Context context;
    FileBrowsingFragment fileBrowsingFragment;
    PlaylistDbAdapter dbHelper;
    private ArrayList<Media> media;

    public FilePickerListAdapter(Context context, ArrayList<Media> objects, FileBrowsingFragment fileBrowsingFragment) {
        super(context, R.layout.fragment_media, android.R.id.text1, objects);
        this.fileBrowsingFragment = fileBrowsingFragment;
        media = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.fragment_media, parent, false);
        } else
            row = convertView;

        final Media media = this.media.get(position);
        ImageView imageView = (ImageView) row.findViewById(R.id.file_picker_image);
        TextView textView = (TextView) row.findViewById(R.id.file_picker_text);
        textView.setSingleLine(true);
        textView.setText(media.getFile().getName());
        final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkbox1);
        checkBox.setVisibility(View.INVISIBLE);
        if (media.getFile().isFile()) {
            checkBox.setVisibility(View.VISIBLE);
        }
        checkBox.setTag(position);
        checkBox.setChecked(media.isChecked());


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer pos = (Integer) checkBox.getTag();
//                Media cl = media.get(pos.intValue());
//                cl.setIsChecked(isChecked);


//               if (isChecked == true) {
//
//                    playlistModelClass = new PlaylistModelClass();
//                    playlistModelClass.setSong_path(media.getFile().getAbsolutePath());
//                    playlistModelClass.setSong_title(media.getFile().getName());
//                   playlistModelClass.setIschekched(true);
//                    playArrayList.add(playlistModelClass);
//                }
//                if (isChecked == false) {
//                    playArrayList.remove(playlistModelClass);
//                }
                fileBrowsingFragment.ischecked(pos, isChecked);

               /* Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("playList", playArrayList);
                Log.e("", "");
*/
            }

        });

        if (media.getFile().isFile()) {
            imageView.setImageResource(R.drawable.ic_songicon);
        } else {
            imageView.setImageResource(R.drawable.ic_folder_openn);
        }
        /*row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fileBrowsingFragment.onLongClick(position);
                return false;
            }
        });*/
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileBrowsingFragment.onClick(position);
            }
        });
        return row;
    }
}

