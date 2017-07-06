package dd.com.myq.Util.PointsPackage;

import android.text.TextUtils;
import android.util.Log;

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

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    public static List<Points> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Points> news = extractFeatureFromJson(jsonResponse);
        return news;
    }

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
        if(url==null){

            return jsonResponse;
        }
        HttpURLConnection urlConnection =null;
        InputStream inputStream = null;
        try{

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);//in milliseonds
            urlConnection.setConnectTimeout(15000);//in millisec
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){

                inputStream = urlConnection.getInputStream();
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

    private static List<Points> extractFeatureFromJson(String newsJSON) {


        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<Points> newnews = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONArray newsArray = baseJsonResponse.getJSONArray("articles");
            for(int i=0;i<newsArray.length();i++){
                JSONObject currentNews = newsArray.getJSONObject(i);
                String wrong = currentNews.getString("WrongAnswers");
                String corr = currentNews.getString("CorrectAnswers");

                String poi = currentNews.getString("points");
                int WrongAnswers=Integer.parseInt("wrong");
                int CorrectAnswers=Integer.parseInt("corr");

                int points=Integer.parseInt(poi);
                Points news = new Points(WrongAnswers,CorrectAnswers,points);
                newnews.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the LASTOOONE earthquake JSON results", e);
        }

        return newnews;
    }

}
