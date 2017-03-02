package com.example.dima.my_movie_watch_list;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static android.R.attr.name;

public class WatchActivity extends AppCompatActivity {
    SqlDatabase sqlDatabase;
    ProgressDialog progressDataMovie;
    ProgressDialog progressPicture;
    String imageString;
    String imdbid;
    String posterUrl;
    Cursor cursor;
    int idsql;
    ///////////////////////Views Initialization/////////////////////////
    TextView movieTitle;
    ImageView logo;
    TextView imdbRating;
    TextView plot;
    TextView movieInformationTitle;
    TextView year;
    TextView rated;
    TextView released;
    TextView runtime;
    TextView genre;
    TextView director;
    TextView myRatingTV;
    Button cancel;
    Button save;
    ImageButton myRatingImgBtn;
    Dialog rankDialog;
    RatingBar ratingBar;
    String myRating;
    ImageButton watchedIB;
    TextView watchedTV;
    String isMovieWatchedString;
/////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        sqlDatabase = new SqlDatabase(this);
        cursor=sqlDatabase.getReadableDatabase().query(DbConstants.TABLE_NAME,null,null,null,null,null,null);
        ///////////////Views Declaration/////////////////////

        movieTitle = (TextView) findViewById(R.id.movieNameTV);
        imdbRating = (TextView) findViewById(R.id.movieImdbRatingTV);
        myRatingTV = (TextView)findViewById(R.id.myMovieRatingTV);
        plot = (TextView) findViewById(R.id.moviePlotTV);
        movieInformationTitle = (TextView) findViewById(R.id.movieInformationTitleTV);
        year = (TextView) findViewById(R.id.movieYearTV);
        rated = (TextView) findViewById(R.id.movieRatedTV);
        released = (TextView) findViewById(R.id.movieReleasedTV);
        runtime = (TextView) findViewById(R.id.movieRuntimeTV);
        genre = (TextView) findViewById(R.id.movieGenreTV);
        director = (TextView) findViewById(R.id.movieDirectorTV);
        cancel = (Button) findViewById(R.id.movieCancelBtn);
        save = (Button) findViewById(R.id.movieSaveBtn);
        logo = (ImageView) findViewById(R.id.moviePosterIV);
        myRatingImgBtn = (ImageButton) findViewById(R.id.myRatingIB);
        watchedIB = (ImageButton)findViewById(R.id.isMovieWatchedIB);
        watchedTV =(TextView)findViewById(R.id.isMovieWatched);

        ///////////////////////////////////////////////////////////


        if (DbConstants.COMES_FROM_MAIN_ACTIVITY) {
         //   ratingBar.setRating(cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_MY_RATING)));
            Intent getInformationFromMainActivity = getIntent();
            idsql = getInformationFromMainActivity.getIntExtra(DbConstants.MOVIE_ID,-1);
            imdbid = getInformationFromMainActivity.getStringExtra(DbConstants.MOVIE_IMDB_ID);
            String imageUrl = getInformationFromMainActivity.getStringExtra(DbConstants.MOVIE_URL_IMAGE);
            myRating = getInformationFromMainActivity.getStringExtra(DbConstants.MOVIE_MY_RATING);
            isMovieWatchedString = getInformationFromMainActivity.getStringExtra((DbConstants.IS_MOVIE_WATCHED));

            new downloadMovieDetalis().execute("http://www.omdbapi.com/?i=" + imdbid);
            new downloadPicture().execute(imageUrl);
            //////////////////////////////////////////////////////////////////////////////////////////
            if(getInformationFromMainActivity.getStringExtra(DbConstants.MOVIE_MY_RATING)==null){
                myRatingTV.setText("Set Rating");
            }else{
               myRatingTV.setText(getInformationFromMainActivity.getStringExtra(DbConstants.MOVIE_MY_RATING));
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////

            watchedTV.setText(isMovieWatchedString);
            if( watchedTV.getText().toString().equals("Unwatched")) {
                watchedIB.setBackgroundColor(Color.DKGRAY);
            }else{
                watchedIB.setBackgroundColor(Color.GREEN);
            }






            DbConstants.COMES_FROM_MAIN_ACTIVITY = false;
            save.setVisibility(View.INVISIBLE);
        }

        if (DbConstants.COMES_FROM_SEARCH_ACTIVITY) {
            Intent getInformationFromSearchActivity = getIntent();
            myRatingTV.setText("Set Rating");
            imdbid = getInformationFromSearchActivity.getStringExtra(DbConstants.MOVIE_IMDB_ID);
            posterUrl = getInformationFromSearchActivity.getStringExtra(DbConstants.MOVIE_URL_IMAGE);
            new downloadMovieDetalis().execute("http://www.omdbapi.com/?i=" + imdbid);
            new downloadPicture().execute(posterUrl);
            DbConstants.COMES_FROM_SEARCH_ACTIVITY = false;
        }

//////////////////////////////////////////WATCHED///////////////////////////////////


        watchedIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( watchedTV.getText().toString().equals("Unwatched")) {
                    watchedTV.setText("Watched");
                    watchedIB.setBackgroundColor(Color.GREEN);
                    if(idsql!=-1){
                        ContentValues values = new ContentValues();
                        values.put(DbConstants.IS_MOVIE_WATCHED,watchedTV.getText().toString());
                        sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(idsql)});
                        Toast.makeText(WatchActivity.this, movieTitle.getText().toString() +" set to Watched", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    watchedTV.setText("Unwatched");
                    watchedIB.setBackgroundColor(Color.DKGRAY);
                    if(idsql!=-1){
                        ContentValues values = new ContentValues();
                        values.put(DbConstants.IS_MOVIE_WATCHED,watchedTV.getText().toString());
                        sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(idsql)});
                        Toast.makeText(WatchActivity.this, movieTitle.getText().toString() +" set to UnWatched", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });












