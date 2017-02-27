package com.example.dima.my_movie_watch_list;

/**
 * Created by Dima on 2/7/2017.
 */

public class MovieData {

    private int id;
    private String subject;
    private String body;
    private String url;
    private String imdbID;


    ///////////////////////////////////////////////////////////Constractors/////////////////////////////////////////
    public MovieData() {
    }
    public MovieData(String title , String imdbID) {
        this.subject=title;
        this.imdbID = imdbID;
    }
    public MovieData(String subject, String url, String imdbID) {
        this.subject = subject;
        this.url = url;
        this.imdbID = imdbID;
    }
////////////////////////////////////////////////////////Getters////////////////////////////////////////////////
    public int getId() {
        return id;
    }
    public String getSubject() {
        return subject;
    }
    public String getUrl() {
        return url;
    }
    public String getImdbID() {
        return imdbID;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return getSubject();
    }
}
