package com.example.dima.my_movie_watch_list;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditActivity1 extends AppCompatActivity {
    SqlDatabase sqlDatabase;
    ProgressDialog progressDataMovie;
    ProgressDialog progressPicture;
    String imageString;
    String imdbid;
    String posterUrl;
    Integer movieId;
///////////////////////Views Initialization/////////////////////////
    EditText movieTitle ;
    ImageView logo;
    EditText url;
    EditText plot;
    EditText movieInformationTitle;
    EditText year;
    EditText rated;
    EditText released;
    EditText runtime;
    EditText genre;
    EditText director;
    Button cancel;
    Button save;
/////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit1);



        sqlDatabase = new SqlDatabase(this);



        movieTitle = (EditText)findViewById(R.id.movieNameET);
        url = (EditText) findViewById(R.id.urlET);
        plot = (EditText) findViewById( R.id.moviePlotET);
        movieInformationTitle = (EditText)findViewById(R.id.movieInformationTitleET);
        year = (EditText)findViewById(R.id.movieYearET);
        rated=(EditText)findViewById(R.id.movieRatedET);
        released = (EditText) findViewById(R.id.movieReleasedET);
        runtime = (EditText)findViewById(R.id.movieRuntimeET);
        genre = (EditText) findViewById(R.id.movieGenreET);
        director= (EditText)findViewById(R.id.movieDirectorET);
        save = (Button)findViewById(R.id.movieSaveBtn);
        logo = (ImageView)findViewById(R.id.moviePosterIV);
        save = (Button)findViewById(R.id.movieEditSaveBtn);
        cancel = (Button)findViewById(R.id.movieEditCancelBtn);
        save.setText("Add");

        if (DbConstants.EDIT_MODE){
            Intent getInfoFromMainActivity = getIntent();
            save.setText("Update");
            movieId = getInfoFromMainActivity.getIntExtra(DbConstants.MOVIE_ID,-1);
            movieTitle.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_SUBJECT));
            url.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_URL_IMAGE));
            plot.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_BODY));
            movieInformationTitle.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_SUBJECT));
            year.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_YEAR));
            rated.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_RATED));
            released.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_RELEASED));
            runtime.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_RUNTIME));
            genre.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_GENRE));
            director.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_DIRECTOR));
        }












    save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put(DbConstants.MOVIE_SUBJECT,movieTitle.getText().toString());
            values.put(DbConstants.MOVIE_URL_IMAGE,url.getText().toString());
            values.put(DbConstants.MOVIE_BODY,plot.getText().toString());
            values.put(DbConstants.MOVIE_YEAR,year.getText().toString());
            values.put(DbConstants.MOVIE_RATED,rated.getText().toString());
            values.put(DbConstants.MOVIE_RELEASED,released.getText().toString());
            values.put(DbConstants.MOVIE_RUNTIME,runtime.getText().toString());
            values.put(DbConstants.MOVIE_GENRE,genre.getText().toString());
            values.put(DbConstants.MOVIE_DIRECTOR,director.getText().toString());
            values.put(DbConstants.MOVIE_MANUAL,1 );

            if(DbConstants.EDIT_MODE){


                sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{movieId.toString()});
                DbConstants.EDIT_MODE=false;
            }
            else {
                sqlDatabase.getWritableDatabase().insert(DbConstants.TABLE_NAME, null, values);
                sqlDatabase.close();
            }
            finish();
        }
    });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbConstants.EDIT_MODE=false;
                finish();
            }
        });


    }


    @Override
    public void onBackPressed() {
        DbConstants.EDIT_MODE=false;
        super.onBackPressed();
    }
}
