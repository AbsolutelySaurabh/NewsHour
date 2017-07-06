package dd.com.myq.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import dd.com.myq.App.Config;
import dd.com.myq.R;
import dd.com.myq.Util.Questions.Question;
import dd.com.myq.Util.Questions.QuestionAdapter;
import dd.com.myq.Util.SessionManager;

public class HomeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "flag";
    private static final String ARG_PARAM2 = "levels";
    private static final String ARG_PARAM3 = "categories";

    private  ArrayList<String> al = new ArrayList<String>();
    private ArrayList<String> al_id = new ArrayList<String>();
    private ArrayList<String> al_correctAns = new ArrayList<String>();

    private static int index;
    private  static int flag=0;
    private static int another_flag;

    int j = 0;

    ArrayAdapter<String> arrayAdapter;

    int report_flag  =1;

    private int i;

    List<Question> questions;

    public  SwipeFlingAdapterView flingContainer;

    private LoaderManager.LoaderCallbacks<List<Question>> mCallbacks;

    private  LayoutInflater inflater=null;

    Context mContext;

    public  QuestionAdapter questionAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;


    SessionManager currentSession;

    private static final int QUESTION_LOADER_ID = 1;

    private TextView emptyTextView;

    private static final String LOG_TAG = HomeFragment.class.getName();

    private String QUESTIONS_REQUEST_URL = "http://myish.com:10011/api/questions/";

    private String REPORT_REQUEST_URL = "http://myish.com:10011/api/questions/report";
    private String ANOTHER_QUESTION_URL;

    private TextView mEmptyStateTextView;

    private OnFragmentInteractionListener mListener;

    View view;

    public HomeFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2, String param3) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
        currentSession = new SessionManager(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        HashMap<String, String> user_details = currentSession.getUserDetails();

        final String user_id = user_details.get(SessionManager.KEY_UID);
        QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;

        //////////////////////////////////////rishabh
        another_flag = 0;

        report_flag = 1;

        if(flag==0&&mParam2==null&&mParam3==null) {

            getQuestions(QUESTIONS_REQUEST_URL);
            Log.e("First call flag = 0 ", "FIRST");

        }
        else  if (mParam2 != null && !mParam2.isEmpty())
        {

            Log.e("second call flag = 0 ", "SECOND");

            Log.e("mParam2 : ", mParam2);
            String qapi = "http://myish.com:10011/api/questions_levels?";
            if (mParam2.equals("E")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=E";
            }
            else if (mParam2.equals("M")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=M";
            }
            else if (mParam2.equals("H")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=H";
            }
            else {
                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
            }
            mParam2=null;

            if (flag == 0)
            {
                getQuestions(QUESTIONS_REQUEST_URL);
            }
        }

        else  if (mParam3 != null && !mParam3.isEmpty())
        {

            Log.e("send2222nd call flag=0 ", "SECOND");

            Log.e("mParam3 : ", mParam3);

            String qapi = "http://myish.com:10011/api/questions_category?";
            if (mParam3.equals("PHP")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=PHP";
            }
            else if (mParam3.equals("C")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C";
            }
            else if (mParam3.equals("HADOOP")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=HADOOP";
            }
//            else if (mParam3.equals("AUTOMATION SYSTEM")) {
//                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=AUTOMATION%20SYSTEM";
//            }
            else if (mParam3.equals("CLOUD COMPUTING")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=CLOUD%20COMPUTING";
            }
            else if (mParam3.equals("ROBOTICS AND AI")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=ROBOTICS%20AND%20AI";
            }

            else if (mParam3.equals("CYBER SECURITY")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=%20CYBER%20SECURITY";
            }
            else if (mParam3.equals("C++")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C%2B%2B";
            }

            else {
                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
            }
            mParam3=null;

            if (flag == 0)
            {
                getQuestions(QUESTIONS_REQUEST_URL);

            }
        }


        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item, R.id.helloText, al);
        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");

                Log.e("FirstObject question :", al.get(index));

