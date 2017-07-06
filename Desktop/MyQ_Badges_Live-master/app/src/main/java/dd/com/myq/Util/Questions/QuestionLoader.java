package dd.com.myq.Util.Questions;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by absolutelysaurabh on 5/6/17.
 */

public class QuestionLoader extends android.support.v4.content.AsyncTaskLoader<List<Question>> {


    /** Tag for log messages */
    private static final String LOG_TAG = QuestionLoader.class.getName();

    /** Query URL */
    private String mUrl;

    public QuestionLoader(Context context, String url) {

        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        forceLoad();
    }

    @Override
    public List<Question> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of questions.
        List<Question> questions = QueryUtils.fetchData(mUrl);
        Log.e("no of records",""+questions.size());
        return questions;
    }
}
