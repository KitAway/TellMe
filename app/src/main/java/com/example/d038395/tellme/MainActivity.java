package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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


public class MainActivity extends Activity {

    private static String appPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Topic.storeResult();
        super.onStop();
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
