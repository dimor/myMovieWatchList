package com.example.dima.simple_watch_library;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Dima on 3/7/2017.
 */

public class CheckMovieExist {
        Context context;
        Cursor  cursor ;
        String movieTitle;
    public CheckMovieExist(Cursor cursor,String movieTitle) {
        this.cursor =cursor;
        this.movieTitle = movieTitle;
    }


 public boolean check(){
     boolean exist=false;
     while(cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.MOVIE_SUBJECT)).equals(movieTitle)) {
                exist = true;
            }
        }
 return exist;
 }

    /*
    Boolean movieExistIndatabse=false;



    */
}
