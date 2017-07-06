package dd.com.myq.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.Activity.GroupActivity;
import dd.com.myq.Fragment.Groups.Group;
import dd.com.myq.Fragment.Groups.GroupAdapter;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 */
public class GroupsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView;
    public int flag=0;
    private String m_Text = "";
    public static int eligible;

    public  ArrayList<String> al_group_name = new ArrayList<String>();
    public  ArrayList<String> al_group_members_count = new ArrayList<String>();
    public  ArrayList<String> al_group_since = new ArrayList<String>();
    public  ArrayList<String> al_group_id = new ArrayList<String>();

    ArrayList<Group> groups;
    ListView listView;
    private GroupAdapter groupAdapter;

    ProgressDialog progress;

    //    SessionManager currentSession;
    public String REQUEST_GET_GROUPS = "http://myish.com:10011/api/groups";
    public String REQUEST_ELIGIBLE_GROUPS = "http://myish.com:10011/api/eligiblegroups/";
    public String UPDATE_MEMBERS = "http://myish.com:10011/api/updatemembers/";

    private OnFragmentInteractionListener mListener;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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

        rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        SessionManager currentSession = new SessionManager(getActivity());

        listView=(ListView)rootView.findViewById(R.id.list_groups);

        Log.e("GET GROUPS : ", REQUEST_GET_GROUPS);
        HashMap<String, String> user_details = currentSession.getUserDetails();
        String user_id = user_details.get(SessionManager.KEY_UID);
        REQUEST_ELIGIBLE_GROUPS = REQUEST_ELIGIBLE_GROUPS + user_id;

        progress = new ProgressDialog(getContext(),  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Groups :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        groups= new ArrayList<>();

        if(flag==0) {

            getGroups(REQUEST_GET_GROUPS);
        }

        checkEligibility(REQUEST_ELIGIBLE_GROUPS);

        return rootView;
    }

    public void checkEligibility(String REQUEST_ELIGIBLE_GROUPS) {

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getActivity(), REQUEST_ELIGIBLE_GROUPS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {

                try {

                    int status = (object.getInt("status"));

                    if(status==0){

                        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.create_group_layout);
                        layout.setVisibility(View.VISIBLE);

                        layout.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {


                                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.create_group_layout);
                                layout.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Answer 100 questions first !", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else {

                        if (status == 1) {

                            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.create_group_layout);
                            layout.setVisibility(View.VISIBLE);

                            layout.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Group Name:");

                                    // Set up the input
                                    final EditText input = new EditText(getContext());

                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(input);

                                    // Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            m_Text = m_Text + input.getText().toString();

                                            Log.e("groupname", input.getText().toString());


                                            PostGroup(input.getText().toString(), "123");


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

                        }
                    }

                } catch (JSONException e) {

                    Log.e("QueryUtils", "Problem parsing the friends JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void PostGroup(String groupname, String groupimage){

        AsyncHttpClient client = new AsyncHttpClient();

        Log.e("groupname", groupname);

        RequestParams requestParams = new RequestParams();
        requestParams.put("groupname", groupname);
        requestParams.put("groupimage", groupimage);

        client.post(getActivity(), REQUEST_GET_GROUPS, requestParams, new JsonHttpResponseHandler() {
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


    public void getGroups(String REQUEST_GET_GROUPS){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), REQUEST_GET_GROUPS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                if(flag==1){
                    groupAdapter.clear();
                    al_group_name.clear();
                    al_group_members_count.clear();
                    al_group_since.clear();
                    al_group_id.clear();
                }
                try {
                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject object = responseArray.getJSONObject(i);

                        String group_name = object.getString("groupname");
                        String group_id = object.getString("_id");
                        String count = String.valueOf(object.getInt("totalmembers"));
                        String since = object.getString("timestamp");

                        String date = "";
                        for(int j=0;j<10;j++){

                            date+=since.charAt(j);
                        }
                        al_group_name.add(group_name);
                        al_group_members_count.add(count);
                        al_group_id.add(group_id);
                        al_group_since.add(date);

                    }
//                    progress.hide();
                    progress.dismiss();


                    for (int i = 0; i < al_group_id.size(); i++) {

                        groups.add(new Group(al_group_name.get(i), al_group_members_count.get(i), al_group_since.get(i)));

                    }
                    groupAdapter = new GroupAdapter(groups, getContext());
                    listView.setAdapter(groupAdapter);
                    flag=1;
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                            UPDATE_MEMBERS = UPDATE_MEMBERS + al_group_id.get(position);
                            updateGroupMembers(UPDATE_MEMBERS);

                            Intent i = new Intent(getContext(), GroupActivity.class);
                            i.putExtra("gp_id", al_group_id.get(position));

                            Log.e("group id CKICKK  : ", al_group_id.get(position));

                            startActivity(i);

                        }
                    });

                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the friends JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progress.dismiss();

                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateGroupMembers(String UPDATE_MEMBERS){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getActivity(), UPDATE_MEMBERS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                //No GET response
                Log.e("Fragment onSuccess : ", String.valueOf(object));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                super.onFailure(statusCode, headers, throwable, object);
                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        updateGroupMembers(UPDATE_MEMBERS);

        if (flag==1) {

            progress.show();

            groupAdapter.clear();

            al_group_name.clear();
            al_group_members_count.clear();
            al_group_since.clear();
            al_group_id.clear();

            updateGroupMembers(UPDATE_MEMBERS);
            getGroups(REQUEST_GET_GROUPS);

        }
    }
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
        void onFragmentInteraction(Uri uri);
    }
}