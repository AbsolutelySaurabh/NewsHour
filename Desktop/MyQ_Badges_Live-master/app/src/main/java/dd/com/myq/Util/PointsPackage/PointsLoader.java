package dd.com.myq.Util.PointsPackage;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class PointsLoader extends AsyncTaskLoader<List<Points>> {

    private static final String LOG_TAG = PointsLoader.class.getName();
    private String mUrl;


    public PointsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        forceLoad();
    }

    @Override
    public List<Points> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Points> poi = QueryUtils.fetchNewsData(mUrl);
        return poi;
    }
}
