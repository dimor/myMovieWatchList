package com.example.dima.my_movie_watch_list;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
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
    ImageButton myRatingBtn;
    Dialog rankDialog;
    TextView myRatingTV;
    RatingBar ratingBar;
    String myRating;
/////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);



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
        myRatingBtn = (ImageButton)findViewById(R.id.myRatingIB) ;
        myRatingTV = (TextView)findViewById(R.id.myRatingTV);
        save.setText("Add");
        myRatingTV.setText("Set Rating");

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
            myRatingTV.setText(getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_MY_RATING));
            myRating = getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_MY_RATING);

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
            values.put(DbConstants.MOVIE_MY_RATING,myRatingTV.getText().toString());

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


        myRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankDialog = new Dialog(EditActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);


                //  String rateString = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_MY_RATING));
                //   ratingBar.setRating(Float.parseFloat(rateString));


                TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
                text.setText(movieTitle.getText().toString());
                //if user not rated movie
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
                            sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(movieId)});
                            Toast.makeText(EditActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        }


                        myRatingTV.setText(rate);
                        rankDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                rankDialog.show();
            }
        });
            }


    @Override
    public void onBackPressed() {
        DbConstants.EDIT_MODE=false;
        super.onBackPressed();
    }
}
