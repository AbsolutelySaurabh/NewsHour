package com.example.absolutelysaurabh.newshour.Model;

/**
 * Created by absolutelysaurabh on 22/8/17.
 */

public class News {

    public String title;
    public String description;
    public String publishedAt;
    public String url;
    public String urlToImage;

    public News(String title, String description, String publishedAt, String url, String urlToImage){

        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.url = url;
        this.urlToImage = urlToImage;
    }

    public String getTitle(){

        return title;
    }
    public String getDescription(){

        return description;
    }
    public String getPublishedAt(){

        return publishedAt;
    }
    public String getUrl(){

        return url;
    }
    public String getUrlToImage(){

        return urlToImage;
    }

}
