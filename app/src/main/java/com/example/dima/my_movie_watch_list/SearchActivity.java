package com.example.dima.my_movie_watch_list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
                String search = ((EditText) findViewById(R.id.searchET)).getText().toString();
                try {
                    String url = URLEncoder.encode(search, "UTF-8");
                    new downloadStringFromNetThread().execute("http://www.omdbapi.com/?s=" + url);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        ////////////////////////////////////////LIST CLICK MOVE  TO EDIT...Pass imdbID to WarchActivity///////////////////////////////////////////
        searchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionOfArrayList = position;
                String imdbID = allMoviesSearch.get(position).getImdbID();
                String urlPoster = allMoviesSearch.get(position).getUrl();
                Intent goToWatchScreen = new Intent(SearchActivity.this, WatchActivity.class);
                goToWatchScreen.putExtra(DbConstants.MOVIE_IMDB_ID, imdbID);
                goToWatchScreen.putExtra(DbConstants.MOVIE_URL_IMAGE, urlPoster);
                DbConstants.COMES_FROM_SEARCH_ACTIVITY=true;
                startActivity(goToWatchScreen);
                finish();
            }
        });


    }

    /////////////////////////////////////////////download async JSON search //////////////////////////////////////////
    public class downloadStringFromNetThread extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(SearchActivity.this, "Searching..",
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
                if(response==null){
                    Toast.makeText(SearchActivity.this, "No Movie Found", Toast.LENGTH_SHORT).show();
                }
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();

            }
            catch (java.net.SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }



        protected void onPostExecute(String JsonScriptString) {

            try {
                JSONObject mainObject = new JSONObject(JsonScriptString);

                JSONArray resultsArray = mainObject.getJSONArray("Search");

                allMoviesSearch.clear();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject currentObject = resultsArray.getJSONObject(i);
                    String title = currentObject.getString("Title");
                    String imdbID = currentObject.getString("imdbID");
                    String url = currentObject.getString("Poster");
                    MovieData movieData = new MovieData(title,url,imdbID);
                    allMoviesSearch.add(movieData);

                }

                adapterArrayList.notifyDataSetChanged();
                progress.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}








