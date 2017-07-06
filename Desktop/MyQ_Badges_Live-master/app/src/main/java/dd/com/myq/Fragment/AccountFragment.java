package dd.com.myq.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import dd.com.myq.Activity.Badges;
import dd.com.myq.Activity.ChangePassword;
import dd.com.myq.Activity.EditProf;
import dd.com.myq.Activity.LoginActivity;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

public class AccountFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public ListView settingsList;
    Context context;
    private int selectedIndex;
    SessionManager sessionManager;
    SharedPreferences.Editor editor;
    Context _context;

    private OnFragmentInteractionListener mListener;


    public AccountFragment() {

    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        String [] option_names={"Edit Profile","Change Password", "Badges", "Logout"};
        settingsList = (ListView) view.findViewById(R.id.settings_list);
        settingsList.setClickable(true);
        settingsList.setAdapter(new AccountSettingAdapter(getActivity(),option_names));



        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test",""+position);

                switch (position){
                    case 0:
                        Intent i = new Intent(getActivity(), EditProf.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1 = new Intent(getActivity(), ChangePassword.class);
                        startActivity(i1);
                        break;

                    case 2:
                        Intent i2 = new Intent(getActivity(), Badges.class);
                        startActivity(i2);
                        break;

                    case 3:

                        SharedPreferences sp = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                        sp.edit().clear().commit();

                        Intent loginscreen=new Intent(getContext(),LoginActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginscreen);
                        getActivity().finish();
                        SessionManager.logoutUser();
                        }

            }
        });

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