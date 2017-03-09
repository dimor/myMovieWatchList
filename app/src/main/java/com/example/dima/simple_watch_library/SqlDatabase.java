package com.example.dima.simple_watch_library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Dima on 2/7/2017.
 */

public class SqlDatabase extends SQLiteOpenHelper {
    Context context;
    public SqlDatabase(Context context){
        super(context, "sqldatamovies.db",null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlQueryCreateTable = "CREATE TABLE " +DbConstants.TABLE_NAME+ " ("
                + DbConstants.MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.MOVIE_SUBJECT + " TEXT,"
                + DbConstants.MOVIE_BODY + " TEXT,"
                + DbConstants.MOVIE_URL_IMAGE + " TEXT,"
                + DbConstants.MOVIE_YEAR + " TEXT,"
                + DbConstants.MOVIE_IMDB_RATING + " TEXT,"
                + DbConstants.MOVIE_MY_RATING + " TEXT,"
                + DbConstants.MOVIE_RATED + " TEXT,"
                + DbConstants.MOVIE_RELEASED + " TEXT,"
                + DbConstants.MOVIE_RUNTIME + " TEXT,"
                + DbConstants.MOVIE_GENRE + " TEXT,"
                + DbConstants.MOVIE_DIRECTOR + " TEXT,"
                + DbConstants.MOVIE_IMDB_ID + " TEXT,"
                + DbConstants.MOVIE_IMG_STRING +" TEXT, "
                + DbConstants.MOVIE_MANUAL + " INTEGER, "
                + DbConstants.IS_MOVIE_WATCHED + " TEXT "
                + ")";
        Toast.makeText(context, "DB CRATED", Toast.LENGTH_SHORT).show();
        db.execSQL(sqlQueryCreateTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
