package dd.com.myq.Util.Questions;

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

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    public static List<Question> fetchData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Question> questions = extractFeatureFromJson(jsonResponse);

        return questions;
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
        //THe InputStream is needed to receive data in chunk
        InputStream inputStream = null;
        try{

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if the Response Code was 200 that is seccessful then read inputstream and parse the response

            if(urlConnection.getResponseCode()==200){

                inputStream = urlConnection.getInputStream();

                //Now we need to read from data from the inputStream we received
                jsonResponse = readFromStream(inputStream);
                Log.e("Response",""+jsonResponse);
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

    private static List<Question> extractFeatureFromJson(String questionsJSON) {


        if (TextUtils.isEmpty(questionsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding questions to
        List<Question> questions = new ArrayList<>();

        try {

            JSONArray questionsArray = new JSONArray(questionsJSON);

            //for each question create a json object
            for(int i=0;i<questionsArray.length();i++){

                JSONObject currentNews = questionsArray.getJSONObject(i);

                String text = currentNews.getString("text");

                String level = currentNews.getString("level");

                String id = currentNews.getString("_id");

                String correctAnswer = currentNews.getString("correctAnswere");
               // String imageUrl = currentNews.getString("imageUrl");

                Question newquestion = new Question(text, level, correctAnswer, id);
                questions.add(newquestion);

            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the Questions JSON results", e);
        }

        // Return the list of questions
        return questions;
    }

}

