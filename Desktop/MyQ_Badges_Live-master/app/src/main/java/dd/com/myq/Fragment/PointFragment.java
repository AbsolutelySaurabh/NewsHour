package dd.com.myq.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

public class PointFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView points;
    private TextView correct;
    private TextView incorrect;

    public static int Wrong  ;
    public static int Correct ;
    public static int Total_points ;

    View view;

    String POINTS_REQUEST_URL="http://myish.com:10011/api/users/";

    private OnFragmentInteractionListener mListener;

    public PointFragment() {

        // Required empty public constructor
    }

    public static PointFragment newInstance(String param1, String param2) {
        PointFragment fragment = new PointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_point, container, false);

        SessionManager currentSession = new SessionManager(getActivity());
        HashMap<String, String> user_details = currentSession.getUserDetails();

        String id = user_details.get(SessionManager.KEY_UID);

        POINTS_REQUEST_URL = POINTS_REQUEST_URL + id;

        Log.e(" user id : "+id,"    .........");


//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.blue, R.color.green);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        points=(TextView)view.findViewById(R.id.points);
        correct = (TextView) view.findViewById(R.id.correct_points);
        incorrect = (TextView) view.findViewById(R.id.incorrect_points);

        TextView date=(TextView)view.findViewById(R.id.point_date);

//        TextView tip = (TextView) view.findViewById(R.id.tip_text);
//        tip.setText(key);

        date.setText("Points acquired till "+formattedDate);

        getPoints(view);

//        mSwipeRefreshLayout.setRefreshing(false);

        Log.e("Points stored: ", Correct + " "+ Wrong);

        return view;
    }

    public  void plotGraph(String ac, String inc, View view){

        float active_clients = Float.parseFloat(ac);
        float inactive_clients = Float.parseFloat(inc);

        float total = active_clients+inactive_clients;

        if(total==0){
            total=100;
        }
        else total=active_clients+inactive_clients;

        DecoView decoView = (DecoView) view.findViewById(R.id.chart);

        decoView.addSeries(new SeriesItem.Builder(Color.rgb(161, 2, 81))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(39f)
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.rgb( 255, 153, 0))
                .setRange(0, total, 0)
                .setLineWidth(29f)
                .setSpinDuration(1500)
                .build();

//        SeriesItem seriesItem1 = new SeriesItem.Builder()
//                .setRange(0, 100, 0)
//                .setSeriesLabel(new SeriesLabel.Builder("Percent %.0f%%")
//                        .setColorBack(Color.argb(218, 0, 0, 0))
//                        .setColorText(Color.argb(255, 255, 255, 255))
//                        .build())
//                .build();

//        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
//                .setRange(0, 50, 0)
//                .build();
//
//        int series1Index = decoView.addSeries(seriesItem);

        int series1Index = decoView.addSeries(seriesItem1);

        decoView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        decoView.addEvent(new DecoEvent.Builder(active_clients).setIndex(series1Index).setDelay(4000).build());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        TextView v = (TextView) view.findViewById(R.id.points);
//        v.setText(total_points);

    }


    public void getPoints(final View view){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getActivity(), POINTS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("Response Shushant GET: ", response.toString());

                try {

                    Wrong = response.getInt("WrongAnswers");

                    Correct = response.getInt("CorrectAnswers");

                    Total_points = response.getInt("points");

                    Log.e("Points stored before: ", Correct + " "+ Wrong + " "+ Total_points);

                    points.setText(String.valueOf(Total_points));
                    correct.setText(String.valueOf(Correct));
                    incorrect.setText(String.valueOf(Wrong));

                    plotGraph(String.valueOf(Correct), String.valueOf(Wrong), view);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnFragmentInteractionListener) {
            mListener = (PointFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}