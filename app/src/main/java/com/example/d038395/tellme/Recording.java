package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;


public class Recording extends Activity {

    Questions questions;
    MediaRecorder mRecorder;
    String filename;
    boolean Recording;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        Intent intent = getIntent();
        Topic topic = Topic.getTopicList().get(intent.getIntExtra("TopicId",0));
        questions = topic.getQuestionList().get(intent.getIntExtra("QuestionId",0));

        filename=questions.getFilename();
        if(filename==null) {
            filename = UUID.randomUUID().toString();
            questions.setFilename(filename);
        }

        Recording=false;
        ImageButton imageButton= (ImageButton)findViewById(R.id.Ibtn_record);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!Recording) {
                            startRecording();
                            Recording = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(Recording) {
                            stopRecording();
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

    @Override
    protected void onStop() {
        Topic.storeResult();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_recording, menu);
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

    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        questions.setFilename(filename);
        mRecorder.setOutputFile(questions.getPath());
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
}
