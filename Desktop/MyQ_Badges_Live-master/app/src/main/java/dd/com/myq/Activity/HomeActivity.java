package dd.com.myq.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import dd.com.myq.Fragment.AccountFragment;
import dd.com.myq.Fragment.FriendFragment;
import dd.com.myq.Fragment.GroupsFragment;
import dd.com.myq.Fragment.HomeFragment;
import dd.com.myq.Fragment.PointFragment;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements AccountFragment.OnFragmentInteractionListener,
        FriendFragment.OnFragmentInteractionListener, PointFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener {

    public static String TAG = "Home";
    private RelativeLayout Home, Point, Account, Friends, Levels, Categories, Groups;
    private SessionManager sessionManager;
    private TextView UserName;
    private CircleImageView ProfilePicture;

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public int Hflag;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FacebookSdk.sdkInitialize(getApplicationContext());

        if((new LoginActivity()).fb_flag == 1 || Hflag==1){

            Hflag = 1;
        }

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetails();

        UserName = (TextView) findViewById(R.id.user_name);
        ProfilePicture = (CircleImageView) findViewById(R.id.profile_picture);
        Home = (RelativeLayout) findViewById(R.id.home_tab);
        Point = (RelativeLayout) findViewById(R.id.points_tab);
        Account = (RelativeLayout) findViewById(R.id.account_tab);
        Friends = (RelativeLayout) findViewById(R.id.friends_tab);
        Levels = (RelativeLayout) findViewById(R.id.level_container);
        Categories = (RelativeLayout) findViewById(R.id.category_container);
        Groups = (RelativeLayout) findViewById(R.id.groups_tab);


/////////////////////
        final String level = getIntent().getStringExtra("level");
        final String category = getIntent().getStringExtra("category");
/////////////////////


        UserName.setText(user.get(SessionManager.KEY_USERNAME));
        Levels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Friends.setClickable(true);


                Intent i = new Intent(getApplicationContext(), Levels.class);
                startActivity(i);
            }
        });
        Categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Friends.setClickable(true);

                Intent i = new Intent(getApplicationContext(), Categories.class);
                startActivity(i);

            }
        });

        final Levels l=new Levels();

        Point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Friends.setClickable(true);
                TAG = "Point";

                Fragment fragment = new PointFragment();
                FragmentManager manager = getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });


        Groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Friends.setClickable(true);
                TAG = "Groups";

                Fragment fragment = new GroupsFragment();
                FragmentManager manager = getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });


        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Friends.setClickable(true);
                TAG = "Account";

                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    getSupportFragmentManager().popBackStack();
                }

                Fragment fragment = new AccountFragment();
                FragmentManager manager = getSupportFragmentManager();
//
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        Friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = "Friends";

                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    getSupportFragmentManager().popBackStack();
                }

                Friends.setClickable(false);

                Fragment fragment = new FriendFragment();
                FragmentManager manager = getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();

                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                transaction.replace(R.id.main_container, fragment, TAG);

                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });
        Home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Friends.setClickable(true);

                TAG = "Home";
//
                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    getSupportFragmentManager().popBackStack();
                }
                Fragment fragment = new HomeFragment();
                FragmentManager manager = getSupportFragmentManager();

                manager.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction transaction = manager.beginTransaction();

                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Fragment fragment = new HomeFragment();
        fragment.setRetainInstance(true);
        FragmentManager manager = this.getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        transaction.replace(R.id.main_container, fragment, TAG);
        transaction.addToBackStack(null);

        Bundle bundle1 = new Bundle();
        bundle1.putString("levels", level);
        bundle1.putString("categories", category);
        fragment.setArguments(bundle1);

        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        finishAffinity();
        finish();

    }

}