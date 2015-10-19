package com.example.d038395.tellme;

import android.app.Activity;
import android.content.Intent;
<<<<<<< HEAD
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
=======
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Explode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
>>>>>>> parent of b8ff507... add google login option_not finished
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

<<<<<<< HEAD
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.io.File;
=======
import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

>>>>>>> parent of b8ff507... add google login option_not finished

public class MainActivity extends Activity {

<<<<<<< HEAD


    static File appPath;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private static final int BK_Topic = 1;

    private String TAG="";
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


    @Override
    public void onConnected(Bundle bundle) {
// onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        showSignedInUI();
    }

    private void showSignedInUI()
    {
        Intent i = new Intent(this,TopicPanel.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        switch (requestCode){
            case RC_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    mShouldResolve = false;
                }

                mIsResolving = false;
                mGoogleApiClient.connect();
                break;
            case BK_Topic:
                mGoogleApiClient.disconnect();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
        if (v.getId() == R.id.sign_out_button) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
       // mStatusTextView.setText(R.string.signing_in);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
                Toast.makeText(this,connectionResult.toString(),Toast.LENGTH_LONG).show();
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
            Toast.makeText(this,"Signed out",Toast.LENGTH_LONG).show();
        }
    }

=======
    private static String appPath;
>>>>>>> parent of b8ff507... add google login option_not finished
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state) &&
                !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            return;
        appPath= getExternalFilesDir(Environment.DIRECTORY_MUSIC);


//        credential=GoogleAccountCredential.usingAudience(this, Arrays.asList(DriveScopes.DRIVE);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .build();

    }

    @Override
    public void onConnectionSuspended(int i) {
=======
        appPath= Environment.getExternalStorageDirectory().getPath()+ File.separator+getString(R.string.app_name)+File.separator;
        File app_path= new File(appPath);
        if(!app_path.isDirectory()){
            if(!app_path.mkdirs())
                Toast.makeText(this,"failed to create directory",Toast.LENGTH_LONG).show();
        }

        Questions.setApp_path(appPath);
        Topic.initialize(appPath);

        if (Topic.firstRun){
            Topic first= new Topic("Unforgettable trip");
            Topic second = new Topic("First lesson in primary school");
            Topic third = new Topic("My childhood");

            first.addQuestion(new Questions("When was the trip?"));
            first.addQuestion(new Questions("Who did you go with?"));
            first.addQuestion(new Questions("Where did you go?"));
            first.addQuestion(new Questions("Could you tell us more details about this trip?"));

            second.addQuestion(new Questions("Who was the teacher?"));
            second.addQuestion(new Questions("What's the topic?"));
            second.addQuestion(new Questions("Could you tell us more details about this lesson?"));

            third.addQuestion(new Questions("Where did you stay when you were a child?"));
            third.addQuestion(new Questions("Could you tell us more details about your childhood?"));
>>>>>>> parent of b8ff507... add google login option_not finished

            first.addTopic();
            second.addTopic();
            third.addTopic();
        }

        refreshListView();
        ListView listView = (ListView)findViewById(R.id.lv_topic);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
    }

    @Override
    protected void onStop() {
<<<<<<< HEAD
        super.onStop();
        Topic.storeResult(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Topic.initialize(this);
        mGoogleApiClient.connect();

=======
        Topic.storeResult();
        super.onStop();
>>>>>>> parent of b8ff507... add google login option_not finished
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
<<<<<<< HEAD
=======
    private void listenTopic(int topicId){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            getWindow().setExitTransition(new Explode());
        Intent intent = new Intent(this,Listen2Topic.class);
        intent.putExtra("TopicId",topicId);
        startActivity(intent);
    }
    private void removeTopic(final int topicId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this topic?")
                .setTitle("Warning")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Topic.removeTopic(topicId);
                        refreshListView();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .create().show();
    }
    private void refreshListView(){
        ListView listView = (ListView)findViewById(R.id.lv_topic);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.topic_list, Topic.getTopicList());
        listView.setAdapter(arrayAdapter);
    }
>>>>>>> parent of b8ff507... add google login option_not finished



}
