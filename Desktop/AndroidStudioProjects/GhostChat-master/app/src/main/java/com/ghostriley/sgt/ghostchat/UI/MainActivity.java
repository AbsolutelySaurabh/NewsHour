package com.ghostriley.sgt.ghostchat.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ghostriley.sgt.ghostchat.utils.ParseConstants;
import com.ghostriley.sgt.ghostchat.R;
import com.ghostriley.sgt.ghostchat.adapters.MessageAdapter;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    public static final String TAG=MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST=0;
    public static final int TAKE_VIDEO_REQUEST=1;
    public static final int PICK_PHOTO_REQUEST=2;
    public static final int PICK_VIDEO_REQUEST=3;
    public static final int MEDIA_TYPE_IMAGE=4;
    public static final int MEDIA_TYPE_VIDEO=5;
    public static final int FILE_SIZE_LIMIT=1024*1024*10; //10 MB


    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener=new DialogInterface.OnClickListener() {
        @Override
    public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case 0:
                    Intent takePhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null) {
                        Toast.makeText(MainActivity.this, "There was a problem accessing external storage", Toast.LENGTH_LONG).show();
                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;

                case 1:
                    Intent videoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri == null) {
                        Toast.makeText(MainActivity.this, "There was a problem accessing external storage", Toast.LENGTH_LONG).show();
                    }
                    else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }

                    break;

                case 2:
                    Intent choosePhotoIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;

                case 3:
                    Intent chooseVideoIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, "Please select a video which is less than 10MB", Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;

            }
        }

        private Uri getOutputMediaFileUri(int mediaType){
            String appName=getString(R.string.app_name);
            if(isExternalStorageAvailable()){
                File mediaStorageDir=new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

                if(!mediaStorageDir.exists()){
                   if(!mediaStorageDir.mkdir()) {
                       Log.e(TAG, "Failed to create directory");
                   }
                }
                File mediaFile;
                java.util.Date now=new java.util.Date();
                String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
                String path=mediaStorageDir.getPath()+File.separator;

                if (mediaType==MEDIA_TYPE_IMAGE) {
                    mediaFile=new File(path+"IMG_"+timestamp+".jpg");
                }
                else if (mediaType==MEDIA_TYPE_VIDEO) {
                    mediaFile=new File(path+"VID_"+timestamp+".mp4");
                }
                else {
                    return null;
                }

                Log.d(TAG, "File: "+Uri.fromFile(mediaFile));
                return Uri.fromFile(mediaFile);
            }
            else {
                return null;
            }
        }

        private boolean isExternalStorageAvailable() {
            String state= Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED))
                return true;
            else return false;
        }
    };

    MessageAdapter.SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser=ParseUser.getCurrentUser();
        // Set up the action bar.
        if(currentUser==null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else{
            Log.i(TAG, currentUser.getUsername());
        }


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new MessageAdapter.SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK) {

            if(requestCode==PICK_PHOTO_REQUEST || requestCode==PICK_VIDEO_REQUEST) {
                if(data==null) {
                    Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri=data.getData();
                }



                Log.i(TAG, "Media Uri: "+mMediaUri);
                if(requestCode==PICK_VIDEO_REQUEST) {
                    int fileSize;
                    InputStream inputStream = null;

                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) { /* Intentionally blank */ }
                    }

                    if(fileSize>=FILE_SIZE_LIMIT) {
                        Toast.makeText(this, "File is too large!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            String fileType;

            if (requestCode==PICK_PHOTO_REQUEST || requestCode==TAKE_PHOTO_REQUEST) {
                fileType= ParseConstants.TYPE_IMAGE;
            }
            else {
                fileType=ParseConstants.TYPE_VIDEO;
            }

            Intent recipientsIntent=new Intent(this, ChooseRecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);
        }

        else if(resultCode!=RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.action_logout:
                ParseUser.logOut();
                Intent intent=new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.action_edit_friends:
                Intent intent2 = new Intent(this, EditFriendsActivity.class);
                startActivity(intent2);
                break;

            case R.id.action_camera:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dailog=builder.create();
                dailog.show();
                break;

            case R.id.action_refresh_inbox:
                //refresh inbox
                Intent intent1=new Intent(this, MainActivity.class);
                startActivity(intent1);
                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_about:
                Intent intent3=new Intent(this, AboutActivity.class);
                startActivity(intent3);
                break;

            case R.id.action_change_password:
                Intent intent4=new Intent(this, ChangePasswordActivity.class);
                startActivity(intent4);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

    }

}
