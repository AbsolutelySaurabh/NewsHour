package com.example.absolutelysaurabh.newsfeed.Json;

import android.text.TextUtils;
import android.util.Log;

import com.example.absolutelysaurabh.newsfeed.News.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by absolutelysaurabh on 31/3/17.
 */

/**
 * Helper methods related to requesting and receiving earthquake data from TOI.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }

    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<News> news = extractFeatureFromJson(jsonResponse);
        return news;
    }

    //This method will create url from the given string;
    private static URL  createUrl(String stringUrl){

        URL url = null;
        try{
            url = new URL(stringUrl);

        }catch(MalformedURLException e){

            Log.e(LOG_TAG,"Error with creating URL",e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url)throws IOException{

        String jsonResponse = "";

        //if the url is nukk then return here only
        if(url==null){

            return jsonResponse;
        }
        HttpURLConnection urlConnection =null;
        //THe INputStream is needed to receive data in chunk
        InputStream inputStream = null;
        try{

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);//in milliseonds
            urlConnection.setConnectTimeout(15000);//in millisec
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode()==200){

                inputStream = urlConnection.getInputStream();

                //Now we need to read from data from the inputStream we received
                jsonResponse = readFromStream(inputStream);
            }else{

                Log.e(LOG_TAG,"Error Response code: "+urlConnection.getResponseCode());

            }

        }catch(IOException e){
            Log.e(LOG_TAG,"Problem retrieving the news JSON results. ",e);

        }finally{

            if(urlConnection!=null){

                urlConnection.disconnect();;
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException{

        //We are using StringBuilder because it's mutable and can be change unlike
        //the normal String class in Java
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    private static List<News> extractFeatureFromJson(String newsJSON) {


        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> newnews = new ArrayList<>();


        try {

            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONArray newsArray = baseJsonResponse.getJSONArray("articles");

            for(int i=0;i<newsArray.length();i++){

                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the value for the key called "title"
                String title = currentNews.getString("title");

                // Extract the value for the key called "description"
                String description = currentNews.getString("description");

                String date = currentNews.getString("publishedAt");
                String url = currentNews.getString("urlToImage");

                News news = new News(title,description,date,url);
                newnews.add(news);

            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newnews;
    }

}
