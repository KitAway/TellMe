package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class QuestionActivity extends Activity {

    Topic topic=null;
    int topicId;
    MediaRecorder mRecorder;
    boolean Recording;
    ImageButton imageButton;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent=getIntent();
        topicId = intent.getIntExtra("TopicId", 0);
        topic=Topic.getTopicList().get(topicId);
        refreshQuestionList();

        imageButton= (ImageButton)findViewById(R.id.Ib_record);
        imageButton.setEnabled(false);

        ((TextView)findViewById(R.id.tx_topic_question)).setText("Topic:"+topic.getTopic());
        textView = (TextView) findViewById(R.id.tx_tip);
        textView.setEnabled(false);
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
//        String [] menuItem = getResources().getStringArray(R.array.arrayQuestionContextMenu);
//        String menuItemName=menuItem[menuItemId];
        final int questionId= info.position;
        Questions question=topic.getQuestionList().get(questionId);
        switch (menuItemId) {
            case 0:
                File audioPath=question.getPath(this);
                if(audioPath!=null)
                    new AudioPlayer(this,audioPath,
                            question.toString()).play();
                else Toast.makeText(this, "No record exists.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                audioPath=question.getPath(this);
                if(audioPath!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Answer exists, do you want to change the answer?")
                            .setTitle("Warning")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    recordAnswer(questionId);
                                }
                            })
                            .setNegativeButton(R.string.cancel,null)
                            .create().show();
                }
                else recordAnswer(questionId);
                break;
            case 2:
                removeQuestion(questionId);
                break;
            default:
                break;
        }
        return true;
    }

    private void recordAnswer(final int questionsId){
//        Intent intent = new Intent(this,Recording.class);
//        intent.putExtra("QuestionId",questionsId);
//        intent.putExtra("TopicId",topicId);
//        startActivity(intent);

        textView.setEnabled(true);
        imageButton.setEnabled(true);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!Recording) {
                            startRecording(topic.getQuestionList().get(questionsId));
                            Recording = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(Recording) {
                            stopRecording();
                            imageButton.setEnabled(false);
                            textView.setEnabled(false);
                            Recording = false;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void startRecording(Questions questions){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        String filename=questions.getFilename();
        if(filename==null) {
            filename = UUID.randomUUID().toString();
            questions.setFilename(filename);
        }
        questions.setFilename(filename);
        mRecorder.setOutputFile(questions.getPath(this).getPath());
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(44100);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch ( IOException e) {
            e.printStackTrace();
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording(){
        mRecorder.stop();
        mRecorder.release();
    }

    private void removeQuestion(final int questionId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this question?")
                .setTitle("Warning")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        topic.getQuestionList().remove(questionId);
                        refreshQuestionList();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .create().show();
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
        Topic.storeResult(this);
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
        final AlertDialog dialog;
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
        dialog=builder.create();
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    dialog.getWindow().
                            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        dialog.show();
    }
}
