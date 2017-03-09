package com.example.dima.simple_watch_library;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;

public class EditActivity extends AppCompatActivity {
    SqlDatabase sqlDatabase;
    ProgressDialog progressPicture;
    String imageString;
    Integer movieId;
    String movieTitleFromCursor;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ///////////////////////Views Initialization/////////////////////////
    EditText movieTitle;
    ImageView logo;
    EditText url;
    Button show;
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
    ImageButton isMovieWatchedIB;
    TextView isMovieWathcedTV;
    ImageButton cameraIB;
/////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        sqlDatabase = new SqlDatabase(this);
        movieTitle = (EditText) findViewById(R.id.MovieNameET);
        url = (EditText) findViewById(R.id.urlET);
        show = (Button) findViewById(R.id.showBtn);
        plot = (EditText) findViewById(R.id.moviePlotET);
        movieInformationTitle = (EditText) findViewById(R.id.movieInformationTitleET);
        year = (EditText) findViewById(R.id.movieYearET);
        rated = (EditText) findViewById(R.id.movieRatedET);
        released = (EditText) findViewById(R.id.movieReleasedET);
        runtime = (EditText) findViewById(R.id.movieRuntimeET);
        genre = (EditText) findViewById(R.id.MovieGenreET);
        director = (EditText) findViewById(R.id.movieDirectorET);
        save = (Button) findViewById(R.id.movieSaveBtn);
        logo = (ImageView) findViewById(R.id.moviePosterIV);
        save = (Button) findViewById(R.id.movieEditSaveBtn);
        cancel = (Button) findViewById(R.id.movieEditCancelBtn);
        myRatingBtn = (ImageButton) findViewById(R.id.myRatingIB);
        myRatingTV = (TextView) findViewById(R.id.myRatingTV);
        cameraIB = (ImageButton) findViewById(R.id.cameraIB);
        isMovieWatchedIB = (ImageButton) findViewById(R.id.isEditMovieWatchedIB);
        isMovieWathcedTV = (TextView) findViewById(R.id.isEditMovieWatchedTV);
        save.setText(getString(R.string.EditActivityAddBtn));
        myRatingTV.setText(getString(R.string.MovieMyRating));
        String isMovieWatchedStatus;

