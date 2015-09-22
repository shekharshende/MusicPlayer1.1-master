package com.example.root.musicplayer.playlistDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.root.musicplayer.modelClass.Media;

import java.util.ArrayList;

/**
 * Created by root on 31/8/15.
 */
public class PlaylistDbAdapter {

    private static final String KEY_ROW_ID = "_id";
    public static final String KEY_SONG_PATH = "song_path";
    public static final String KEY_SONG_TITLe = "song_title";
    public static final String KEY_MAX_DURATION = "max_Duration";
    private static final String DATABASE_NAME = "playlistdb";
    private static final String TABLE_NAME = "playlist";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_CREATE = "create table playlist(_id integer primary key autoincrement default 1, "
            + "song_title text not null,song_path text not null," + "max_Duration integer not null );";
    private final Context context;
    ArrayList<Media> playlistArrayList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public PlaylistDbAdapter(Context context) {
        this.context = context;
    }

    //add selected songs in a databse
    public long addToPlayList(ArrayList<Media> playlist) {
        int ind;
        boolean isPresent = false;
        playlistArrayList = readPlaylist();
        if (playlistArrayList.size() == 0) {
            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).isChecked() && playlist.get(i).getFile().isFile()) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_SONG_TITLe, playlist.get(i).getFile().getName());
                    values.put(KEY_SONG_PATH, playlist.get(i).getFile().getAbsolutePath());
                    values.put(KEY_MAX_DURATION, playlist.get(i).getFile().length());
                    open();
                    db.insert(TABLE_NAME, null, values);
                }
            }
        } else {
            for (ind = 0; ind < playlist.size(); ind++) {

                if (playlist.get(ind).isChecked()) {
                    isPresent = false;
                    String s = playlist.get(ind).getFile().getAbsolutePath();
                    for (int index = 0; index < playlistArrayList.size(); index++) {
                        String path = playlistArrayList.get(index).getSong_path();
                        if (path.equals(s)) {
                            isPresent = true;
                        }
                    }
                    if (isPresent == false) {

                        if (playlist.get(ind).isChecked() && playlist.get(ind).getFile().isFile()) {
                            ContentValues values = new ContentValues();
                            values.put(KEY_SONG_TITLe, playlist.get(ind).getFile().getName());
                            values.put(KEY_SONG_PATH, playlist.get(ind).getFile().getAbsolutePath());
                            values.put(KEY_MAX_DURATION, playlist.get(ind).getFile().length());
                            open();
                            db.insert(TABLE_NAME, null, values);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public PlaylistDbAdapter open() {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }
//fetch song details based on its rowid
    public Cursor fetchSongPath(long rowid) {
        String songpath = "SELECT * FROM playlist WHERE  _id =" + rowid;
        Cursor cursor;
        open();
        cursor = db.rawQuery(songpath, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

//retrieve the playlist from database
    public ArrayList<Media> readPlaylist() {
        ArrayList<Media> playArraylist = new ArrayList<>();

        String read = "";
        read += "select * from " + TABLE_NAME;
        open();
        Cursor cursor = db.rawQuery(read, null);

        if (cursor.moveToFirst())
            do {
                String song_title = cursor.getString(cursor.getColumnIndex(KEY_SONG_TITLe));
                String song_path = cursor.getString(cursor.getColumnIndex(KEY_SONG_PATH));
                long maxDuration = cursor.getLong(cursor.getColumnIndex(KEY_MAX_DURATION));
                long _id = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_ROW_ID)));
                Media playlist = new Media();
                playlist.set_id(_id);
                playlist.setSong_title(song_title);
                playlist.setSong_path(song_path);
                playlist.setMaxDuration(maxDuration);
                playArraylist.add(playlist);

            } while (cursor.moveToNext());
        cursor.close();
        close();
        return playArraylist;
    }

    public void close() {
        dbHelper.close();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            Log.e("db", "created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS playlist");
            onCreate(db);

        }
    }
}
