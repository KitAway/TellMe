package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Explode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static String appPath;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
        if (v.getId() == R.id.sign_out_button) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
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
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            showSignedOutUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        appPath= Environment.getExternalStorageDirectory().getPath()+ File.separator+getString(R.string.app_name)+File.separator;
        File app_path= new File(appPath);
        if(!app_path.isDirectory()){
            if(!app_path.mkdirs())
                Toast.makeText(this,"failed to create directory",Toast.LENGTH_LONG).show();
        }

        Questions.setApp_path(appPath);
        Topic.initialize(appPath);


    }

    @Override
    protected void onStop() {
        super.onStop();
        Topic.storeResult();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemId=item.getItemId();
//        String [] menuItem = getResources().getStringArray(R.array.arrayTopicContextMenu);
//        String menuItemName=menuItem[menuItemId];
        int topicId= info.position;
        switch (menuItemId) {
            case 0:
                recordTopic(topicId);
                break;
            case 1:
                listenTopic(topicId);
                break;
            case 2:
                viewQuestion(topicId);
                break;
            case 3:
                removeTopic(topicId);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_addTopic:
                addTopicDialog();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recordTopic(int topicId){
        Intent intent = new Intent(this,Recording.class);
        intent.putExtra("TopicId",topicId);
        startActivity(intent);
    }
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
    


    private void viewQuestion(int topicId){
        Intent intent = new Intent(this,QuestionActivity.class);
        intent.putExtra("TopicId",topicId);
        startActivity(intent);
    }
    private void addTopicDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_addtopic,null);
        final EditText editText =(EditText) dialogView.findViewById(R.id.et_topicName);
        builder.setTitle("Add new Topic")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Topic.addTopic(editText.getText().toString());
                        refreshListView();
                    }
                })
                .setNegativeButton(R.string.cancel,null);
        dialog=builder.create();
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    dialog.getWindow().
                            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        dialog.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()){
            case R.id.lv_topic:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle(Topic.getTopicList().get(info.position).toString());
                String [] menuItem = getResources().getStringArray(R.array.arrayTopicContextMenu);
                for (int i=0; i < menuItem.length;i++){
                    menu.add(Menu.NONE,i,0,menuItem[i]);
                }
                break;
            default:
                break;
        }
    }
}
