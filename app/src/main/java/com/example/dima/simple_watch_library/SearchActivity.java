package com.example.dima.simple_watch_library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<MovieData> allMoviesSearch;
    ArrayAdapter<MovieData> adapterArrayList;
    ListView searchResultLV;
    int positionOfArrayList;
    ProgressDialog progress;
    String search;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /////////////////////////////////////////ListView Initialization/////////////////////////////
        searchResultLV = (ListView) findViewById(R.id.searchMovieLV);
        allMoviesSearch = new ArrayList<>();
        adapterArrayList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allMoviesSearch);
        searchResultLV.setAdapter(adapterArrayList);

/////////////////////////////////////////////ON CLICK GO BUTTON//////////////////////////////////////////
        findViewById(R.id.goBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 search = ((EditText) findViewById(R.id.searchET)).getText().toString();
                if(search.equals(""))
                {
                    Toast.makeText(SearchActivity.this, "Type something , search bar is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        String url = URLEncoder.encode(search, "UTF-8");
                        new downloadStringFromNetThread().execute("http://www.omdbapi.com/?s=" + url);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

/////////////////////////////////////////////////////////////cancel Button////////////////////////////////////////////

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });







        ////////////////////////////////////////LIST CLICK MOVE  TO EDIT...Pass imdbID to WarchActivity///////////////////////////////////////////
        searchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionOfArrayList = position;
                String title = allMoviesSearch.get(position).getSubject();
                SqlDatabase data = new SqlDatabase(SearchActivity.this);
                Cursor cursor = data.getReadableDatabase().query(DbConstants.TABLE_NAME,null,null,null,null,null,null);
                CheckMovieExist checkMovieExist = new CheckMovieExist(cursor,title);
                if(checkMovieExist.check()==true){
                    Toast.makeText(SearchActivity.this, "Movie already exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    String imdbID = allMoviesSearch.get(position).getImdbID();
                    String urlPoster = allMoviesSearch.get(position).getUrl();
                    Intent goToWatchScreen = new Intent(SearchActivity.this, WatchActivity.class);
                    goToWatchScreen.putExtra(DbConstants.MOVIE_IMDB_ID, imdbID);
                    goToWatchScreen.putExtra(DbConstants.MOVIE_URL_IMAGE, urlPoster);
                    DbConstants.COMES_FROM_SEARCH_ACTIVITY=true;
                    startActivity(goToWatchScreen);
                    finish();
                }
            }
        });


    }

    /////////////////////////////////////////////download async JSON search //////////////////////////////////////////
    public class downloadStringFromNetThread extends AsyncTask<String, String, String> {
Boolean isConnected = true;
        @Override
        protected void onPreExecute() {

           // progress = ProgressDialog.show(SearchActivity.this, "Searching..",
             //       "Please Wait", true, false);

          progress = new ProgressDialog (SearchActivity.this);
            progress.setTitle("Loading....");
            progress.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            StringBuilder response = null;
            if (!new CheckConnection(SearchActivity.this).isNetworkAvailable()) {
                isConnected = false;
            }
            if (isConnected) {


                try {
                    URL website = new URL(params[0]);
                    URLConnection connection = website.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));
                    response = new StringBuilder();


                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                if (response == null) {
                    response.append("No connection");
                }

            return response.toString();
        }


        protected void onPostExecute(String JsonScriptString) {

            if (JsonScriptString.equals("No connection") | JsonScriptString.contains("Movie not found")){
                if(isConnected)
                    Toast.makeText(SearchActivity.this, search + " Doesn't exist , try something else ", Toast.LENGTH_LONG).show();
                else
                Toast.makeText(SearchActivity.this, "Not Connection", Toast.LENGTH_LONG).show();
            }

            else{
            try {
                JSONObject mainObject = new JSONObject(JsonScriptString);

                JSONArray resultsArray = mainObject.getJSONArray("Search");

                allMoviesSearch.clear();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject currentObject = resultsArray.getJSONObject(i);
                    String title = currentObject.getString("Title");
                    String imdbID = currentObject.getString("imdbID");
                    String url = currentObject.getString("Poster");
                    MovieData movieData = new MovieData(title, url, imdbID);
                    allMoviesSearch.add(movieData);

                }

                adapterArrayList.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
            progress.dismiss();

        }
    }
}







