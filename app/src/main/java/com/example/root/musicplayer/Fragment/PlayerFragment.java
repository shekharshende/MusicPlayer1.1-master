package com.example.root.musicplayer.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.root.musicplayer.Media.service.MusicService;
import com.example.root.musicplayer.R;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.io.IOException;
import java.util.ArrayList;


public class PlayerFragment extends Fragment implements View.OnClickListener {

    private PlaylistDbAdapter dbHelper;
    private Messenger messenger;
    private String filePath = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private TextView singTitle;
    private SongReciever songReciever;
    private boolean isBound = false;
    private boolean isPlay = false;
    private int position;
    private int maxDuration;
    private int currentposition;
    private SeekBar seekBar;
    private ArrayList<Media> songArrayList;
    private ImageButton playpause, prev, forward;
    private Handler mHandler = new Handler();
    private ServiceConnection musicServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
//            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
//            musicService = binder.getService();
            // Create the Messenger object
            messenger = new Messenger(service);
            Bundle g = getArguments();
            if (g != null) {

                songArrayList = g.getParcelableArrayList("playlistarray");

                filePath = songArrayList.get(position).getSong_path();
                String songTitle = songArrayList.get(position).getSong_title();

                singTitle.setText(songTitle);
                singTitle.setTextColor(Color.WHITE);
            }
            if (filePath != null) {
//                filePath = getArguments().getString("mp3Path");
                playpause.setImageResource(android.R.drawable.ic_media_pause);


                Message msg = Message.obtain(null, MusicService.MSG_GET_SONGLIST, 0, 0);

                Bundle bundle = new Bundle();
//                bundle.putString("songid", filePath);

                // Set the bundle dataBundle bundle = this.getArguments();
                if (bundle != null) {
//                    filePath = bundle.getString("mp3Path", null);
                    bundle.putInt("position", position);
                    bundle.putParcelableArrayList("playlistarray", songArrayList);

                    ;
                }
                msg.setData(bundle);

                // Send the Message to the Service (in another process)
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                // Create a Message
                // Note the usage of MSG_GET_SONGLIST as the what value


                isBound = true;
            }
        }


        public void onServiceDisconnected(ComponentName arg0) {
            messenger = null;
            isBound = false;

        }

    };
    private int duration;


    //set the position of selected song
    public void setPosition(int pos) {
        position = pos;
        Bundle bundle = new Bundle();
        playpause.setImageResource(android.R.drawable.ic_media_pause);
        Message sendPosition = Message.obtain(null, MusicService.MSG_SEND_POSITION, 0, 0);
        bundle.putInt("position", position);
        singTitle.setText(songArrayList.get(position).getSong_title());
        singTitle.setTextColor(Color.WHITE);
        sendPosition.setData(bundle);
        try {
            messenger.send(sendPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        songReciever = new SongReciever(new Handler());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new PlaylistDbAdapter(getActivity());
        dbHelper.open();
        songArrayList = dbHelper.readPlaylist();


        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("reciever", songReciever);
        getActivity().bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_player,
                container, false);
        singTitle = (TextView) view.findViewById(R.id.songName);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        playpause = (ImageButton) view.findViewById(R.id.media_play);
        playpause.setOnClickListener(this);
        prev = (ImageButton) view.findViewById(R.id.media_rew);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        prev.setOnClickListener(this);
        forward = (ImageButton) view.findViewById(R.id.media_ff);
        forward.setOnClickListener(this);


        try {
            mediaPlayer.setDataSource(songArrayList.get(position).getSong_path());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mediaPlayer.getDuration());
//        Log.e("shek", "" + mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.e("progress", "" + progress);
                Message msgSeekTo = Message.obtain(null, MusicService.MSG_SEEK_SONG, 0, 0);
                try {
                    if (progress != 0) {
                        int Songprogress = progress;
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", Songprogress);
                        msgSeekTo.setData(bundle);
                        messenger.send(msgSeekTo);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                seekBar.setProgress(currentposition);
                mHandler.postDelayed(this, 1000);
            }
        });
        return view;
    }

    //get the cuurent position of the song from service
    public void getCurrentDuration(int currentposition) {
        int p = currentposition;
        this.currentposition = p;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.media_rew:
                try {
                    Message msgPrevSong = Message.obtain(null, MusicService.MSG_PREV_SONG, 0, 0);
                    if (messenger != null) {
                        isPlay = true;
                        if (position == 0) {
                            messenger.send(msgPrevSong);
                            position = songArrayList.size() - 1;
                            singTitle.setText(songArrayList.get(position).getSong_title());
                            maxDuration = (int) songArrayList.get(position).getMaxDuration();
                        } else {
//                        if (messenger != null) {
                            messenger.send(msgPrevSong);
                            position--;
                            singTitle.setText(songArrayList.get(position).getSong_title());
                            maxDuration = (int) songArrayList.get(position).getMaxDuration();

                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.media_play:
                if (isPlay == false) {

                    Message msgPlaySong = Message.obtain(null, MusicService.MSG_PlAY_SONG, 0, 0);
                    try {
                        if (messenger != null) {
                            playpause.setImageResource(android.R.drawable.ic_media_pause);
                            messenger.send(msgPlaySong);
                            isPlay = true;

                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                } else {
                    try {
                        if (messenger != null) {
                            playpause.setImageResource(android.R.drawable.ic_media_play);
                            Message msgPauseSong = Message.obtain(null, MusicService.MSG_PAUSE_SONG, 0, 0);
                            messenger.send(msgPauseSong);
                            isPlay = false;

                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.media_ff:
                Message msgNextSong = Message.obtain(null, MusicService.MSG_NEXT_SONG, 0, 0);
                try {
                    isPlay = true;
                    if (messenger != null) {
                        if (position == songArrayList.size() - 1) {
                            messenger.send(msgNextSong);
                            position = 0;
                            singTitle.setText(songArrayList.get(position).getSong_title());
                            maxDuration = (int) songArrayList.get(position).getMaxDuration();

                        } else {
                            messenger.send(msgNextSong);
                            position++;
                            singTitle.setText(songArrayList.get(position).getSong_title());
                            maxDuration = (int) songArrayList.get(position).getMaxDuration();

                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    private class SongReciever extends ResultReceiver {
        public SongReciever(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            maxDuration = (int) resultData.getLong("duration");
            Log.e("rah", "" + maxDuration);
//            seekBar.setMax(maxDuration);
        }
    }
}

