package com.example.absolutelysaurabh.newsfeed;

/**
 * Created by absolutelysaurabh on 31/3/17.
 */

public class News {

    private String title;
    private String url;
    private String publishedAt;
    private String description;
    private String urlToImage;

    public News(String title, String description,String publishedAt,String url){

        this.publishedAt = publishedAt;
        this.url=url;
        this.description=description;
        this.title = title;

    }
    //We need to make the setters and getters
    public String getDescription(){
        return description;
    }

    public String getPublishedAt(){
        return publishedAt;
    }

    public String  getUrl(){
        return url;
    }

    public String getTitle(){
        return title;
    }

    public String getUrlToImage(){
        return urlToImage;
    }

}
