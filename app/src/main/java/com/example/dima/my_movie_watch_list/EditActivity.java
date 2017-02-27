package com.example.dima.my_movie_watch_list;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.example.dima.my_movie_watch_list.DbConstants.EDIT_MODE;
import static com.example.dima.my_movie_watch_list.DbConstants.EDIT_MODE_FROM_SEARCH;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    ///////////////////////VARS///////////////
    SqlDatabase database = new SqlDatabase(this);
    Integer idSql;
    EditText subjet;
    EditText body;
    EditText url;
    Button save;
    Button cancel;
    Button show;
    ImageView logo;
    ProgressDialog progress;
    RatingBar ratingBar;
    Float myrate;
    /////////////////////////////////////////////////
    String year;
    String imdbRating;
    String imageString;
    ////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ////////////////VIEWS/////////////////////

        subjet = (EditText) findViewById(R.id.subjectET);
        body = (EditText) findViewById(R.id.bodyET);
        url = (EditText) findViewById(R.id.urlET);
        save = (Button) findViewById(R.id.saveBtn);
        cancel = (Button) findViewById(R.id.cancelBtn);
        show = (Button) findViewById(R.id.showBtn);
        ratingBar = (RatingBar)findViewById(R.id.myRating);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        show.setOnClickListener(this);
//////////////////////////////OnCreate Initialization ////////////////////////////////////////////////////

        if (EDIT_MODE) { // if the activity called from context menu or MainActivity listview
            Intent reciveDataFromItemClick = getIntent();
            idSql = reciveDataFromItemClick.getIntExtra(DbConstants.MOVIE_ID, -1); // sql id to update database
            subjet.setText(reciveDataFromItemClick.getStringExtra(DbConstants.MOVIE_SUBJECT));
            body.setText(reciveDataFromItemClick.getStringExtra(DbConstants.MOVIE_BODY));
            url.setText(reciveDataFromItemClick.getStringExtra(DbConstants.MOVIE_URL_IMAGE));
        } else if (EDIT_MODE_FROM_SEARCH) {  // if the activity called from searchDataonWeb Acticity
            downloadBodyThread thread1 = new downloadBodyThread();
            Intent reciveDataFromSearch = getIntent();
            String imdbID = reciveDataFromSearch.getStringExtra("ImdbID");
            thread1.execute("http://www.omdbapi.com/?i=" + imdbID); // get body
            subjet.setText(reciveDataFromSearch.getStringExtra(DbConstants.MOVIE_SUBJECT));
            url.setText(reciveDataFromSearch.getStringExtra(DbConstants.MOVIE_URL_IMAGE));
            DbConstants.EDIT_MODE_FROM_SEARCH = false;
        }
    }
    /////////////////////////////////////////////onClickListener for buttons////////////////////////////////////////
    @Override
    public void onClick(View v) {
        if  (v.getId() == R.id.myRatingTV){
            myrate = ratingBar.getRating();

        }

        if (v.getId() == R.id.saveBtn) {    /////save button on pressed
            ContentValues values = new ContentValues();
            values.put(DbConstants.MOVIE_SUBJECT, subjet.getText().toString());
            values.put(DbConstants.MOVIE_BODY, body.getText().toString());
            values.put(DbConstants.MOVIE_URL_IMAGE, url.getText().toString());
            values.put(DbConstants.MOVIE_IMG_STRING,imageString);
            values.put(DbConstants.MOVIE_MY_RATING,myrate.toString());

            if (EDIT_MODE) {
                database.getWritableDatabase().update(DbConstants.TABLE_NAME, values, "_id=?", new String[]{idSql.toString()});
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                EDIT_MODE = false;
            } else {
                values.put(DbConstants.MOVIE_YEAR, year);
                values.put(DbConstants.MOVIE_IMDB_RATING, imdbRating);
                database.getWritableDatabase().insert(DbConstants.TABLE_NAME, null, values);
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

            }
            finish();
        } else if (v.getId() == R.id.showBtn) {
            downloadPicture thread2 = new downloadPicture();
            thread2.execute(url.getText().toString());
        } else if (v.getId() == R.id.cancelBtn) {
            finish();

        }
    }

    /////////////////////////////////////movie details JSON download////////////////////////////////
    public class downloadBodyThread extends AsyncTask<String, Bitmap, String> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(EditActivity.this, "Searching..",
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
                    String plot = mainObject.getString("Plot");
                     year = mainObject.getString("Year");
                     imdbRating = mainObject.getString("imdbRating");
                    body.setText(plot);
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                body.setText("N/A");
                progress.dismiss();
            }
        }

    }
    //////////////////////////////////image download task////////////////////////////////////
    public class downloadPicture extends AsyncTask<String, Void, Bitmap> {

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

            logo = (ImageView) findViewById(R.id.imageView);
            logo.setImageBitmap(ImageResult);
            findViewById(R.id.activity_edit).setFocusable(true);
            findViewById(R.id.activity_edit).getDrawingCache(true);
            imageString = BitMapToString(ImageResult);

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
}