//                al.remove(0);
//                al_id.remove(0);
//                al_correctAns.remove(0);
//
//                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                Log.e("Exit question :", al.get(index));

                if (report_flag == 1) {

                    String correctness;

                    if (al_correctAns.get(index).equals("NO")) {

                        correctness = "Correct";

                        final Toast toast = Toast.makeText(getActivity(),
                                correctness, Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 500);

                    } else {
                        correctness = "incorrect";

                        final Toast toast = Toast.makeText(getActivity(),
                                correctness, Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 500);
                    }

                    Log.d("getTop() : ", String.valueOf(flingContainer.getTop()));
                    Log.d(" al_id : ", al_id.get(index));
                    Log.d(" Questions : ", al.get(index));
                    Log.d(" correctA : ", al_correctAns.get(index));

                    AddQuestion(user_id, al_id.get(index), al.get(index), "", al_correctAns.get(index), "2", correctness);
                    AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));

                    al.remove(0);
                    al_id.remove(0);
                    al_correctAns.remove(0);

                    arrayAdapter.notifyDataSetChanged();

                    Log.d("al in LeftCard: ", String.valueOf(al.size()));

                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Log.e("Exit question :", al.get(index));

                String correctness;

                if (al_correctAns.get(index).equals("YES")) {

                    correctness = "Correct";

                    final Toast toast = Toast.makeText(getActivity(),
                            correctness, Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 500);

                } else {

                    correctness = "incorrect";

                    final Toast toast = Toast.makeText(getActivity(),
                            correctness, Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 500);
                }

                Log.d(" al_id : ", al_id.get(index));

                Log.d(" Questions : ", al.get(index));

                Log.d(" correctA : ", al_correctAns.get(index));

                AddQuestion(user_id, al_id.get(index), al.get(index), "", al_correctAns.get(index), "2", correctness);
                AddUserToQuestion(user_id, al_id.get(index), al_correctAns.get(index));


                al.remove(0);
                al_id.remove(0);
                al_correctAns.remove(0);
                arrayAdapter.notifyDataSetChanged();

                Log.d("al in Right Card : ", String.valueOf(al.size()));

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                Log.d("al EMpty Adapter 1 : ", String.valueOf(al.size()));

                TextView report = (TextView) view.findViewById(R.id.report_question);
                report.setEnabled(false);

                Button button_left = (Button) view.findViewById(R.id.left);
                button_left.setBackgroundResource(R.drawable.false_btn);
                button_left.setEnabled(false);

                Button button_right = (Button) view.findViewById(R.id.right);
                button_right.setBackgroundResource(R.drawable.true_btn);
                button_right.setEnabled(false);

//                Log.e("al ZERO AboutEMPTY: ", al.get(0));
//                if(al.get(1)!=null){
//
//                    Log.e("aln ONE AboutEMPTY: ", al.get(1));
//
//                }

                if(flag==10&&mParam2==null&&mParam3==null)
                {
                    Log.e("second 111st0", "1");
                    flingContainer.setAdapter(null);

                    getQuestions(QUESTIONS_REQUEST_URL);
                    flag=0;

                }
                else
                {
                    if(flag==10)
                    {
                        if (mParam3 != null && !mParam3.isEmpty()&&mParam2 == null && mParam2.isEmpty())
                        {

                            Log.e("EMPTYY mParam3 : ", mParam3);

                            String qapi = "http://myish.com:10011/api/questions_category?";
                            if (mParam3.equals("PHP")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=PHP";
                            }
                            else if (mParam3.equals("C")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C";
                            }

                            else if (mParam3.equals("HADOOP")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=HADOOP";
                            }
//                            else if (mParam3.equals("AUTOMATION SYSTEM")) {
//                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=AUTOMATION%20SYSTEM";
//                            }
                            else if (mParam3.equals("CLOUD COMPUTING")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=CLOUD%20COMPUTING";
                            }
                            else if (mParam3.equals("ROBOTICS AND AI")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=ROBOTICS%20AND%20AI";
                            }
                            else if (mParam3.equals("CYBER SECURITY")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=%20CYBER%20SECURITY";
                            }
                            else if (mParam3.equals("C++")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C%2B%2B";
                            }

                            else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                        }
                        else if (mParam3 == null && mParam3.isEmpty()&&mParam2 != null && !mParam2.isEmpty())
                        {

                            Log.e("EMPTYY  mParam2 : ", mParam2);

                            String qapi = "http://myish.com:10011/api/questions_levels?";
                            if (mParam2.equals("E")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=E";
                            }
                            else if (mParam2.equals("M")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=M";
                            }
                            else if (mParam2.equals("H")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=H";
                            }
                            else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                        }


                        flingContainer.setAdapter(null);

                        getQuestions(QUESTIONS_REQUEST_URL);


                    }

                }
                Log.d("al EMpty Adapter 2 : ", String.valueOf(al.size()));
            }
            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(getA "Clicked!");
            }
        });

        Button button_left = (Button) view.findViewById(R.id.left);
        button_left.setBackgroundResource(R.drawable.false_btn);

        button_left.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                flingContainer.getTopCardListener().selectLeft();

            }
        });

        Button button_right = (Button) view.findViewById(R.id.right);

        button_right.setBackgroundResource(R.drawable.true_btn);

        button_right.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                flingContainer.getTopCardListener().selectRight();
            }
        });

        TextView report = (TextView) view.findViewById(R.id.report_question);
        Log.d("TEXTTTT", String.valueOf(report));
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ReportAPI(user_id, al_id.get(index));
//                Toast.makeText(getActivity(), "Question Reported", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Report This Question:");

                // Set up the input
