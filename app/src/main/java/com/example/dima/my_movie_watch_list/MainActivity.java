package com.example.dima.my_movie_watch_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.dima.my_movie_watch_list.DbConstants.COMES_FROM_MAIN_ACTIVITY;
import static com.example.dima.my_movie_watch_list.DbConstants.EDIT_MODE;

public class MainActivity extends AppCompatActivity {

    /////////////////////VARS/////////////////////////
    Cursor cursor;
    SimpleCursorAdapter adapter;
    SqlDatabase database;
    ListView mainLV;
    int listViewPositionFromContextMenu;
    int listViewIdSQLFromContextMenu;
    int idFromSQL;
    MovieListAdapter movieListAdapter;
    //#######################MENUS########################################################################################
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        listViewIdSQLFromContextMenu = (int) ((AdapterView.AdapterContextMenuInfo) menuInfo).id;
        listViewPositionFromContextMenu = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_edit) {

            cursor.moveToPosition(listViewPositionFromContextMenu);
            String subject = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_SUBJECT));
            String body = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_BODY));
            String url = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_URL_IMAGE));
            idFromSQL = cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_ID));
            Intent moveDatatoEdit = new Intent(MainActivity.this, EditActivity.class);
            moveDatatoEdit.putExtra(DbConstants.MOVIE_SUBJECT, subject);
            moveDatatoEdit.putExtra(DbConstants.MOVIE_BODY, body);
            moveDatatoEdit.putExtra(DbConstants.MOVIE_URL_IMAGE, url);
            moveDatatoEdit.putExtra(DbConstants.MOVIE_ID, idFromSQL);
            EDIT_MODE = true;
            startActivity(moveDatatoEdit);
        }


        if (item.getItemId() == R.id.context_menu_remove) {
            database.getWritableDatabase().delete(DbConstants.TABLE_NAME, "_id=?", new String[]{""+listViewIdSQLFromContextMenu});
            Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
            refreshListView();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuClearItemBtn) {
            database.getWritableDatabase().delete(DbConstants.TABLE_NAME, null, null);
            refreshListView();
            Toast.makeText(this, "List Cleared", Toast.LENGTH_SHORT).show();
        }


        if (item.getItemId() == R.id.menuExit) {

            Intent movetowatch = new Intent(this,EditActivity1.class);
            startActivity(movetowatch);
            finish();
        }
        return true;
    }

    public void buildDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Select");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Manual",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent goToEdit = new Intent(MainActivity.this, EditActivity1.class);
                        startActivity(goToEdit);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Search Online",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent searchDataonWeb = new Intent(MainActivity.this, searchDataOnWeb.class);
                        startActivity(searchDataonWeb);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    //#####################################################ON CREATE#########################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////////////////////////////////////////////onCreate Initialization //////////////////////////////////////////////////////


        database = new SqlDatabase(this);
        mainLV = (ListView) findViewById(R.id.listMovieTV);
        registerForContextMenu(mainLV);
        cursor = database.getReadableDatabase().query(DbConstants.TABLE_NAME, null, null, null, null, null, null);
        movieListAdapter = new MovieListAdapter(this,cursor);
        mainLV.setAdapter(movieListAdapter);


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        findViewById(R.id.addEditAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // build dialig menu in pluss buttn pressed
                buildDialog();
            }
        });

      ////////////////////////////////////////////// list view listener goes to watchActivity
        mainLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


               cursor.moveToPosition(position); // listview position

                if((cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_MANUAL))==1)){
                    Intent editModeEditActivity = new Intent(MainActivity.this,EditActivity1.class);
                    editModeEditActivity.putExtra(DbConstants.MOVIE_SUBJECT,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_SUBJECT)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_BODY,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_BODY)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_URL_IMAGE,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_URL_IMAGE)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_YEAR,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_YEAR)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_RATED,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_RATED)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_RELEASED,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_RELEASED)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_RUNTIME,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_RUNTIME)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_GENRE,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_GENRE)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_DIRECTOR,cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_DIRECTOR)));
                    editModeEditActivity.putExtra(DbConstants.MOVIE_ID,cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_ID)));
                    DbConstants.EDIT_MODE=true;
                    startActivity(editModeEditActivity);

                }else {
                    String imdb_id = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMDB_ID));
                    String imageUrl = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_URL_IMAGE));
                    Intent moveToWatchActivity = new Intent(MainActivity.this, WatchActivity.class);
                    moveToWatchActivity.putExtra(DbConstants.MOVIE_IMDB_ID, imdb_id);
                    moveToWatchActivity.putExtra(DbConstants.MOVIE_URL_IMAGE, imageUrl);
                    COMES_FROM_MAIN_ACTIVITY = true;
                    startActivity(moveToWatchActivity);
                }
            }
        });
    }


    public void refreshListView() {
        cursor = database.getReadableDatabase().query(DbConstants.TABLE_NAME, null, null, null, null, null, null);
        movieListAdapter.swapCursor(cursor);
    }

    @Override
    protected void onResume() {
        refreshListView();
        Toast.makeText(this, "List Refreshed", Toast.LENGTH_SHORT).show();
        super.onResume();
    }


}


