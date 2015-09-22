package com.example.root.musicplayer.Media.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.root.musicplayer.MusicPlayer;
import com.example.root.musicplayer.R;
import com.example.root.musicplayer.modelClass.Media;
import com.example.root.musicplayer.playlistDB.PlaylistDbAdapter;

import java.util.ArrayList;

public class MusicService extends Service implements android.media.MediaPlayer.OnPreparedListener, android.media.MediaPlayer.OnErrorListener, android.media.MediaPlayer.OnCompletionListener {

    public static final int MSG_GET_SONGLIST = 1;
    public static final int MSG_PREV_SONG = 2;
    public static final int MSG_NEXT_SONG = 3;
    public static final int MSG_PlAY_SONG = 4;
    public static final int MSG_PAUSE_SONG = 5;
    public static final int MSG_SEEK_SONG = 6;
    public static final int MSG_SEND_POSITION = 7;
    private android.media.MediaPlayer mediaPlayer = new android.media.MediaPlayer();
    private ResultReceiver resultReceiver;
    private Messenger messenger = new Messenger(new IncomingHandler());
    private Handler uiHandler = new Handler();
    private ArrayList<Media> songArrayList = null;
    private int songPosition;
    private String songTitle;
    private PlaylistDbAdapter dbHelper;
    private int NOTIFY_ID = 1;
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            sendMessage();
//            Log.e("startTime", "" + startTime);
            uiHandler.postDelayed(this, 1000);
        }
    };

    public MusicService() {

    }

    //play song
    public void playSong(String mp3FilePath, String title) {
        songTitle = title;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();


        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(mp3FilePath);

        } catch (Exception e) {
            Log.e("PLAYER SERVICE", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrepared(final android.media.MediaPlayer mp) {
        Bundle bundle = new Bundle();
        bundle.putLong("duration", mp.getDuration());

        mediaPlayer = mp;
        resultReceiver.send(1, bundle);
        mp.start();
        Intent notIntent = new Intent(this, MusicPlayer.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setLights(Color.GREEN, 100, 100)
                .setContentTitle(songTitle)
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
//        initNotification();
        uiHandler.postDelayed(UpdateSongTime, 1000);
        //mp.prepareAsync();
    }


    private void initNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_play;
        String s = songTitle;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, s, when);
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        Context context = getApplicationContext();
        String contentTitle = songTitle;
        String contentText = songTitle;
        Intent intent = new Intent(this, MusicPlayer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
        notificationManager.notify(NOTIFY_ID, notification);

    }

    //cancel the notification when service destroy
    private void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

        notificationManager.cancel(NOTIFY_ID);
    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
        mp.reset();
        if (songPosition < songArrayList.size() - 1) {
            if (songPosition == 0) {
                playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());

            } else {
                songPosition++;
                playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        cancelNotification();
    }

    //play paused song

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        cancelNotification();

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.e("Song List ::", "onhi");

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        dbHelper = new PlaylistDbAdapter(getApplicationContext());
        dbHelper.open();
        songArrayList = dbHelper.readPlaylist();
    }


    @Override
    public boolean onError(android.media.MediaPlayer mp, int what, int extra) {

        return false;
    }

    //pause the playing song

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        resultReceiver = intent.getParcelableExtra("reciever");


        return messenger.getBinder();
    }

    private void seekSong(int songProgress) {
        mediaPlayer.seekTo(songProgress);

    }

    // Send an Intent with an action named "my-event".
    private void sendMessage() {
        Intent intent = new Intent("my-event");
        intent.putExtra("message", mediaPlayer.getCurrentPosition());
        Log.e("message", "" + mediaPlayer.getCurrentPosition());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // Incoming messages Handler
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case MSG_GET_SONGLIST:
                    bundle = msg.getData();
//                    songArrayList = bundle.getParcelableArrayList("arraylist");
                    String mp3FilePath;
                    songPosition = bundle.getInt("position");
                    songArrayList = dbHelper.readPlaylist();
                    String title = songArrayList.get(songPosition).getSong_title();
                    mp3FilePath = songArrayList.get(songPosition).getSong_path();
                    playSong(mp3FilePath, title);
                    break;

                case MSG_NEXT_SONG:
                    if (songPosition == songArrayList.size() - 1) {
                        songPosition = 0;
                        playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
                    } else {
                        songPosition++;
                        playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
                    }
                    break;

                case MSG_PREV_SONG:
                    if (songPosition == 0) {
                        songPosition = songArrayList.size() - 1;
                        playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
                    } else {
                        songPosition--;
                        playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
                    }
                    break;

                case MSG_PlAY_SONG:
                    play();
                    break;

                case MSG_PAUSE_SONG:
                    pause();
                    break;

                case MSG_SEEK_SONG:
                    bundle = msg.getData();
                    int songProgress = bundle.getInt("progress");
                    seekSong(songProgress);
                    break;

                case MSG_SEND_POSITION:
                    bundle = msg.getData();
                    songPosition = bundle.getInt("position");
                    playSong(songArrayList.get(songPosition).getSong_path(), songArrayList.get(songPosition).getSong_title());
                    break;

                default:
                    super.handleMessage(msg);
            }

        }
    }


}