//                final EditText input = new EditText(getContext());

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ReportAPI(user_id, al_id.get(index));
                        Toast.makeText(getActivity(), "Question Reported", Toast.LENGTH_SHORT).show();

                        flingContainer.getTopCardListener().selectLeft();

                        report_flag = 0;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        ButterKnife.inject(getActivity());
        return view;
    }


    public void ReportAPI(String userid, String questionid){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", userid);
        requestParams.put("qid", questionid);

        client.post(getActivity(),REPORT_REQUEST_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("REPORT Success",response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("REPORT Error",errorResponse.toString());
            }
        });
    }


    public void AddQuestion(String userid,String questionid, String questiontext, String questionimage, String questioncorrectanswer, String questionpoints, String answercorrectness){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("questionid", questionid);
        requestParams.put("questiontext", questiontext);
        requestParams.put("questionimage", questionimage);
        requestParams.put("questioncorrectanswer", questioncorrectanswer);
        requestParams.put("questionpoints", questionpoints);
        requestParams.put("answercorrectness", answercorrectness);

        client.post(getActivity(), Config.AddQuestionUrl , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("ResponsePoint Success",response.toString());            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }

        });
    }

    public void AddUserToQuestion(String uid,String qid, String questioncorrectanswer){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", uid);
        requestParams.put("qid", qid);
        requestParams.put("answer", questioncorrectanswer);

        client.post(getActivity(), Config.AddQuestionToUser, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }
        });
    }

    public void getQuestions(String QUESTIONS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), QUESTIONS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

//                if(another_flag==1){
//
//                    arrayAdapter.cl
//                    al_id.clear();
//                    al_correctAns.clear();
//                }

                try {
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNews = responseArray.getJSONObject(i);

                        String text = currentNews.getString("text");

                        String level = currentNews.getString("level");

                        String id = currentNews.getString("_id");

                        String correctAnswer = currentNews.getString("correctAnswere");

                        al_id.add(id);
                        al.add(text);
                        al_correctAns.add(correctAnswer);

                    }

                } catch (JSONException e) {

                    Log.e("QueryUtils", "Problem parsing the Questions JSON results", e);
                }

                for(int i=0;i<al.size();i++){

                    Log.e("al: ",al.get(i));
                }

                if(another_flag == 1) {

                    al.remove(0);
                    al_id.remove(0);
                    al_correctAns.remove(0);

                    arrayAdapter.notifyDataSetChanged();
//
//                    al.remove(0);
//                    al_id.remove(0);
//                    al_correctAns.remove(0);
//
//                    arrayAdapter.notifyDataSetChanged();
//
                }
                flag=10;
                another_flag = 1;

                flingContainer.setAdapter(arrayAdapter);

                Button button_left = (Button) view.findViewById(R.id.left);
                button_left.setBackgroundResource(R.drawable.false_btn);
                button_left.setEnabled(true);

                Button button_right = (Button) view.findViewById(R.id.right);
                button_right.setBackgroundResource(R.drawable.true_btn);
                button_right.setEnabled(true);

                TextView report = (TextView) view.findViewById(R.id.report_question);
                report.setEnabled(true);

                for(int i=0;i<al.size();i++){

                    Log.e("al_1: ",al.get(i));
                }

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}