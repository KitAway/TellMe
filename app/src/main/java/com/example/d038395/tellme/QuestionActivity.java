package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class QuestionActivity extends Activity {

    Topic topic=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent=getIntent();
        int topicId = intent.getIntExtra("TopicId", 0);
        topic=Topic.getTopicList().get(topicId);
        refreshQuestionList();

        ListView listView=(ListView)findViewById(R.id.lv_question);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()){
            case R.id.lv_question:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle(topic.getQuestionList().get(info.position).toString());
                String [] menuItem = getResources().getStringArray(R.array.arrayQuestionContextMenu);
                for (int i=0; i < menuItem.length;i++){
                    menu.add(Menu.NONE,i,0,menuItem[i]);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemId=item.getItemId();
        String [] menuItem = getResources().getStringArray(R.array.arrayQuestionContextMenu);
        String menuItemName=menuItem[menuItemId];
        int questionId= info.position;
        switch (menuItemId) {
            case 0:
                Questions question=topic.getQuestionList().get(menuItemId);
                String audioPath=question.getPath();
                if(audioPath!=null)
                    new AudioPlayer(this,audioPath,
                            topic.getQuestionList().get(questionId).toString()).play();
                else Toast.makeText(this, "No record exists.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
        return true;
    }

    private void refreshQuestionList(){
        ListView listView = (ListView)findViewById(R.id.lv_question);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.question_list, topic.getQuestionList());
        listView.setAdapter(arrayAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    protected void onStop() {
        Topic.storeResult();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_addQuestion:
                addQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addQuestion(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_addtopic,null);
        final EditText editText =(EditText) dialogView.findViewById(R.id.et_topicName);
        builder.setTitle("Add new Question")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        topic.addQuestion(new Questions(editText.getText().toString()));
                        refreshQuestionList();
                    }
                })
                .setNegativeButton(R.string.cancel,null);
        builder.create().show();
    }
}
