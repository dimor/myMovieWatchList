package com.example.dima.my_movie_watch_list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dima on 2/19/2017.
 */

public class MovieListAdapter extends CursorAdapter {

    public MovieListAdapter(Context context, Cursor c) {
        super(context, c);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v= LayoutInflater.from(context).inflate(R.layout.main_listview_item , null);

        return v;
    }

    @Override
     public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_SUBJECT));
        String year = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_YEAR));
        String imdbRating = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMDB_RATING));
        String imageString = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMG_STRING));
        String myRate = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_MY_RATING));
        TextView titleTV = (TextView)view.findViewById(R.id.titleTv);
        TextView yearTV = (TextView)view.findViewById(R.id.yearTV);
        TextView imdbTV = (TextView)view.findViewById(R.id.imdbTV);
        TextView myRatting = (TextView)view.findViewById(R.id.myRatingTV);
        String isMovieWatched = cursor.getString(cursor.getColumnIndex(DbConstants.IS_MOVIE_WATCHED));
        ImageView watched = (ImageView)view.findViewById(R.id.isMovieWatchedIV) ;


        titleTV.setText(title);
        yearTV.setText(year);

        if(!isMovieWatched.equals("Watched")){
            watched.setVisibility(View.INVISIBLE);
        }
        else{
            watched.setVisibility(View.VISIBLE);
        }



        if(myRate==null){
            myRatting.setText("N/A");
        }
        else {
            myRatting.setText(myRate);
        }


        //////////////////////////////////////////////////////////////////////////////////
        if (cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_MANUAL))==1){


            imdbTV.setText("N/A");
        }
        else{
            imdbTV.setText(imdbRating);
        }
        ImageView logo = (ImageView)view.findViewById(R.id.logoIV);
        if(imageString==null){
            logo.setImageResource(R.drawable.noimage);
        }
        else {
            logo.setImageBitmap(StringToBitMap(imageString));
        }



    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }  }