        //////////////////MY RATING///////////////////////////////////////

        myRatingImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rankDialog = new Dialog(WatchActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);


                  //  String rateString = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_MY_RATING));
                 //   ratingBar.setRating(Float.parseFloat(rateString));


                TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
                text.setText(movieTitle.getText().toString());

                if(myRatingTV.getText().toString().equals("Set Rating")){
                    ratingBar.setRating(0);
                }
                else{
                    Float rate = Float.parseFloat(myRatingTV.getText().toString());
                    ratingBar.setRating(rate);
                }

                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String rate = String.valueOf(ratingBar.getRating());

                        if(myRating!=null && !myRating.equals(rate)) {
                            ContentValues values = new ContentValues();
                            values.put(DbConstants.MOVIE_MY_RATING,String.valueOf(ratingBar.getRating()));
                            sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(idsql)});
                            Toast.makeText(WatchActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                        myRatingTV.setText(rate);
                        rankDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                rankDialog.show();
            }
        });


        //////////////SAVE BUTTON //////////////

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent backtomain = new Intent(WatchActivity.this, MainActivity.class);
                ContentValues values = new ContentValues();
                values.put(DbConstants.MOVIE_SUBJECT, movieTitle.getText().toString());
                values.put(DbConstants.MOVIE_IMDB_RATING, imdbRating.getText().toString());
                values.put(DbConstants.MOVIE_YEAR, year.getText().toString());
                values.put(DbConstants.MOVIE_IMG_STRING, imageString);
                values.put(DbConstants.MOVIE_IMDB_ID, imdbid);
                values.put(DbConstants.MOVIE_URL_IMAGE, posterUrl);
                values.put(DbConstants.MOVIE_MY_RATING,myRatingTV.getText().toString());
                values.put(DbConstants.IS_MOVIE_WATCHED,watchedTV.getText().toString());
                sqlDatabase.getWritableDatabase().insert(DbConstants.TABLE_NAME, null, values);
                startActivity(backtomain);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /////////////////////////////////////movie details JSON download////////////////////////////////
    public class downloadMovieDetalis extends AsyncTask<String, Bitmap, String> {

        @Override
        protected void onPreExecute() {
            progressDataMovie = ProgressDialog.show(WatchActivity.this, "Download Movie Data..",
                    "Please Wait", true, false);
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            StringBuilder response = null;

            try {
                URL website = new URL(params[0]);
                URLConnection connection = website.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));
                response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return response.toString();
        }

        protected void onPostExecute(String JsonScriptString) {
            if (JsonScriptString != null) {
                try {
                    JSONObject mainObject = new JSONObject(JsonScriptString);

                    movieTitle.setText(mainObject.getString("Title"));
                    imdbRating.setText(mainObject.getString("imdbRating"));
                    plot.setText(mainObject.getString("Plot"));
                    movieInformationTitle.setText(mainObject.getString("Title"));
                    year.setText(mainObject.getString("Year"));
                    rated.setText(mainObject.getString("Rated"));
                    released.setText(mainObject.getString("Released"));
                    runtime.setText(mainObject.getString("Runtime"));
                    genre.setText(mainObject.getString("Genre"));
                    director.setText(mainObject.getString("Director"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(WatchActivity.this, "Server Not Responding ", Toast.LENGTH_SHORT).show();
            }
            progressDataMovie.dismiss();
        }

    }

    //////////////////////////////////image download task////////////////////////////////////
    public class downloadPicture extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            progressPicture = ProgressDialog.show(WatchActivity.this, "Download Poster..",
                    "Please Wait", true, false);
        }

        protected Bitmap doInBackground(String... params) {

            Bitmap image = null;
            String urlImage = new String(params[0]);
            try {
                InputStream in = new java.net.URL((urlImage)).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap ImageResult) {

            if (ImageResult != null) {
                logo.setImageBitmap(ImageResult);
            } else {
                logo.setImageResource(R.drawable.noimage);
            }

            findViewById(R.id.activity_watch).setFocusable(true);
            findViewById(R.id.activity_watch).getDrawingCache(true);
            imageString = BitMapToString(ImageResult);
            progressPicture.dismiss();
        }

    }

    ///////////////////////////////BitMap To String Converting//////////////////
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
    }


}