        if (DbConstants.EDIT_MODE) {
            Intent getInfoFromMainActivity = getIntent();
            movieTitleFromCursor= getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_SUBJECT);
            save.setText(R.string.EditActivityUpdateBtn);
            movieId = getInfoFromMainActivity.getIntExtra(DbConstants.MOVIE_ID, -1);
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
            isMovieWatchedStatus = getInfoFromMainActivity.getStringExtra(DbConstants.IS_MOVIE_WATCHED);
            imageString = getInfoFromMainActivity.getStringExtra(DbConstants.MOVIE_IMG_STRING);
            watchedStatusOnEditMode(isMovieWatchedStatus);

        }

        //////////////////////////////////////////////show image button //////////////////////////////////////////////


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               downloadPicture thread1 = new downloadPicture();
                if(Patterns.WEB_URL.matcher(url.getText()).matches()){   //url validation check

                    thread1.execute(url.getText().toString());

                }
                else
                {
                    Toast.makeText(EditActivity.this, "Url not valid", Toast.LENGTH_SHORT).show();
                }

            }
        });
        /////////////////////////////////////logo////////////////////////////////

        if (imageString == null) {
            logo.setImageResource(R.drawable.noimage);
        } else {
            logo.setImageBitmap(StringToBitMap(imageString));
        }


        ////////////////////////////////////////ratting bar /////////////////////////////////////////////////////////////////
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
                if (myRatingTV.getText().toString().equals("Set Rating")) {
                    ratingBar.setRating(0);
                } else {
                    Float rate = Float.parseFloat(myRatingTV.getText().toString());
                    ratingBar.setRating(rate);
                }

                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String rate = String.valueOf(ratingBar.getRating());

                        if (myRating != null && !myRating.equals(rate)) {
                            ContentValues values = new ContentValues();
                            values.put(DbConstants.MOVIE_MY_RATING, String.valueOf(ratingBar.getRating()));
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

        ///////////////////////////////////is Movie watched/////////////////////////////////////////////
        isMovieWatchedIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watched(isMovieWathcedTV.getText().toString());
            }
        });


        /////////////////////////////camera button ./////////////////////////////////////

        cameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureIntent();
            }
        });


        ///////////////////////////////////////save button click listener///////////////////////////////
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(DbConstants.MOVIE_SUBJECT, movieTitle.getText().toString());
                values.put(DbConstants.MOVIE_URL_IMAGE, url.getText().toString());
                values.put(DbConstants.MOVIE_BODY, plot.getText().toString());
                values.put(DbConstants.MOVIE_YEAR, year.getText().toString());
                values.put(DbConstants.MOVIE_RATED, rated.getText().toString());
                values.put(DbConstants.MOVIE_RELEASED, released.getText().toString());
                values.put(DbConstants.MOVIE_RUNTIME, runtime.getText().toString());
                values.put(DbConstants.MOVIE_GENRE, genre.getText().toString());
                values.put(DbConstants.MOVIE_DIRECTOR, director.getText().toString());
                values.put(DbConstants.MOVIE_MANUAL, 1);
                values.put(DbConstants.MOVIE_MY_RATING, myRatingTV.getText().toString());
                values.put(DbConstants.IS_MOVIE_WATCHED, isMovieWathcedTV.getText().toString());
                values.put(DbConstants.MOVIE_IMG_STRING, imageString);


                String title = movieTitle.getText().toString().trim();

                if (title.length()<=0) {
                    movieTitle.setBackgroundColor(Color.RED);
                    Toast.makeText(EditActivity.this, "Movie must have title", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (DbConstants.EDIT_MODE) {
                        sqlDatabase.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{movieId.toString()});
                        DbConstants.EDIT_MODE = false;
                        finish();
                    } else {
                        Cursor cursor = sqlDatabase.getReadableDatabase().query(DbConstants.TABLE_NAME, null, null, null, null, null, null);
                        CheckMovieExist exist = new CheckMovieExist(cursor, title);
                        if (!exist.check()){
                            sqlDatabase.getWritableDatabase().insert(DbConstants.TABLE_NAME, null, values);
                        sqlDatabase.close();
                            finish();
                    }
                        else{
                            Toast.makeText(EditActivity.this, "Movie Already Exist", Toast.LENGTH_SHORT).show();
                        }
                        }
                    }

                    }});

        ////////////////////////////////////////cancel button click listener
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbConstants.EDIT_MODE = false;
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DbConstants.EDIT_MODE = false;
        super.onBackPressed();
    }

    ////////////////////////////////watched option functions////////////////////////////
    public void watched(String status) {

        if (status.equals("Unwatched")) {
            isMovieWathcedTV.setText(R.string.WatchStatusTrue);
            isMovieWatchedIB.setBackgroundColor(getResources().getColor(R.color.Watched));
            Toast.makeText(EditActivity.this, movieTitle.getText().toString() +" set to Watched", Toast.LENGTH_SHORT).show();

        } else {
            isMovieWathcedTV.setText(R.string.WatchStatusFalse);
            isMovieWatchedIB.setBackgroundColor(getResources().getColor(R.color.unWatched));
            Toast.makeText(EditActivity.this, movieTitle.getText().toString() +" set to Unwatched", Toast.LENGTH_SHORT).show();

        }


    }

    public void watchedStatusOnEditMode(String isMovieWatchedStatus) {

        if (isMovieWatchedStatus.equals("Watched")) {
            isMovieWathcedTV.setText(R.string.WatchStatusTrue);
            isMovieWatchedIB.setBackgroundColor(getResources().getColor(R.color.Watched));
        } else {
            isMovieWathcedTV.setText(R.string.WatchStatusFalse);
            isMovieWatchedIB.setBackgroundColor(getResources().getColor(R.color.unWatched));
        }
    }

    //////////////////////////////////url image donwload task////////////////////////////
    public class downloadPicture extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            progressPicture = ProgressDialog.show(EditActivity.this, "Download Poster...",
                    "Please Wait", true, false);
        }

        protected Bitmap doInBackground(String... params) {

            Bitmap image = null;
            String urlImage = new String(params[0]);
            try {
                InputStream in = new java.net.URL((urlImage)).openStream();
                image = BitmapFactory.decodeStream(in);
                if(image.getByteCount()==0){
                    progressPicture.dismiss();
                }

            } catch (Exception ee) {
                ee.printStackTrace();

            }

            return image;
        }

        protected void onPostExecute(Bitmap ImageResult) {

            if (ImageResult != null) {
                logo.setImageBitmap(ImageResult);
                findViewById(R.id.activity_edit).setFocusable(true);
                findViewById(R.id.activity_edit).getDrawingCache(true);
                imageString = BitMapToString(ImageResult);
            } else {
                logo.setImageResource(R.drawable.noimage);
            }


            progressPicture.dismiss();
        }

    }


        ///////////////////////////////BitMap To String Converting//////////////////
        public String BitMapToString(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        }

        ///////////////////////////////String To Bitmap Converting//////////////////
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


        public void TakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                }
            }


        @Override

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
               logo.setImageBitmap(imageBitmap);
                imageString = BitMapToString(imageBitmap);
            }
        }
    }









