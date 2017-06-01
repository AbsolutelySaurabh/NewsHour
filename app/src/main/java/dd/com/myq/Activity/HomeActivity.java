package dd.com.myq.Activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.w3c.dom.Text;

import java.util.HashMap;

import dd.com.myq.App.Config;
import dd.com.myq.Fragment.AccountFragment;
import dd.com.myq.Fragment.FriendFragment;
import dd.com.myq.Fragment.HomeFragment;
import dd.com.myq.Fragment.PointFragment;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements AccountFragment.OnFragmentInteractionListener, FriendFragment.OnFragmentInteractionListener, PointFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener {

    public static String TAG = "Home";
    private RelativeLayout Home, Point, Account, Friends, Levels, Categories;
    private SessionManager sessionManager;
    private TextView UserName;
    private CircleImageView ProfilePicture;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        UserName.setText(user.get(SessionManager.KEY_USERNAME));

        imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).build();

        imageLoader.displayImage(Config.defaultImagePrefix+ user.get(SessionManager.KEY_PROFILE_PIC), ProfilePicture, options);

        Levels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });

        Categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });

        Point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = "Point";
                getSupportFragmentManager().popBackStackImmediate ();
                Fragment fragment = new PointFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = "Account";
                getSupportFragmentManager().popBackStackImmediate ();
                Fragment fragment = new AccountFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = "Friends";
                getSupportFragmentManager().popBackStackImmediate ();
                Fragment fragment = new FriendFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAG = "Home";
                getSupportFragmentManager().popBackStackImmediate ();
                Fragment fragment = new HomeFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.main_container, fragment, TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        Fragment fragment = new HomeFragment();
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        transaction.replace(R.id.main_container, fragment, TAG);
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
