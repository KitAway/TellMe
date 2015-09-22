package com.example.d038395.tellme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

public class Recording extends Activity {

    MediaRecorder mRecorder;
    Iterator<Questions> iterator;
    TextToSpeech tts;
    VideoView videoView;
    boolean Recording;
    ImageButton imageButton;
    Questions questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        Intent intent = getIntent();
        Topic topic = Topic.getTopicList().get(intent.getIntExtra("TopicId", 0));
        ((TextView) findViewById(R.id.tx_topic)).setText("Topic:"+topic.getTopic());
        ArrayList<Questions> questionsArrayList=topic.getQuestionList();
        iterator  = questionsArrayList.iterator();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    start();
                }
            }
        });

        videoView=(VideoView) findViewById(R.id.vv_topic);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
        Uri uri =Uri.parse("android.resource://" + getPackageName() +'/'+ R.raw.animation_speaking);
        videoView.setVideoURI(uri);

        imageButton= (ImageButton) findViewById(R.id.btn_record);
    }


    private void start(){
        if(iterator.hasNext()) {
            questions=iterator.next();
            playQuestion();
        }
    }

    private void playQuestion(){
        ((TextView)findViewById(R.id.tx_question_record)).setText(questions.getQuestion());
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                videoView.start();
            }

            @Override
            public void onDone(String utteranceId) {
                videoView.pause();
                imageButton.setEnabled(true);
                recording();
            }

            @Override
            public void onError(String utteranceId) {
                Toast.makeText(Recording.this, "TTs failure.", Toast.LENGTH_SHORT).show();
            }
        });


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            tts.speak(questions.getQuestion(),TextToSpeech.QUEUE_ADD,hashMap);
        }
        else {
            tts.speak(questions.getQuestion(), TextToSpeech.QUEUE_ADD,
                    null, questions.getQuestion());
        }
    }

    private void recording(){
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!Recording) {
                            Recording = true;
                            startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Recording) {
                            Recording = false;
                            stopRecording();
                            imageButton.setEnabled(false);
                            if(iterator.hasNext()) {
                                questions=iterator.next();
                                playQuestion();
                            }
                            else  finish();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onStop() {
        Topic.storeResult();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(tts!=null)
            tts.shutdown();
        super.onDestroy();
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
                String filename=questions.getFilename();
                if(filename==null) {
                    filename = UUID.randomUUID().toString();
                    questions.setFilename(filename);
                }
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
